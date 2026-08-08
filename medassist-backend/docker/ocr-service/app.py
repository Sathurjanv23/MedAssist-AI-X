"""
OCR Microservice for MedAssist AI X
Tesseract-based text extraction from medical documents
Supports: English, Tamil (tam), Sinhala (sin)
"""
from flask import Flask, request, jsonify
import pytesseract
from PIL import Image
import fitz  # PyMuPDF — for PDF → image conversion
import base64
import io
import requests
import logging

app = Flask(__name__)
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


def pdf_to_images(pdf_bytes: bytes) -> list:
    """Convert all pages of a PDF to PIL images."""
    doc = fitz.open(stream=pdf_bytes, filetype="pdf")
    images = []
    for page_num in range(len(doc)):
        page = doc[page_num]
        mat = fitz.Matrix(2, 2)   # 2x zoom for better OCR quality
        pix = page.get_pixmap(matrix=mat)
        img = Image.frombytes("RGB", [pix.width, pix.height], pix.samples)
        images.append(img)
    return images


def extract_text_from_image(image: Image, language: str = "eng") -> dict:
    """Extract text using Tesseract with confidence scoring."""
    config = f"--oem 3 --psm 6 -l {language}"
    try:
        text = pytesseract.image_to_string(image, config=config)
        # Get word-level confidence data
        data = pytesseract.image_to_data(image, config=config, output_type=pytesseract.Output.DICT)
        confidences = [c for c in data['conf'] if c != -1]
        avg_confidence = sum(confidences) / len(confidences) if confidences else 0
        return {
            "text": text.strip(),
            "confidence": round(avg_confidence / 100, 3)  # normalize to 0-1
        }
    except Exception as e:
        logger.error(f"OCR error: {e}")
        return {"text": "", "confidence": 0.0}


@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "healthy", "service": "medassist-ocr"})


@app.route("/extract", methods=["POST"])
def extract():
    """Extract text from a base64-encoded image or PDF."""
    data = request.get_json()
    language = data.get("language", "eng")
    document_type = data.get("documentType", "GENERAL")

    if not data.get("imageBase64"):
        return jsonify({"success": False, "error": "No image data provided"}), 400

    try:
        file_bytes = base64.b64decode(data["imageBase64"])
        all_text = []
        total_confidence = 0.0

        # Detect if PDF or image by checking magic bytes
        if file_bytes[:4] == b"%PDF":
            images = pdf_to_images(file_bytes)
            for img in images:
                result = extract_text_from_image(img, language)
                if result["text"]:
                    all_text.append(result["text"])
                    total_confidence += result["confidence"]
            avg_confidence = total_confidence / len(images) if images else 0
        else:
            img = Image.open(io.BytesIO(file_bytes))
            result = extract_text_from_image(img, language)
            all_text.append(result["text"])
            avg_confidence = result["confidence"]

        full_text = "\n\n".join(filter(None, all_text))

        return jsonify({
            "success": True,
            "rawText": full_text,
            "confidence": round(avg_confidence, 3),
            "entities": extract_entities(full_text, document_type),
            "warnings": [] if full_text else ["No text detected in document"]
        })

    except Exception as e:
        logger.error(f"Extraction error: {e}")
        return jsonify({"success": False, "error": str(e)}), 500


@app.route("/extract-url", methods=["POST"])
def extract_url():
    """Download file from URL and extract text."""
    data = request.get_json()
    url = data.get("imageUrl")
    language = data.get("language", "eng")

    if not url:
        return jsonify({"success": False, "error": "No URL provided"}), 400

    try:
        response = requests.get(url, timeout=30)
        file_bytes = response.content
        encoded = base64.b64encode(file_bytes).decode()
        data["imageBase64"] = encoded
        request._cached_json = (data, data)
        return extract()
    except Exception as e:
        return jsonify({"success": False, "error": str(e)}), 500


def extract_entities(text: str, doc_type: str) -> dict:
    """Simple entity extraction — looks for lab values, dates, etc."""
    import re
    entities = {}

    # Extract dates (common medical report date formats)
    dates = re.findall(r'\b\d{1,2}[/-]\d{1,2}[/-]\d{2,4}\b', text)
    if dates:
        entities["dates"] = dates[:5]

    # Extract numeric values with units (lab values)
    lab_values = re.findall(r'(\w[\w\s]+)\s*[:=]\s*(\d+\.?\d*)\s*(mg/dL|g/dL|%|mmol/L|U/L|IU/L|mEq/L)?', text)
    if lab_values:
        entities["labValues"] = [{"name": v[0].strip(), "value": v[1], "unit": v[2]} for v in lab_values[:20]]

    return entities


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5001, debug=False)

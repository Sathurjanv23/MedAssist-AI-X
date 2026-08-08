FROM python:3.11-slim

# Install Tesseract OCR + language packs (English, Tamil, Sinhala)
RUN apt-get update && apt-get install -y \
    tesseract-ocr \
    tesseract-ocr-eng \
    tesseract-ocr-tam \
    tesseract-ocr-sin \
    libglib2.0-0 \
    libgl1-mesa-glx \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY app.py .

EXPOSE 5001

HEALTHCHECK --interval=30s --timeout=5s CMD curl -f http://localhost:5001/health || exit 1

CMD ["python", "app.py"]

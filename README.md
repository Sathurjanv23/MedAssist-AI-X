# MedAssist AI X 🩺🤖

### Your Personal AI Healthcare Operating System

MedAssist AI X is an AI-powered HealthTech platform designed to help individuals understand, organize and manage their health information through intelligent AI services.

The platform combines AI-assisted health analysis, medical report OCR, health tracking, medication management, conversational AI and a personalized AI Health Twin into one unified healthcare experience.

---

## 🚀 Vision

Healthcare information is often fragmented across medical reports, prescriptions, medicines, health records and personal lifestyle data.

MedAssist AI X aims to bring these pieces together into a single intelligent healthcare platform.

The system helps users:

- Understand medical reports
- Track health information
- Manage medicines
- Interact with an AI health assistant
- Visualize their health journey
- Receive personalized health insights
- Identify potential health risks
- Prepare questions for healthcare professionals

> MedAssist AI X is an AI-assisted healthcare platform and does not replace professional medical diagnosis or treatment.

---

# ✨ Core Features

## 🤖 AI Healthcare Assistant

- Conversational AI
- Context-aware health conversations
- Personalized responses
- Health-related question answering
- Safety-aware AI responses
- AI conversation memory

## 🧠 AI Health Twin

A personalized digital health representation built from available user health information.

Features include:

- Health score
- Health trends
- Lifestyle indicators
- Risk indicators
- Vital statistics
- Personalized recommendations

## 📄 Medical Report Intelligence

Users can upload medical documents and receive AI-assisted analysis.

Pipeline:

Upload
→ File Validation
→ OCR
→ Text Extraction
→ Medical Entity Extraction
→ AI Analysis
→ Structured Results
→ Health Timeline

Supported document types include:

- PDF
- JPG
- JPEG
- PNG
- DOC
- DOCX

## 🔎 OCR

Medical document OCR pipeline supporting multilingual processing.

Languages planned/supported:

- English
- Tamil
- Sinhala

## 💬 AI Chat

ChatGPT-style healthcare assistant interface with:

- Conversation history
- Suggested questions
- AI thinking state
- Context-aware responses
- Safety guardrails

## 💊 Medicine Management

- Medicine records
- Dosage
- Frequency
- Reminder times
- Refill tracking
- Reminder notifications

## 🕐 Health Timeline

A chronological view of important health events including:

- Reports
- Medicines
- Health measurements
- AI insights
- Consultations

## 👨‍⚕️ Doctor Module

Doctor-related functionality includes:

- Doctor profiles
- Verification
- Consultation management
- Patient interaction

## 🔐 Security

- JWT authentication
- Refresh token rotation
- BCrypt password hashing
- Role-based access control
- Audit logging
- API rate limiting
- Secure environment configuration

Roles:

- USER
- DOCTOR
- ADMIN

---

# 🏗️ Architecture

```text
                    ┌──────────────────────┐
                    │     Next.js App      │
                    │   React + TypeScript │
                    └──────────┬───────────┘
                               │
                               │ REST API
                               ▼
                    ┌──────────────────────┐
                    │   Spring Boot API    │
                    │       Backend        │
                    └──────────┬───────────┘
                               │
          ┌────────────────────┼────────────────────┐
          │                    │                    │
          ▼                    ▼                    ▼
   ┌─────────────┐      ┌─────────────┐      ┌─────────────┐
   │ MongoDB     │      │   Redis     │      │    AWS      │
   │   Atlas     │      │   Cache     │      │ S3 / SES    │
   └─────────────┘      └─────────────┘      └─────────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │      AI Engine       │
                    │ Ollama / LLM / RAG   │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │      OCR Service     │
                    │ Python / OCR Engine  │
                    └──────────────────────┘
🛠️ Technology Stack
Frontend
Next.js
React
TypeScript
Tailwind CSS
shadcn/ui
Framer Motion
Zustand
TanStack Query
React Hook Form
Zod
Recharts
Lucide React
Backend
Java 17
Spring Boot 3
Spring Security
Spring Data MongoDB
JWT
BCrypt
WebSocket / STOMP
Spring Scheduler
Spring Validation
SpringDoc OpenAPI
Database
MongoDB Atlas
Redis
AI
Ollama
LLM integration
RAG architecture
Context building
AI safety layer
Medical document analysis
OCR
Python OCR service
Tesseract / OCR engine
Multilingual document processing
Cloud & DevOps
AWS
S3
SES
Docker
Docker Compose
GitHub Actions
Nginx
CI/CD
📁 Project Structure
MedAssist AI X/
│
├── medassist-frontend/
│
├── medassist-backend/
│
├── ocr-service/
│
├── docker-compose.yml
│
├── .env.example
│
├── .gitignore
│
└── README.md
🔐 Environment Configuration

Never commit real credentials.

Create:

cp .env.example .env

Then configure:

JWT_SECRET=
MONGODB_URI=
MONGODB_DATABASE=
AWS_ACCESS_KEY_ID=
AWS_SECRET_ACCESS_KEY=
AWS_S3_BUCKET=
MAIL_USERNAME=
MAIL_PASSWORD=

.env must remain local and must never be pushed to GitHub.

▶️ Running Backend

Requirements:

Java 17
Maven
MongoDB Atlas
Redis
Ollama

Run:

mvn spring-boot:run

Backend:

http://localhost:8080

Swagger:

http://localhost:8080/swagger-ui.html

Health:

http://localhost:8080/actuator/health
▶️ Running Frontend
npm install
npm run dev

Frontend:

http://localhost:3000
🧪 Testing

Backend:

mvn clean test

Full verification:

mvn verify

Frontend:

npm run build
🔑 Main API Endpoints
Authentication
POST /api/auth/register
POST /api/auth/login
POST /api/auth/refresh
POST /api/auth/logout
User
GET /api/users/me
PATCH /api/users/me
Health
GET /api/health
GET /api/health/twin
PUT /api/health
POST /api/health/recalculate
Medical Reports
POST /api/reports/upload
GET /api/reports
GET /api/reports/{id}
GET /api/reports/{id}/status
DELETE /api/reports/{id}
AI
POST /api/ai/chat
POST /api/ai/analyze-report
POST /api/ai/symptoms
GET /api/ai/status
Medicines
GET /api/medicines
POST /api/medicines
PUT /api/medicines/{id}
DELETE /api/medicines/{id}
PATCH /api/medicines/{id}/toggle
Timeline
GET /api/timeline
POST /api/timeline
DELETE /api/timeline/{id}
🛡️ Security

MedAssist AI X follows a security-first architecture.

Implemented security mechanisms include:

JWT authentication
Refresh token rotation
BCrypt password hashing
RBAC
API rate limiting
Audit logging
Environment-based secrets
Secure file validation
Protected API endpoints

Sensitive information must never be committed to source control.

⚠️ Medical Disclaimer

MedAssist AI X provides AI-assisted health information and educational insights.

It is not intended to replace:

Doctors
Emergency medical services
Professional diagnosis
Prescriptions
Clinical decision-making

Users should consult qualified healthcare professionals for medical decisions.

🎯 AI Challenge Sri Lanka 2026

MedAssist AI X is being developed as a startup-level AI HealthTech platform for the AI Challenge Sri Lanka 2026.

The project focuses on:

Artificial Intelligence
Healthcare
Responsible AI
Medical document intelligence
Personalized health insights
Accessible healthcare technology
📌 Project Status
Current Status

Full-stack development completed.

Major platform components:

Next.js frontend
Spring Boot backend
MongoDB Atlas
JWT authentication
AI engine
OCR pipeline
RAG architecture
Health Twin
Medical reports
Medicine management
Health timeline
Doctor module
Admin module
Notifications
AWS integration
Docker
CI/CD

The project is currently being prepared for testing, final integration and production deployment.

👨‍💻 Development

Built with:

Next.js + Spring Boot + MongoDB + AI + OCR + AWS
📄 License

This project is currently intended for educational, research and innovation purposes.

Add an appropriate open-source or proprietary license before public production release.


---

# 6. GitHub Push Commands

Root project folder-ல்:

```powershell
git init
git add .
git status


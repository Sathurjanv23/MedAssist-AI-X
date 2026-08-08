// MongoDB initialization script — runs once on first container start
// Creates indexes and initial admin user for MedAssist AI X

db = db.getSiblingDB('medassist');

// ── Collections & Indexes ─────────────────────────────────────────────────
print("Creating MongoDB indexes...");

// Users — unique email and phone
db.users.createIndex({ "email": 1 }, { unique: true });
db.users.createIndex({ "phone_number": 1 }, { sparse: true });
db.users.createIndex({ "roles": 1, "is_active": 1 });
db.users.createIndex({ "created_at": -1 });

// Medical Profiles
db.medical_profiles.createIndex({ "user_id": 1 }, { unique: true });

// Health Data
db.health_data.createIndex({ "user_id": 1 }, { unique: true });

// Medical Reports
db.medical_reports.createIndex({ "user_id": 1, "created_at": -1 });
db.medical_reports.createIndex({ "status": 1 });

// Medicines
db.medicines.createIndex({ "user_id": 1, "is_active": 1 });

// AI Chat sessions
db.ai_chats.createIndex({ "user_id": 1, "created_at": -1 });

// Health Timeline
db.health_timeline.createIndex({ "user_id": 1, "event_date": -1 });

// Consultations
db.consultations.createIndex({ "doctor_id": 1, "patient_id": 1, "scheduled_at": -1 });

// Doctors
db.doctors.createIndex({ "user_id": 1 }, { unique: true });
db.doctors.createIndex({ "license_number": 1 }, { unique: true, sparse: true });

// Audit Logs
db.audit_logs.createIndex({ "actor_id": 1, "created_at": -1 });
db.audit_logs.createIndex({ "action": 1 });

print("MongoDB indexes created successfully.");

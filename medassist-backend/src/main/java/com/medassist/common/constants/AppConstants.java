package com.medassist.common.constants;

/**
 * Application-wide constants for MedAssist AI X.
 */
public final class AppConstants {

    private AppConstants() {}

    // â”€â”€ API Paths â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    public static final String API_BASE = "/api";
    public static final String AUTH_BASE = API_BASE + "/auth";
    public static final String USER_BASE = API_BASE + "/users";
    public static final String PROFILE_BASE = API_BASE + "/profile";
    public static final String HEALTH_BASE = API_BASE + "/health";
    public static final String REPORTS_BASE = API_BASE + "/reports";
    public static final String MEDICINE_BASE = API_BASE + "/medicines";
    public static final String TIMELINE_BASE = API_BASE + "/timeline";
    public static final String AI_BASE = API_BASE + "/ai";
    public static final String OCR_BASE = API_BASE + "/ocr";
    public static final String DOCTOR_BASE = API_BASE + "/doctor";
    public static final String ADMIN_BASE = API_BASE + "/admin";
    public static final String VOICE_BASE = API_BASE + "/voice";
    public static final String NOTIFICATION_BASE = API_BASE + "/notifications";

    // â”€â”€ Pagination â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;

    // â”€â”€ Cache Keys â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    public static final String CACHE_USER = "user";
    public static final String CACHE_HEALTH = "health";
    public static final String CACHE_AI_RESPONSE = "ai_response";
    public static final String CACHE_HEALTH_SCORE = "health_score";
    public static final String CACHE_REPORT = "report";

    // â”€â”€ JWT â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String TOKEN_TYPE = "access";
    public static final String REFRESH_TOKEN_TYPE = "refresh";

    // â”€â”€ File â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    public static final long MAX_FILE_SIZE_BYTES = 20L * 1024 * 1024; // 20 MB
    public static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png", "image/jpg"};
    public static final String[] ALLOWED_REPORT_TYPES = {
        "application/pdf", "image/jpeg", "image/png", "image/jpg",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    };

    // â”€â”€ Health Score â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    public static final int HEALTH_SCORE_EXCELLENT = 90;
    public static final int HEALTH_SCORE_GOOD = 75;
    public static final int HEALTH_SCORE_FAIR = 60;
    public static final int HEALTH_SCORE_POOR = 0;

    // ——— AI —————————————————————————————————————————————————————————————
    public static final int AI_CONTEXT_MAX_TOKENS = 4096;
    public static final int AI_MAX_CONVERSATION_HISTORY = 20;
    public static final String AI_SAFETY_DISCLAIMER =
        "\n\n⚕️ *Disclaimer: This is AI-generated health information for educational purposes only. " +
        "It is not a medical diagnosis. Please consult a qualified healthcare professional for medical advice.*";

    // ——— Roles ——————————————————————————————————————————————————————————
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_DOCTOR = "ROLE_DOCTOR";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    // â”€â”€ S3 Prefixes â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    public static final String S3_REPORTS_PREFIX = "reports/";
    public static final String S3_PROFILES_PREFIX = "profiles/";

    // â”€â”€ Scheduling â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    public static final String CRON_MORNING = "0 0 8 * * *";
    public static final String CRON_EVENING = "0 0 20 * * *";
    public static final String CRON_MIDNIGHT = "0 0 0 * * *";
}


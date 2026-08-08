/**
 * MedAssist AI X ï¿½ Typed API Functions
 * All calls use the authenticated axios client (JWT auto-attached).
 * Every backend response is wrapped in { success, data, message }.
 */
import apiClient from "./api-client";

// Helper ï¿½ unwrap standard ApiResponse<T>
const unwrap = (res) => res.data?.data ?? res.data;

// -- Auth -----------------------------------------------------------------------
export const authApi = {
  login: async ({ email, password }) => {
    const res = await apiClient.post("/api/auth/login", { email, password });
    return unwrap(res);
  },
  register: async (payload) => {
    const res = await apiClient.post("/api/auth/register", payload);
    return unwrap(res);
  },
  refresh: async (refreshToken) => {
    const res = await apiClient.post("/api/auth/refresh", { refreshToken });
    return unwrap(res);
  },
  logout: async () => {
    await apiClient.post("/api/auth/logout");
  },
};

// -- Current User ---------------------------------------------------------------
export const usersApi = {
  getMe: async () => {
    const res = await apiClient.get("/api/users/me");
    return unwrap(res);
  },
  updateMe: async (payload) => {
    const res = await apiClient.patch("/api/users/me", payload);
    return unwrap(res);
  },
};

// -- Medical Profile -----------------------------------------------------------
export const profileApi = {
  getMedicalProfile: async () => {
    const res = await apiClient.get("/api/profile");
    return unwrap(res);
  },
  updateMedicalProfile: async (payload) => {
    const res = await apiClient.put("/api/profile", payload);
    return unwrap(res);
  },
};

// -- Health Data & Twin --------------------------------------------------------
export const healthApi = {
  getHealthData: async () => {
    const res = await apiClient.get("/api/health");
    return unwrap(res);
  },
  getHealthTwin: async () => {
    const res = await apiClient.get("/api/health/twin");
    return unwrap(res);
  },
  updateHealthData: async (payload) => {
    const res = await apiClient.put("/api/health", payload);
    return unwrap(res);
  },
  recalculate: async () => {
    await apiClient.post("/api/health/recalculate");
  },
};

// -- Health Timeline -----------------------------------------------------------
export const timelineApi = {
  getTimeline: async (page = 0, size = 20) => {
    const res = await apiClient.get("/api/timeline", { params: { page, size } });
    return unwrap(res);
  },
  addEvent: async (event) => {
    const res = await apiClient.post("/api/timeline", event);
    return unwrap(res);
  },
  deleteEvent: async (eventId) => {
    await apiClient.delete(`/api/timeline/${eventId}`);
  },
};

// -- Medical Reports -----------------------------------------------------------
export const reportsApi = {
  getReports: async (page = 0, size = 10) => {
    const res = await apiClient.get("/api/reports", { params: { page, size } });
    return unwrap(res);
  },
  getReport: async (reportId) => {
    const res = await apiClient.get(`/api/reports/${reportId}`);
    return unwrap(res);
  },
  uploadReport: async (file, reportType = "GENERAL") => {
    const form = new FormData();
    form.append("file", file);
    form.append("reportType", reportType);
    const res = await apiClient.post("/api/reports/upload", form, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    return unwrap(res);
  },
  getStatus: async (reportId) => {
    const res = await apiClient.get(`/api/reports/${reportId}/status`);
    return unwrap(res);
  },
  deleteReport: async (reportId) => {
    await apiClient.delete(`/api/reports/${reportId}`);
  },
};

// -- Medicines -----------------------------------------------------------------
export const medicinesApi = {
  getMedicines: async (activeOnly = false) => {
    const res = await apiClient.get("/api/medicines", { params: { activeOnly } });
    return unwrap(res);
  },
  addMedicine: async (payload) => {
    const res = await apiClient.post("/api/medicines", payload);
    return unwrap(res);
  },
  updateMedicine: async (id, payload) => {
    const res = await apiClient.put(`/api/medicines/${id}`, payload);
    return unwrap(res);
  },
  deleteMedicine: async (id) => {
    await apiClient.delete(`/api/medicines/${id}`);
  },
  toggleMedicine: async (id, active) => {
    await apiClient.patch(`/api/medicines/${id}/toggle`, null, { params: { active } });
  },
};

// -- AI Chat -------------------------------------------------------------------
export const aiApi = {
  chat: async (message, sessionId) => {
    const res = await apiClient.post("/api/ai/chat", { message, sessionId });
    return unwrap(res);
  },
  status: async () => {
    const res = await apiClient.get("/api/ai/status");
    return unwrap(res);
  },
};


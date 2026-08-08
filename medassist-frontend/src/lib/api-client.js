import axios from "axios";
// ─────────────────────────────────────────────────────────────────────────────
// MedAssist AI X — Axios API Client with JWT Interceptor
// ─────────────────────────────────────────────────────────────────────────────
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";
export const apiClient = axios.create({
    baseURL: API_BASE_URL,
    timeout: 30000,
    headers: {
        "Content-Type": "application/json",
    },
});
// ── Request Interceptor: Attach JWT ───────────────────────────────────────────
apiClient.interceptors.request.use((config) => {
    if (typeof window !== "undefined") {
        const token = localStorage.getItem("medassist_access_token");
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
    }
    return config;
}, (error) => Promise.reject(error));
// ── Response Interceptor: Handle 401, Token Refresh ──────────────────────────
apiClient.interceptors.response.use((response) => response, async (error) => {
    const originalRequest = error.config;
    if (error.response?.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true;
        try {
            const refreshToken = localStorage.getItem("medassist_refresh_token");
            if (!refreshToken) {
                clearTokens();
                window.location.href = "/login";
                return Promise.reject(error);
            }
            const { data } = await axios.post(`${API_BASE_URL}/auth/refresh`, {
                refreshToken,
            });
            const { accessToken, refreshToken: newRefresh } = data.data;
            localStorage.setItem("medassist_access_token", accessToken);
            localStorage.setItem("medassist_refresh_token", newRefresh);
            originalRequest.headers.Authorization = `Bearer ${accessToken}`;
            return apiClient(originalRequest);
        }
        catch {
            clearTokens();
            window.location.href = "/login";
            return Promise.reject(error);
        }
    }
    return Promise.reject(error);
});
export function clearTokens() {
    if (typeof window !== "undefined") {
        localStorage.removeItem("medassist_access_token");
        localStorage.removeItem("medassist_refresh_token");
        // Clear the auth cookie so middleware knows user is logged out
        document.cookie = "medassist_token=; path=/; max-age=0; samesite=strict";
    }
}
export function setTokens(accessToken, refreshToken) {
    if (typeof window !== "undefined") {
        localStorage.setItem("medassist_access_token", accessToken);
        localStorage.setItem("medassist_refresh_token", refreshToken);
        // Set a cookie so the server-side middleware can verify auth
        // Use 7 days expiry matching refresh token expiry (604800s)
        document.cookie = `medassist_token=${accessToken}; path=/; max-age=604800; samesite=strict`;
    }
}
export default apiClient;

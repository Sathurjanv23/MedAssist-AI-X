"use client";
import { create } from "zustand";
import { persist } from "zustand/middleware";
import { setTokens, clearTokens } from "@/lib/api-client";
export const useAuthStore = create()(persist((set, get) => ({
    user: null,
    isAuthenticated: false,
    isLoading: false,
    language: "en",
    setUser: (user) => set({ user, isAuthenticated: true }),
    login: (user, accessToken, refreshToken) => {
        setTokens(accessToken, refreshToken);
        set({ user, isAuthenticated: true, isLoading: false });
    },
    logout: () => {
        clearTokens();
        set({ user: null, isAuthenticated: false });
    },
    setLoading: (isLoading) => set({ isLoading }),
    setLanguage: (language) => set({ language }),
    hasRole: (role) => get().user?.role === role,
}), {
    name: "medassist_auth",
    partialize: (state) => ({
        user: state.user,
        isAuthenticated: state.isAuthenticated,
        language: state.language,
    }),
}));
export const useUIStore = create((set) => ({
    sidebarOpen: true,
    notificationPanelOpen: false,
    activeModal: null,
    setSidebarOpen: (open) => set({ sidebarOpen: open }),
    toggleSidebar: () => set((s) => ({ sidebarOpen: !s.sidebarOpen })),
    setNotificationPanel: (open) => set({ notificationPanelOpen: open }),
    openModal: (modal) => set({ activeModal: modal }),
    closeModal: () => set({ activeModal: null }),
}));

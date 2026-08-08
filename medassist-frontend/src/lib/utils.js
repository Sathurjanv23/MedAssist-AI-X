import { clsx } from "clsx";
import { twMerge } from "tailwind-merge";
export function cn(...inputs) {
    return twMerge(clsx(inputs));
}
export function formatDate(date, format) {
    const d = new Date(date);
    if (format === "short") {
        return d.toLocaleDateString("en-US", { month: "short", day: "numeric" });
    }
    return d.toLocaleDateString("en-US", {
        year: "numeric",
        month: "long",
        day: "numeric",
    });
}
export function formatTime(date) {
    return new Date(date).toLocaleTimeString("en-US", {
        hour: "2-digit",
        minute: "2-digit",
    });
}
export function formatFileSize(bytes) {
    if (bytes < 1024)
        return `${bytes} B`;
    if (bytes < 1024 * 1024)
        return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
}
export function getRiskColor(risk) {
    switch (risk) {
        case "LOW":
            return "text-emerald-500 border-emerald-500/30 bg-emerald-500/10";
        case "MEDIUM":
            return "text-amber-500 border-amber-500/30 bg-amber-500/10";
        case "HIGH":
            return "text-red-500 border-red-500/30 bg-red-500/10";
        case "CRITICAL":
            return "text-red-600 border-red-600/50 bg-red-600/15 animate-pulse";
        default:
            return "text-muted-foreground border-border bg-muted";
    }
}
export function getHealthGrade(score) {
    if (score >= 90)
        return { grade: "Excellent", color: "text-emerald-400", description: "Outstanding health" };
    if (score >= 75)
        return { grade: "Good", color: "text-cyan-400", description: "Well maintained" };
    if (score >= 60)
        return { grade: "Fair", color: "text-amber-400", description: "Room for improvement" };
    return { grade: "Poor", color: "text-red-400", description: "Needs attention" };
}
export function slugify(text) {
    return text.toLowerCase().replace(/\s+/g, "-").replace(/[^\w-]+/g, "");
}
export function truncate(text, length) {
    if (text.length <= length)
        return text;
    return `${text.slice(0, length)}...`;
}
export function calculateBMI(weight, height) {
    const heightM = height / 100;
    return Math.round((weight / (heightM * heightM)) * 10) / 10;
}
export function getBMICategory(bmi) {
    if (bmi < 18.5)
        return { category: "Underweight", color: "text-blue-400" };
    if (bmi < 25)
        return { category: "Normal", color: "text-emerald-400" };
    if (bmi < 30)
        return { category: "Overweight", color: "text-amber-400" };
    return { category: "Obese", color: "text-red-400" };
}
export function timeAgo(date) {
    const now = new Date();
    const past = new Date(date);
    const diff = now.getTime() - past.getTime();
    const minutes = Math.floor(diff / 60000);
    const hours = Math.floor(diff / 3600000);
    const days = Math.floor(diff / 86400000);
    if (minutes < 1)
        return "Just now";
    if (minutes < 60)
        return `${minutes}m ago`;
    if (hours < 24)
        return `${hours}h ago`;
    if (days < 7)
        return `${days}d ago`;
    return formatDate(date, "short");
}
export function generateId() {
    return Math.random().toString(36).substring(2, 11);
}

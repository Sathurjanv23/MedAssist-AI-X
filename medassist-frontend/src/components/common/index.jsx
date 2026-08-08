"use client";
import { motion } from "framer-motion";
import { cn } from "@/lib/utils";
const glowMap = {
    cyan: "hover:border-cyan-500/40 hover:[box-shadow:0_0_30px_rgba(6,182,212,0.25)]",
    indigo: "hover:border-indigo-500/40 hover:[box-shadow:0_0_30px_rgba(99,102,241,0.25)]",
    emerald: "hover:border-emerald-500/40 hover:[box-shadow:0_0_30px_rgba(16,185,129,0.25)]",
    rose: "hover:border-rose-500/40 hover:[box-shadow:0_0_30px_rgba(239,68,68,0.25)]",
    amber: "hover:border-amber-500/40 hover:[box-shadow:0_0_30px_rgba(245,158,11,0.25)]",
};
export function GlowCard({ children, className, glowColor = "cyan", delay = 0, hoverable = true, noPadding = false, ...props }) {
    return (<motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4, delay, ease: "easeOut" }} className={cn("bg-card border border-border rounded-xl", !noPadding && "p-5", hoverable && [
            "transition-all duration-300 cursor-default",
            glowMap[glowColor],
            "hover:-translate-y-1",
        ], className)} {...props}>
      {children}
    </motion.div>);
}
const riskConfig = {
    LOW: { label: "Low Risk", className: "text-emerald-400 bg-emerald-400/10 border-emerald-400/30" },
    MEDIUM: { label: "Medium Risk", className: "text-amber-400 bg-amber-400/10 border-amber-400/30" },
    HIGH: { label: "High Risk", className: "text-red-400 bg-red-400/10 border-red-400/30" },
    CRITICAL: { label: "Critical", className: "text-red-500 bg-red-500/15 border-red-500/50 animate-pulse" },
};
export function RiskBadge({ level, className, size = "md" }) {
    const config = riskConfig[level];
    return (<span className={cn("inline-flex items-center gap-1.5 rounded-full border font-semibold", size === "sm" ? "px-2 py-0.5 text-xs" : "px-3 py-1 text-sm", config.className, className)}>
      <span className={cn("inline-block rounded-full", size === "sm" ? "w-1.5 h-1.5" : "w-2 h-2", {
            "bg-emerald-400": level === "LOW",
            "bg-amber-400": level === "MEDIUM",
            "bg-red-400": level === "HIGH" || level === "CRITICAL",
        })}/>
      {config.label}
    </span>);
}
export function AnimatedCounter({ value, suffix = "", prefix = "", decimals = 0, className, }) {
    return (<motion.span className={className} initial={{ opacity: 0, scale: 0.8 }} animate={{ opacity: 1, scale: 1 }} transition={{ duration: 0.5, ease: "easeOut" }}>
      {prefix}
      {value.toFixed(decimals)}
      {suffix}
    </motion.span>);
}
// ─────────────────────────────────────────────────────────────────────────────
// PageTransition
// ─────────────────────────────────────────────────────────────────────────────
export function PageTransition({ children }) {
    return (<motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: -16 }} transition={{ duration: 0.35, ease: "easeInOut" }}>
      {children}
    </motion.div>);
}
// ─────────────────────────────────────────────────────────────────────────────
// LoadingSpinner
// ─────────────────────────────────────────────────────────────────────────────
export function LoadingSpinner({ size = 24, className }) {
    return (<svg className={cn("animate-spin text-primary", className)} width={size} height={size} viewBox="0 0 24 24" fill="none">
      <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeDasharray="60" strokeDashoffset="20"/>
    </svg>);
}
export function SectionHeader({ title, subtitle, badge, action, className }) {
    return (<div className={cn("flex items-start justify-between gap-4 mb-6", className)}>
      <div>
        {badge && (<span className="text-xs font-semibold text-primary bg-primary/10 border border-primary/20 rounded-full px-3 py-1 mb-2 inline-block">
            {badge}
          </span>)}
        <h2 className="text-xl font-bold text-foreground">{title}</h2>
        {subtitle && <p className="text-sm text-muted-foreground mt-1">{subtitle}</p>}
      </div>
      {action && <div className="shrink-0">{action}</div>}
    </div>);
}
// ─────────────────────────────────────────────────────────────────────────────
// AIOrb — Pulsing AI visual element
// ─────────────────────────────────────────────────────────────────────────────
export function AIOrb({ size = 40, className }) {
    return (<div className={cn("relative inline-flex items-center justify-center", className)} style={{ width: size, height: size }}>
      {/* Outer glow ring */}
      <div className="absolute inset-0 rounded-full bg-primary/20 animate-ping" style={{ animationDuration: "2.5s" }}/>
      {/* Middle ring */}
      <div className="absolute inset-1 rounded-full bg-primary/30"/>
      {/* Core */}
      <div className="relative rounded-full gradient-brand flex items-center justify-center" style={{ width: size * 0.6, height: size * 0.6 }}>
        <span className="text-white font-bold" style={{ fontSize: size * 0.2 }}>
          AI
        </span>
      </div>
    </div>);
}
// ─────────────────────────────────────────────────────────────────────────────
// AIThinkingIndicator
// ─────────────────────────────────────────────────────────────────────────────
export function AIThinkingIndicator() {
    return (<div className="flex items-center gap-3 p-4 bg-card border border-border rounded-xl">
      <AIOrb size={32}/>
      <div className="flex flex-col gap-1">
        <span className="text-xs text-muted-foreground">AI Thinking</span>
        <div className="flex gap-1.5 items-center h-4">
          {[0, 1, 2].map((i) => (<motion.div key={i} className="w-2 h-2 rounded-full bg-primary" animate={{ scale: [1, 1.4, 1], opacity: [0.5, 1, 0.5] }} transition={{ duration: 1.2, repeat: Infinity, delay: i * 0.2 }}/>))}
        </div>
      </div>
    </div>);
}
export function HealthScoreRing({ score, size = 120, strokeWidth = 10, className, showLabel = true, }) {
    const radius = (size - strokeWidth) / 2;
    const circumference = 2 * Math.PI * radius;
    const offset = circumference - (score / 100) * circumference;
    const getColor = (s) => {
        if (s >= 80)
            return "#10b981"; // emerald
        if (s >= 60)
            return "#06b6d4"; // cyan
        if (s >= 40)
            return "#f59e0b"; // amber
        return "#ef4444"; // red
    };
    const color = getColor(score);
    return (<div className={cn("relative inline-flex items-center justify-center", className)}>
      <svg width={size} height={size} className="health-ring -rotate-90">
        {/* Background track */}
        <circle cx={size / 2} cy={size / 2} r={radius} fill="none" stroke="currentColor" strokeWidth={strokeWidth} className="text-border"/>
        {/* Progress arc */}
        <motion.circle cx={size / 2} cy={size / 2} r={radius} fill="none" stroke={color} strokeWidth={strokeWidth} strokeLinecap="round" strokeDasharray={circumference} initial={{ strokeDashoffset: circumference }} animate={{ strokeDashoffset: offset }} transition={{ duration: 1.5, ease: "easeOut", delay: 0.3 }}/>
      </svg>
      {showLabel && (<div className="absolute inset-0 flex flex-col items-center justify-center">
          <motion.span className="text-2xl font-bold text-foreground" style={{ color }} initial={{ opacity: 0, scale: 0.5 }} animate={{ opacity: 1, scale: 1 }} transition={{ duration: 0.5, delay: 0.8 }}>
            {score}
          </motion.span>
          <span className="text-xs text-muted-foreground font-medium">/ 100</span>
        </div>)}
    </div>);
}

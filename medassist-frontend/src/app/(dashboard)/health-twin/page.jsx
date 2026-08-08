"use client";
import { motion } from "framer-motion";
import { useQuery } from "@tanstack/react-query";
import { Heart, Activity, Droplets, Brain, Moon, TrendingUp, TrendingDown, Target, Shield, Apple, Dumbbell, Loader2, Database } from "lucide-react";
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, RadialBarChart, RadialBar } from "recharts";
import { GlowCard, HealthScoreRing, RiskBadge, AIOrb, PageTransition, SectionHeader } from "@/components/common";
import { healthApi } from "@/lib/api";
import { useAuthStore } from "@/store/auth.store";

const lifestyleIcons = {
  Nutrition: Apple,
  Exercise: Dumbbell,
  Sleep: Moon,
  Mental: Brain,
  Hydration: Droplets,
  Vitals: Heart,
};

const lifestyleColors = {
  Nutrition: "#10b981",
  Exercise: "#6366f1",
  Sleep: "#8b5cf6",
  Mental: "#06b6d4",
  Hydration: "#f59e0b",
  Vitals: "#ef4444",
};

export default function HealthTwinPage() {
  const { user } = useAuthStore();
  
  const { data: twinData, isLoading, error } = useQuery({
    queryKey: ["health-twin"],
    queryFn: healthApi.getHealthTwin,
    retry: 1,
  });

  if (isLoading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh]">
        <div className="relative mb-6">
          <AIOrb size={80} />
          <motion.div animate={{ rotate: 360 }} transition={{ duration: 3, repeat: Infinity, ease: "linear" }} className="absolute inset-0 rounded-full border-t-2 border-primary" />
        </div>
        <h2 className="text-xl font-bold text-foreground">Syncing Health Twin...</h2>
        <p className="text-muted-foreground mt-2">Connecting to MedAssist AI Core</p>
      </div>
    );
  }

  // Handle empty state gracefully
  const isDataEmpty = !twinData || (
    twinData.healthScore == null &&
    !twinData.sleepData &&
    !twinData.activityData &&
    !twinData.nutritionData &&
    !(twinData.aiInsights?.length) &&
    !(twinData.recommendations?.length)
  );
  
  if (isDataEmpty) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh] max-w-lg mx-auto text-center">
        <div className="w-20 h-20 bg-muted/50 rounded-full flex items-center justify-center mb-6">
          <Database className="w-10 h-10 text-muted-foreground opacity-50" />
        </div>
        <h2 className="text-2xl font-bold text-foreground">No Health Data Found</h2>
        <p className="text-muted-foreground mt-2 mb-6">
          Your AI Health Twin needs data to activate. Upload medical reports or enter your vitals manually to generate your personalized health replica.
        </p>
        <a href="/reports" className="btn-neon px-6 py-3 rounded-xl font-semibold text-white">
          Upload Medical Report
        </a>
      </div>
    );
  }

  const { healthScore, riskLevel, lifestyleScores, riskIndicators, bodyMetrics, aiInsights, recommendations } = twinData;

  const lifestyleArray = lifestyleScores ? Object.entries(lifestyleScores).map(([key, value]) => ({
    name: key,
    score: value,
    color: lifestyleColors[key] || "#primary",
    icon: lifestyleIcons[key] || Activity,
  })) : [];

  const radialData = lifestyleArray.map((s) => ({ name: s.name, value: s.score, fill: s.color }));

  // Convert map to array for body metrics
  const metricsArray = bodyMetrics ? Object.entries(bodyMetrics).map(([key, val], i) => {
    const icons = [Target, Activity, Shield, Droplets];
    const colors = ["text-cyan-400", "text-indigo-400", "text-emerald-400", "text-red-400"];
    return {
      label: key,
      value: val,
      icon: icons[i % icons.length],
      color: colors[i % colors.length]
    };
  }) : [];

  return (
    <PageTransition>
      <div className="max-w-[1400px] mx-auto space-y-6">
        {/* ── Page Header ───────────────────────────────────────────── */}
        <div className="flex items-start justify-between">
          <div>
            <div className="flex items-center gap-2 mb-2">
              <AIOrb size={32} />
              <span className="text-xs font-semibold text-primary bg-primary/10 border border-primary/20 rounded-full px-3 py-1">
                AI Health Twin
              </span>
            </div>
            <h1 className="text-2xl font-bold text-foreground">Your Digital Health Twin</h1>
            <p className="text-muted-foreground text-sm mt-1">
              A real-time AI replica of your health — continuously updated with your data.
            </p>
          </div>
          <motion.div animate={{ rotate: [0, 360] }} transition={{ duration: 20, repeat: Infinity, ease: "linear" }} className="w-12 h-12 rounded-full border-2 border-dashed border-primary/40 flex items-center justify-center">
            <Activity className="w-5 h-5 text-primary" />
          </motion.div>
        </div>

        {/* ── Main Twin Visualization ───────────────────────────────── */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-5">
          {/* Left: Body Metrics + Score */}
          <GlowCard delay={0} glowColor="cyan" className="flex flex-col items-center text-center">
            <div className="relative w-48 h-56 mb-4">
              <motion.div animate={{ scale: [1, 1.05, 1], opacity: [0.5, 0.8, 0.5] }} transition={{ duration: 3, repeat: Infinity }} className="absolute inset-0 rounded-full border-2 border-primary/30" />
              <div className="absolute inset-0 flex items-center justify-center">
                <div className="relative">
                  <HealthScoreRing score={healthScore} size={140} strokeWidth={10} />
                  <motion.div animate={{ rotate: 360 }} transition={{ duration: 8, repeat: Infinity, ease: "linear" }} className="absolute inset-0 pointer-events-none">
                    <div className="absolute -top-1 left-1/2 -translate-x-1/2 w-3 h-3 rounded-full bg-primary shadow-lg shadow-cyan-500/50" />
                  </motion.div>
                </div>
              </div>
            </div>

            <h2 className="text-lg font-bold text-foreground">Overall Health</h2>
            <p className="text-3xl font-bold gradient-brand-text">{healthScore}%</p>
            <RiskBadge level={riskLevel} className="mt-2" />

            {metricsArray.length > 0 && (
              <div className="mt-5 w-full grid grid-cols-2 gap-2">
                {metricsArray.map((metric) => (
                  <div key={metric.label} className="p-3 rounded-lg bg-muted/50 text-left">
                    <p className="text-[10px] text-muted-foreground">{metric.label}</p>
                    <p className={`text-sm font-bold ${metric.color}`}>{metric.value}</p>
                  </div>
                ))}
              </div>
            )}
          </GlowCard>

          {/* Center: Lifestyle Score Radar */}
          <GlowCard delay={0.08} glowColor="indigo" className="flex flex-col">
            <SectionHeader title="Lifestyle Score" subtitle={`${lifestyleArray.length} wellness dimensions`} />
            <div className="flex-1 flex items-center justify-center">
              <div className="w-full h-64">
                <ResponsiveContainer width="100%" height="100%">
                  <RadialBarChart innerRadius="20%" outerRadius="90%" data={radialData} startAngle={180} endAngle={-180}>
                    <RadialBar dataKey="value" cornerRadius={4} />
                    <Tooltip contentStyle={{ background: "hsl(var(--card))", border: "1px solid hsl(var(--border))", borderRadius: "8px", fontSize: "12px" }} />
                  </RadialBarChart>
                </ResponsiveContainer>
              </div>
            </div>
            <div className="grid grid-cols-3 gap-2 mt-2">
              {lifestyleArray.map((s) => (
                <div key={s.name} className="flex items-center gap-1.5">
                  <div className="w-2 h-2 rounded-full shrink-0" style={{ background: s.color }} />
                  <span className="text-[10px] text-muted-foreground truncate">{s.name}</span>
                  <span className="text-[10px] font-semibold text-foreground">{s.score}</span>
                </div>
              ))}
            </div>
          </GlowCard>

          {/* Right: Risk Indicators */}
          <GlowCard delay={0.12} glowColor="amber">
            <SectionHeader title="Risk Indicators" badge="AI Assessed" />
            <div className="space-y-4">
              {riskIndicators ? Object.entries(riskIndicators).map(([name, level], i) => (
                <motion.div key={name} initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: 0.2 + i * 0.06 }}>
                  <div className="flex items-center justify-between mb-1.5">
                    <span className="text-sm font-medium text-foreground">{name}</span>
                    <RiskBadge level={level} size="sm" />
                  </div>
                  <div className="h-2 bg-muted rounded-full overflow-hidden">
                    <motion.div initial={{ width: 0 }} animate={{ width: level === "LOW" ? "20%" : level === "MEDIUM" ? "60%" : "95%" }} transition={{ duration: 1, delay: 0.4 + i * 0.1, ease: "easeOut" }} className={`h-full rounded-full ${level === "LOW" ? "bg-emerald-400" : level === "MEDIUM" ? "bg-amber-400" : "bg-red-400"}`} />
                  </div>
                </motion.div>
              )) : (
                <p className="text-sm text-muted-foreground">No risk indicators identified yet.</p>
              )}
            </div>
          </GlowCard>
        </div>

        {/* ── Wellness Dimension Cards ──────────────────────────────── */}
        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3">
          {lifestyleArray.map((score, i) => (
            <WellnessDimensionCard key={score.name} data={score} delay={i * 0.05} />
          ))}
        </div>

        {/* ── AI Insights Panel ────────────────────────────────────── */}
        <GlowCard delay={0.25} glowColor="indigo" noPadding>
          <div className="p-5">
            <div className="flex items-center gap-3 mb-5">
              <AIOrb size={36} />
              <div>
                <h3 className="font-bold text-foreground">AI Recommendations</h3>
                <p className="text-xs text-muted-foreground">Personalized health actions</p>
              </div>
            </div>
            
            {recommendations && recommendations.length > 0 ? (
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                {recommendations.map((rec, i) => (
                  <motion.div key={i} initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.4 + i * 0.1 }} className="p-4 rounded-xl bg-muted/40 border border-border flex items-start gap-3">
                    <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center shrink-0 mt-0.5">
                      <Target className="w-4 h-4 text-primary" />
                    </div>
                    <p className="text-sm text-foreground leading-relaxed">{rec}</p>
                  </motion.div>
                ))}
              </div>
            ) : (
              <p className="text-sm text-muted-foreground">Keep tracking your data to receive personalized AI recommendations.</p>
            )}
          </div>
        </GlowCard>
      </div>
    </PageTransition>
  );
}

function WellnessDimensionCard({ data, delay }) {
  return (
    <motion.div initial={{ opacity: 0, scale: 0.85 }} animate={{ opacity: 1, scale: 1 }} transition={{ delay, duration: 0.35 }} className="p-4 rounded-xl bg-card border border-border hover:border-primary/30 transition-all group cursor-default">
      <div className="w-10 h-10 rounded-xl flex items-center justify-center mb-3 transition-transform group-hover:scale-110" style={{ background: `${data.color}20`, border: `1px solid ${data.color}30` }}>
        <data.icon className="w-5 h-5" style={{ color: data.color }} />
      </div>
      <p className="text-xs text-muted-foreground">{data.name}</p>
      <p className="text-2xl font-bold text-foreground mt-0.5" style={{ color: data.color }}>
        {data.score}
      </p>
      <div className="h-1 bg-muted rounded-full mt-2 overflow-hidden">
        <motion.div initial={{ width: 0 }} animate={{ width: `${data.score}%` }} transition={{ duration: 1, delay: delay + 0.3 }} className="h-full rounded-full" style={{ background: data.color }} />
      </div>
    </motion.div>
  );
}

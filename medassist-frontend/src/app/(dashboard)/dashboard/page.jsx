"use client";
import { motion } from "framer-motion";
import Link from "next/link";
import { useQuery } from "@tanstack/react-query";
import { ArrowRight, Pill, FileText, Activity, Zap, TrendingUp, Plus, AlertTriangle, CheckCircle2, Loader2, Database, } from "lucide-react";
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from "recharts";
import { GlowCard, RiskBadge, AIOrb, HealthScoreRing, PageTransition, SectionHeader } from "@/components/common";
import { useAuthStore } from "@/store/auth.store";
import { healthApi, reportsApi, medicinesApi, timelineApi } from "@/lib/api";
import { formatDate, timeAgo } from "@/lib/utils";

// ─────────────────────────────────────────────────────────────────────────────
// Empty-state reusable component
// ─────────────────────────────────────────────────────────────────────────────
function EmptyState({ icon: Icon, title, description, action }) {
  return (
    <div className="flex flex-col items-center justify-center py-8 text-center">
      <div className="w-12 h-12 rounded-xl bg-muted/60 border border-border flex items-center justify-center mb-3">
        <Icon className="w-5 h-5 text-muted-foreground" />
      </div>
      <p className="text-sm font-medium text-foreground">{title}</p>
      <p className="text-xs text-muted-foreground mt-1 max-w-[180px]">{description}</p>
      {action && <div className="mt-3">{action}</div>}
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// Dashboard Page
// ─────────────────────────────────────────────────────────────────────────────
export default function DashboardPage() {
  const { user } = useAuthStore();
  const currentHour = new Date().getHours();
  const greeting = currentHour < 12 ? "Good morning" : currentHour < 17 ? "Good afternoon" : "Good evening";

  const { data: twin, isLoading: twinLoading } = useQuery({
    queryKey: ["health-twin"],
    queryFn: healthApi.getHealthTwin,
    retry: 1,
  });

  const { data: reportsPage, isLoading: reportsLoading } = useQuery({
    queryKey: ["reports", 0],
    queryFn: () => reportsApi.getReports(0, 3),
    retry: 1,
  });

  const { data: medicines, isLoading: medsLoading } = useQuery({
    queryKey: ["medicines"],
    queryFn: () => medicinesApi.getMedicines(true),
    retry: 1,
  });

  const { data: timelinePage, isLoading: timelineLoading } = useQuery({
    queryKey: ["timeline", 0],
    queryFn: () => timelineApi.getTimeline(0, 4),
    retry: 1,
  });

  const reports = reportsPage?.content ?? [];
  const todayMeds = medicines ?? [];
  const timelineEvents = timelinePage?.content ?? [];
  const healthScore = twin?.healthScore ?? null;
  const riskLevel = twin?.riskLevel ?? null;

  return (
    <PageTransition>
      <div className="max-w-[1400px] mx-auto space-y-6">
        {/* ── Greeting ──────────────────────────────────────────────── */}
        <div className="flex items-center justify-between">
          <div>
            <motion.h1 initial={{ opacity: 0, y: -10 }} animate={{ opacity: 1, y: 0 }} className="text-2xl font-bold text-foreground">
              {greeting}, {user?.firstName ?? "there"} 👋
            </motion.h1>
            <p className="text-sm text-muted-foreground mt-1">
              {new Date().toLocaleDateString("en-US", { weekday: "long", year: "numeric", month: "long", day: "numeric" })}
            </p>
          </div>
          <Link href="/reports">
            <motion.button whileHover={{ scale: 1.03 }} whileTap={{ scale: 0.97 }} className="btn-neon px-5 py-2.5 rounded-xl font-semibold text-white text-sm flex items-center gap-2">
              <Plus className="w-4 h-4" />
              Upload Report
            </motion.button>
          </Link>
        </div>

        {/* ── Top Cards Row ─────────────────────────────────────────── */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          {/* Health Score */}
          <GlowCard delay={0} glowColor="cyan" className="md:col-span-1">
            <div className="flex items-center justify-between mb-3">
              <p className="text-sm font-semibold text-muted-foreground">Health Score</p>
            </div>
            {twinLoading ? (
              <div className="flex items-center justify-center h-20"><Loader2 className="w-6 h-6 animate-spin text-muted-foreground" /></div>
            ) : healthScore !== null ? (
              <div className="flex items-center gap-4">
                <HealthScoreRing score={healthScore} size={90} strokeWidth={8} />
                <div>
                  <p className="text-2xl font-bold text-foreground">{healthScore}%</p>
                  <p className="text-sm font-semibold text-cyan-400">{healthScore >= 75 ? "Good" : healthScore >= 50 ? "Fair" : "Needs care"}</p>
                  <p className="text-xs text-muted-foreground mt-1">Calculated from your data</p>
                </div>
              </div>
            ) : (
              <div className="flex flex-col items-center py-4">
                <p className="text-sm text-muted-foreground text-center">No health data yet</p>
                <p className="text-xs text-muted-foreground mt-1">Upload reports to generate score</p>
              </div>
            )}
          </GlowCard>

          {/* AI Daily Summary */}
          <GlowCard delay={0.05} glowColor="indigo" className="md:col-span-1 lg:col-span-2">
            <div className="flex items-center gap-2.5 mb-3">
              <AIOrb size={32} />
              <div>
                <p className="text-sm font-semibold text-primary">AI Health Assistant</p>
                <p className="text-xs text-muted-foreground">Ready to help</p>
              </div>
            </div>
            {twin?.aiInsights?.length > 0 ? (
              <div className="space-y-1">
                {twin.aiInsights.slice(0, 2).map((ins, i) => (
                  <p key={i} className="text-sm text-foreground leading-relaxed">✨ {ins}</p>
                ))}
              </div>
            ) : (
              <p className="text-sm text-muted-foreground leading-relaxed">
                Upload your medical reports to get personalized AI health insights and recommendations.
              </p>
            )}
            <div className="mt-3 flex flex-wrap gap-2">
              {["View Health Twin", "Chat with AI"].map((action) => (
                <Link key={action} href={action.includes("Chat") ? "/ai-chat" : "/health-twin"} className="text-xs text-primary border border-primary/20 bg-primary/8 hover:bg-primary/15 rounded-full px-3 py-1 transition-colors">
                  {action} →
                </Link>
              ))}
            </div>
          </GlowCard>

          {/* Risk Overview */}
          <GlowCard delay={0.1} glowColor="amber">
            <p className="text-sm font-semibold text-muted-foreground mb-3">Risk Overview</p>
            {riskLevel ? (
              <>
                <RiskBadge level={riskLevel} className="mb-3" />
                {twin?.recommendations?.slice(0, 2).map((rec, i) => (
                  <p key={i} className="text-xs text-muted-foreground mb-1">• {rec}</p>
                ))}
              </>
            ) : (
              <p className="text-sm text-muted-foreground">No risk data yet — add health data to see your risk profile.</p>
            )}
          </GlowCard>
        </div>

        {/* ── Bottom Grid ───────────────────────────────────────────── */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-5">
          {/* Recent Reports */}
          <GlowCard delay={0.2} glowColor="indigo" noPadding>
            <div className="p-5">
              <SectionHeader title="Recent Reports" action={
                <Link href="/reports" className="text-xs text-primary hover:underline flex items-center gap-1">
                  View all <ArrowRight className="w-3.5 h-3.5" />
                </Link>
              } />
              {reportsLoading ? (
                <div className="flex justify-center py-6"><Loader2 className="w-5 h-5 animate-spin text-muted-foreground" /></div>
              ) : reports.length === 0 ? (
                <EmptyState
                  icon={FileText}
                  title="No reports yet"
                  description="Upload your first medical report to see AI analysis here"
                  action={<Link href="/reports" className="text-xs text-primary hover:underline">Upload report →</Link>}
                />
              ) : (
                <div className="space-y-3">
                  {reports.map((report) => (
                    <Link key={report.id} href="/reports">
                      <div className="flex items-center gap-3 p-3 rounded-lg hover:bg-muted transition-colors">
                        <div className="w-9 h-9 rounded-lg bg-indigo-500/10 border border-indigo-500/20 flex items-center justify-center shrink-0">
                          <FileText className="w-4 h-4 text-indigo-400" />
                        </div>
                        <div className="flex-1 min-w-0">
                          <p className="text-sm font-medium text-foreground truncate">
                            {report.originalFileName ?? report.fileName}
                          </p>
                          <p className="text-xs text-muted-foreground">{timeAgo(report.createdAt)}</p>
                        </div>
                        {report.analysisResult?.riskLevel && <RiskBadge level={report.analysisResult.riskLevel} size="sm" />}
                      </div>
                    </Link>
                  ))}
                </div>
              )}
            </div>
          </GlowCard>

          {/* Medicine Reminder */}
          <GlowCard delay={0.25} glowColor="emerald" noPadding>
            <div className="p-5">
              <SectionHeader title="Today's Medicines" badge={todayMeds.length > 0 ? `${todayMeds.length} Active` : undefined} action={
                <Link href="/medicines" className="text-xs text-primary hover:underline flex items-center gap-1">
                  Manage <ArrowRight className="w-3.5 h-3.5" />
                </Link>
              } />
              {medsLoading ? (
                <div className="flex justify-center py-6"><Loader2 className="w-5 h-5 animate-spin text-muted-foreground" /></div>
              ) : todayMeds.length === 0 ? (
                <EmptyState
                  icon={Pill}
                  title="No medicines added"
                  description="Track your medications and get smart reminders"
                  action={<Link href="/medicines" className="text-xs text-primary hover:underline">Add medicine →</Link>}
                />
              ) : (
                <div className="space-y-3">
                  {todayMeds.slice(0, 4).map((med, i) => (
                    <motion.div key={med.id} initial={{ opacity: 0, x: -10 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: 0.3 + i * 0.07 }} className="flex items-center gap-3 p-3 rounded-lg bg-muted/50">
                      <div className="w-9 h-9 rounded-lg bg-emerald-500/10 border border-emerald-500/20 flex items-center justify-center shrink-0">
                        <Pill className="w-4 h-4 text-emerald-400" />
                      </div>
                      <div className="flex-1">
                        <p className="text-sm font-medium text-foreground">{med.medicineName}</p>
                        <p className="text-xs text-muted-foreground">{med.reminderTimes?.join(", ")} • {med.dosage}</p>
                      </div>
                      <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
                    </motion.div>
                  ))}
                </div>
              )}
            </div>
          </GlowCard>

          {/* AI Recommendations */}
          <GlowCard delay={0.3} glowColor="cyan" noPadding>
            <div className="p-5">
              <SectionHeader title="AI Recommendations" badge={twin?.recommendations?.length > 0 ? "Personalized" : undefined} />
              {twinLoading ? (
                <div className="flex justify-center py-6"><Loader2 className="w-5 h-5 animate-spin text-muted-foreground" /></div>
              ) : !twin?.recommendations?.length ? (
                <EmptyState
                  icon={Activity}
                  title="No recommendations yet"
                  description="Add health data to receive personalized AI recommendations"
                />
              ) : (
                <div className="space-y-3">
                  {twin.recommendations.slice(0, 4).map((rec, i) => (
                    <motion.div key={i} initial={{ opacity: 0, x: 10 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: 0.35 + i * 0.07 }} className="flex gap-3 p-3 rounded-lg border border-border bg-muted/30">
                      <div className="mt-0.5 w-1.5 h-1.5 rounded-full shrink-0 bg-cyan-400" />
                      <p className="text-sm text-foreground">{rec}</p>
                    </motion.div>
                  ))}
                </div>
              )}
            </div>
          </GlowCard>
        </div>

        {/* ── Health Timeline Preview ───────────────────────────────── */}
        <GlowCard delay={0.35} glowColor="indigo" noPadding>
          <div className="p-5">
            <SectionHeader title="Health Timeline" subtitle="Your recent health journey" action={
              <Link href="/timeline" className="text-xs text-primary hover:underline flex items-center gap-1">
                View full timeline <ArrowRight className="w-3.5 h-3.5" />
              </Link>
            } />
            {timelineLoading ? (
              <div className="flex justify-center py-6"><Loader2 className="w-5 h-5 animate-spin text-muted-foreground" /></div>
            ) : timelineEvents.length === 0 ? (
              <EmptyState
                icon={Zap}
                title="No health events yet"
                description="Your health journey will appear here as you add data and upload reports"
              />
            ) : (
              <div className="relative">
                <div className="timeline-line absolute left-5 top-0 bottom-0" />
                <div className="space-y-4 pl-12">
                  {timelineEvents.map((event, i) => (
                    <TimelineEventItem key={event.id} event={event} delay={0.4 + i * 0.06} />
                  ))}
                </div>
              </div>
            )}
          </div>
        </GlowCard>
      </div>
    </PageTransition>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// TimelineEventItem
// ─────────────────────────────────────────────────────────────────────────────
const eventTypeConfig = {
  REPORT_UPLOADED: { color: "bg-indigo-500", icon: FileText },
  MEDICATION_STARTED: { color: "bg-emerald-500", icon: Pill },
  DOCTOR_VISIT: { color: "bg-cyan-500", icon: Activity },
  HEALTH_IMPROVEMENT: { color: "bg-emerald-400", icon: TrendingUp },
  RISK_DETECTED: { color: "bg-red-500", icon: AlertTriangle },
  SYMPTOM_LOG: { color: "bg-amber-500", icon: Zap },
  VACCINATION: { color: "bg-violet-500", icon: CheckCircle2 },
};

function TimelineEventItem({ event, delay }) {
  const config = eventTypeConfig[event.eventType] ?? { color: "bg-primary", icon: Activity };
  return (
    <motion.div initial={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }} transition={{ delay, duration: 0.3 }} className="relative">
      <div className={`timeline-dot absolute -left-[2.65rem] top-1.5 w-3 h-3 ${config.color}`} />
      <div className="flex items-start gap-3 p-3 rounded-lg hover:bg-muted/40 transition-colors">
        <div>
          <p className="text-sm font-medium text-foreground">{event.title}</p>
          <p className="text-xs text-muted-foreground mt-0.5 line-clamp-1">{event.description}</p>
          <p className="text-xs text-muted-foreground mt-1">{formatDate(event.eventDate, "short")} · {timeAgo(event.eventDate)}</p>
        </div>
      </div>
    </motion.div>
  );
}

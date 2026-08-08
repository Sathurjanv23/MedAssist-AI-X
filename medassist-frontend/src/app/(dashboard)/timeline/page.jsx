"use client";
import { useState } from "react";
import { motion } from "framer-motion";
import { useQuery } from "@tanstack/react-query";
import { FileText, Pill, Activity, TrendingUp, AlertTriangle, Zap, CheckCircle2, Filter, Search, Loader2 } from "lucide-react";
import { PageTransition } from "@/components/common";
import { timelineApi } from "@/lib/api";
import { formatDate, timeAgo } from "@/lib/utils";

const eventConfig = {
  REPORT_UPLOADED: { icon: FileText, color: "text-indigo-400", bg: "bg-indigo-500/15 border-indigo-500/25", label: "Report" },
  MEDICATION_STARTED: { icon: Pill, color: "text-emerald-400", bg: "bg-emerald-500/15 border-emerald-500/25", label: "Medicine" },
  DOCTOR_VISIT: { icon: Activity, color: "text-cyan-400", bg: "bg-cyan-500/15 border-cyan-500/25", label: "Doctor" },
  HEALTH_IMPROVEMENT: { icon: TrendingUp, color: "text-emerald-400", bg: "bg-emerald-500/15 border-emerald-500/25", label: "Progress" },
  RISK_DETECTED: { icon: AlertTriangle, color: "text-red-400", bg: "bg-red-500/15 border-red-500/25", label: "Alert" },
  SYMPTOM_LOG: { icon: Zap, color: "text-amber-400", bg: "bg-amber-500/15 border-amber-500/25", label: "Symptom" },
  VACCINATION: { icon: CheckCircle2, color: "text-violet-400", bg: "bg-violet-500/15 border-violet-500/25", label: "Vaccine" },
  SYSTEM: { icon: Activity, color: "text-primary", bg: "bg-primary/15 border-primary/25", label: "System" },
};

const filterOptions = ["All", "Report", "Medicine", "Doctor", "Progress", "Alert", "Symptom"];

export default function TimelinePage() {
  const [activeFilter, setActiveFilter] = useState("All");
  const [search, setSearch] = useState("");

  const { data: pageData, isLoading } = useQuery({
    queryKey: ["timeline", 0],
    queryFn: () => timelineApi.getTimeline(0, 50),
    retry: 1,
  });

  const timelineEvents = pageData?.content ?? [];

  const filteredEvents = timelineEvents.filter((evt) => {
    const config = eventConfig[evt.eventType] || eventConfig.SYSTEM;
    const matchFilter = activeFilter === "All" || config.label === activeFilter;
    const matchSearch = evt.title.toLowerCase().includes(search.toLowerCase()) || 
                       (evt.description || "").toLowerCase().includes(search.toLowerCase());
    return matchFilter && matchSearch;
  });

  return (
    <PageTransition>
      <div className="max-w-3xl mx-auto space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-foreground">Health Timeline</h1>
          <p className="text-muted-foreground text-sm mt-1">
            Your complete health journey — every report, visit, and milestone.
          </p>
        </div>

        {/* Search + Filter */}
        <div className="flex flex-col sm:flex-row gap-3">
          <div className="relative flex-1">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
            <input 
              value={search} 
              onChange={(e) => setSearch(e.target.value)} 
              placeholder="Search timeline..." 
              className="w-full h-10 pl-9 pr-4 bg-card border border-border rounded-xl text-sm text-foreground placeholder:text-muted-foreground outline-none focus:border-primary/50 transition-colors" 
            />
          </div>
          <div className="flex gap-1.5 overflow-x-auto">
            {filterOptions.map((f) => (
              <button 
                key={f} 
                onClick={() => setActiveFilter(f)} 
                className={`text-xs font-medium px-3 py-2 rounded-lg whitespace-nowrap transition-all ${
                  activeFilter === f ? "bg-primary/15 text-primary border border-primary/30" : "bg-card border border-border text-muted-foreground hover:text-foreground"
                }`}
              >
                {f}
              </button>
            ))}
          </div>
        </div>

        {/* Timeline */}
        {isLoading ? (
          <div className="flex justify-center py-12"><Loader2 className="w-8 h-8 animate-spin text-primary" /></div>
        ) : timelineEvents.length === 0 ? (
          <div className="text-center py-16 text-muted-foreground border border-dashed border-border rounded-2xl">
            <Activity className="w-12 h-12 mx-auto mb-4 opacity-50 text-primary" />
            <p className="font-medium text-foreground">Your timeline is empty</p>
            <p className="text-sm mt-1">Actions like uploading reports or adding medicines will appear here.</p>
          </div>
        ) : (
          <div className="relative">
            <div className="timeline-line absolute left-6 top-4 bottom-4" />
            <div className="space-y-4">
              {filteredEvents.map((event, i) => {
                const config = eventConfig[event.eventType] || eventConfig.SYSTEM;
                return (
                  <motion.div key={event.id} initial={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: i * 0.07, duration: 0.4 }} className="relative flex gap-5 pl-14">
                    {/* Event dot */}
                    <div className={`timeline-dot absolute left-3.5 top-4 w-5 h-5 rounded-full ${config.bg} border flex items-center justify-center`}>
                      <config.icon className={`w-2.5 h-2.5 ${config.color}`} />
                    </div>

                    {/* Event card */}
                    <div className="flex-1 bg-card border border-border rounded-xl p-4 hover:border-primary/25 transition-all hover:shadow-lg hover:shadow-primary/5">
                      <div className="flex items-start justify-between gap-3">
                        <div className="flex-1">
                          <div className="flex items-center gap-2 mb-1">
                            <span className={`text-xs font-semibold px-2 py-0.5 rounded-full border ${config.bg} ${config.color}`}>
                              {config.label}
                            </span>
                          </div>
                          <h3 className="font-semibold text-foreground">{event.title}</h3>
                          <p className="text-sm text-muted-foreground mt-1">{event.description}</p>
                        </div>
                        <div className="text-right shrink-0">
                          <p className="text-xs font-medium text-foreground">{formatDate(event.eventDate, "short")}</p>
                          <p className="text-xs text-muted-foreground">{timeAgo(event.eventDate)}</p>
                        </div>
                      </div>
                    </div>
                  </motion.div>
                );
              })}

              {filteredEvents.length === 0 && (
                <div className="text-center py-12 text-muted-foreground">
                  <Filter className="w-10 h-10 mx-auto mb-3 opacity-30" />
                  <p>No events match your filter</p>
                </div>
              )}
            </div>
          </div>
        )}
      </div>
    </PageTransition>
  );
}

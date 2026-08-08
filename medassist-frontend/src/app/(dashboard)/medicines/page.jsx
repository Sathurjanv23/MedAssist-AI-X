"use client";
import { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Pill, Plus, Clock, CheckCircle2, AlertTriangle, X, Loader2, Trash2 } from "lucide-react";
import { GlowCard, PageTransition, SectionHeader } from "@/components/common";
import { medicinesApi } from "@/lib/api";
import { formatDate } from "@/lib/utils";

const frequencyLabel = {
  ONCE_DAILY: "Once daily",
  TWICE_DAILY: "Twice daily",
  THREE_TIMES_DAILY: "3× daily",
  AS_NEEDED: "As needed",
};

export default function MedicinesPage() {
  const queryClient = useQueryClient();
  const [showAdd, setShowAdd] = useState(false);
  const [newMed, setNewMed] = useState({ medicineName: "", dosage: "", frequency: "ONCE_DAILY", notes: "", reminderTimes: ["08:00"] });

  const { data: medicines = [], isLoading } = useQuery({
    queryKey: ["medicines-all"],
    queryFn: () => medicinesApi.getMedicines(false),
    retry: 1,
  });

  const addMutation = useMutation({
    mutationFn: medicinesApi.addMedicine,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["medicines-all"] });
      queryClient.invalidateQueries({ queryKey: ["medicines"] });
      setNewMed({ medicineName: "", dosage: "", frequency: "ONCE_DAILY", notes: "", reminderTimes: ["08:00"] });
      setShowAdd(false);
    },
  });

  const deleteMutation = useMutation({
    mutationFn: medicinesApi.deleteMedicine,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["medicines-all"] });
      queryClient.invalidateQueries({ queryKey: ["medicines"] });
    },
  });

  const addMedicine = () => {
    if (!newMed.medicineName.trim()) return;
    addMutation.mutate({
      medicineName: newMed.medicineName,
      dosage: newMed.dosage,
      frequency: newMed.frequency,
      notes: newMed.notes,
      reminderTimes: newMed.reminderTimes,
      startDate: new Date().toISOString().split("T")[0],
    });
  };

  const todayReminders = medicines.filter((m) => m.status === "ACTIVE");

  return (
    <PageTransition>
      <div className="max-w-4xl mx-auto space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-foreground">Medicine Management</h1>
            <p className="text-muted-foreground text-sm mt-1">Track your medications and set smart AI reminders.</p>
          </div>
          <motion.button whileHover={{ scale: 1.03 }} whileTap={{ scale: 0.97 }} onClick={() => setShowAdd(true)} className="btn-neon px-5 py-2.5 rounded-xl font-semibold text-white text-sm flex items-center gap-2">
            <Plus className="w-4 h-4" />
            Add Medicine
          </motion.button>
        </div>

        {isLoading ? (
          <div className="flex justify-center py-16"><Loader2 className="w-8 h-8 animate-spin text-primary" /></div>
        ) : (
          <>
            {/* Today's Schedule */}
            <GlowCard delay={0} glowColor="emerald" noPadding>
              <div className="p-5">
                <SectionHeader title="Today's Schedule" badge={todayReminders.length > 0 ? `${todayReminders.length} Active` : undefined} />
                {todayReminders.length === 0 ? (
                  <div className="flex flex-col items-center py-8 text-center">
                    <Pill className="w-10 h-10 text-muted-foreground mb-3" />
                    <p className="text-sm font-medium text-foreground">No active medicines</p>
                    <p className="text-xs text-muted-foreground mt-1">Add your first medicine to start tracking</p>
                  </div>
                ) : (
                  <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
                    {todayReminders.map((med, i) => (
                      <motion.div key={med.id} initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: i * 0.07 }} className="p-4 rounded-xl bg-muted/50 border border-border hover:border-emerald-500/30 transition-all">
                        <div className="flex items-start justify-between gap-2">
                          <div className="w-10 h-10 rounded-xl bg-emerald-500/10 border border-emerald-500/20 flex items-center justify-center shrink-0">
                            <Pill className="w-5 h-5 text-emerald-400" />
                          </div>
                          <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0 mt-1" />
                        </div>
                        <p className="font-semibold text-foreground mt-3 text-sm">{med.medicineName}</p>
                        <p className="text-xs text-muted-foreground mt-0.5">{med.dosage}</p>
                        <div className="flex items-center gap-1.5 mt-2">
                          <Clock className="w-3 h-3 text-muted-foreground" />
                          <span className="text-xs text-muted-foreground">{med.reminderTimes?.join(", ")}</span>
                        </div>
                        <div className="mt-2">
                          <span className="text-[10px] font-medium text-emerald-400 bg-emerald-400/10 border border-emerald-400/20 px-2 py-0.5 rounded-full">
                            {frequencyLabel[med.frequency]}
                          </span>
                        </div>
                      </motion.div>
                    ))}
                  </div>
                )}
              </div>
            </GlowCard>

            {/* All Medicines */}
            <GlowCard delay={0.1} glowColor="indigo" noPadding>
              <div className="p-5">
                <SectionHeader title="All Medicines" />
                {medicines.length === 0 ? (
                  <div className="flex flex-col items-center py-8 text-center">
                    <p className="text-sm text-muted-foreground">No medicines added yet. Click "Add Medicine" to begin tracking.</p>
                  </div>
                ) : (
                  <div className="space-y-3">
                    {medicines.map((med, i) => (
                      <motion.div key={med.id} initial={{ opacity: 0, x: -10 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: 0.1 + i * 0.06 }} className="flex items-center gap-4 p-4 rounded-xl bg-muted/30 border border-border hover:border-primary/25 transition-all">
                        <div className="w-10 h-10 rounded-xl bg-indigo-500/10 border border-indigo-500/20 flex items-center justify-center shrink-0">
                          <Pill className="w-5 h-5 text-indigo-400" />
                        </div>
                        <div className="flex-1">
                          <p className="font-semibold text-foreground">{med.medicineName}</p>
                          <p className="text-xs text-muted-foreground">
                            {med.dosage} · {frequencyLabel[med.frequency]} · Since {med.startDate ? formatDate(med.startDate, "short") : "—"}
                          </p>
                          {med.notes && <p className="text-xs text-amber-400 mt-0.5">{med.notes}</p>}
                        </div>
                        <div className="flex items-center gap-2">
                          <span className={`text-xs px-2.5 py-1 rounded-full border font-medium ${med.status === "ACTIVE" ? "text-emerald-400 bg-emerald-400/10 border-emerald-400/25" : "text-muted-foreground bg-muted border-border"}`}>
                            {med.status}
                          </span>
                          <button onClick={() => deleteMutation.mutate(med.id)} className="w-7 h-7 rounded-lg hover:bg-red-500/10 flex items-center justify-center text-muted-foreground hover:text-red-400 transition-colors">
                            <Trash2 className="w-3.5 h-3.5" />
                          </button>
                        </div>
                      </motion.div>
                    ))}
                  </div>
                )}
              </div>
            </GlowCard>
          </>
        )}

        {/* AI Disclaimer */}
        <div className="p-4 rounded-xl border border-amber-500/20 bg-amber-500/5 flex gap-3">
          <AlertTriangle className="w-4 h-4 text-amber-400 shrink-0 mt-0.5" />
          <p className="text-xs text-amber-400/90">
            <span className="font-semibold">Medicine Disclaimer:</span> Never add or stop medications without consulting your doctor.
            MedAssist AI X provides reminders and information only — not medical prescriptions.
          </p>
        </div>

        {/* Add Medicine Modal */}
        <AnimatePresence>
          {showAdd && (
            <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4" onClick={(e) => e.target === e.currentTarget && setShowAdd(false)}>
              <motion.div initial={{ scale: 0.9, opacity: 0 }} animate={{ scale: 1, opacity: 1 }} exit={{ scale: 0.9, opacity: 0 }} className="bg-card border border-border rounded-2xl p-6 w-full max-w-md shadow-2xl">
                <div className="flex items-center justify-between mb-5">
                  <h2 className="font-bold text-foreground text-lg">Add Medicine</h2>
                  <button onClick={() => setShowAdd(false)} className="w-8 h-8 rounded-lg hover:bg-muted flex items-center justify-center text-muted-foreground">
                    <X className="w-4 h-4" />
                  </button>
                </div>
                <div className="space-y-4">
                  {[
                    { label: "Medicine Name", key: "medicineName", placeholder: "e.g., Metformin 500mg" },
                    { label: "Dosage", key: "dosage", placeholder: "e.g., 500mg" },
                    { label: "Notes", key: "notes", placeholder: "e.g., Take with food" },
                  ].map((field) => (
                    <div key={field.key}>
                      <label className="text-xs font-medium text-muted-foreground mb-1.5 block">{field.label}</label>
                      <input value={newMed[field.key]} onChange={(e) => setNewMed((prev) => ({ ...prev, [field.key]: e.target.value }))} placeholder={field.placeholder} className="w-full h-10 px-3 bg-muted border border-border rounded-xl text-sm text-foreground placeholder:text-muted-foreground outline-none focus:border-primary/50 transition-colors" />
                    </div>
                  ))}
                  <div>
                    <label className="text-xs font-medium text-muted-foreground mb-1.5 block">Frequency</label>
                    <select value={newMed.frequency} onChange={(e) => setNewMed((prev) => ({ ...prev, frequency: e.target.value }))} className="w-full h-10 px-3 bg-muted border border-border rounded-xl text-sm text-foreground outline-none focus:border-primary/50 transition-colors">
                      {Object.entries(frequencyLabel).map(([k, v]) => <option key={k} value={k}>{v}</option>)}
                    </select>
                  </div>
                  {addMutation.error && (
                    <p className="text-xs text-red-400 bg-red-500/5 border border-red-500/20 rounded-lg px-3 py-2">
                      {addMutation.error?.response?.data?.message ?? "Failed to add medicine. Is the backend running?"}
                    </p>
                  )}
                  <motion.button whileHover={{ scale: 1.02 }} whileTap={{ scale: 0.98 }} onClick={addMedicine} disabled={addMutation.isPending} className="w-full btn-neon py-3 rounded-xl font-semibold text-white flex items-center justify-center gap-2 disabled:opacity-60">
                    {addMutation.isPending ? <Loader2 className="w-4 h-4 animate-spin" /> : <><Plus className="w-4 h-4" /> Add Medicine</>}
                  </motion.button>
                </div>
              </motion.div>
            </motion.div>
          )}
        </AnimatePresence>
      </div>
    </PageTransition>
  );
}

"use client";
import { useState, useCallback } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { useDropzone } from "react-dropzone";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Upload, FileText, CheckCircle2, AlertTriangle, Info, ChevronDown, ChevronUp, Download, RefreshCw, Microscope, Sparkles, MessageSquare, Loader2, Trash2 } from "lucide-react";
import Link from "next/link";
import { GlowCard, RiskBadge, AIOrb, PageTransition, SectionHeader } from "@/components/common";
import { reportsApi } from "@/lib/api";
import { formatFileSize, timeAgo } from "@/lib/utils";

// Processing steps for UI feedback during upload
const AI_PROCESSING_STEPS = [
  { id: "upload", label: "Uploading document securely...", duration: 800 },
  { id: "ocr", label: "Extracting medical text (OCR)...", duration: 1200 },
  { id: "nlp", label: "Analyzing clinical markers...", duration: 1500 },
  { id: "risk", label: "Calculating health risks...", duration: 1000 },
];

export default function ReportsPage() {
  const queryClient = useQueryClient();
  const [uploadState, setUploadState] = useState("idle"); // idle, uploading, done, error
  const [uploadError, setUploadError] = useState("");
  const [currentStep, setCurrentStep] = useState(0);
  const [selectedReportId, setSelectedReportId] = useState(null);
  const [expandedFindings, setExpandedFindings] = useState(new Set());

  const { data: reportsPage, isLoading } = useQuery({
    queryKey: ["reports", 0],
    queryFn: () => reportsApi.getReports(0, 50),
    retry: 1,
  });

  const reports = reportsPage?.content ?? [];
  
  // Set default selection when reports load
  if (!selectedReportId && reports.length > 0) {
    setSelectedReportId(reports[0].id);
  }

  const selectedReport = reports.find(r => r.id === selectedReportId) || reports[0];

  const uploadMutation = useMutation({
    mutationFn: (file) => reportsApi.uploadReport(file, "GENERAL"),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ["reports"] });
      queryClient.invalidateQueries({ queryKey: ["health-twin"] });
      setSelectedReportId(data.id);
      setUploadState("done");
      setTimeout(() => setUploadState("idle"), 3000);
    },
    onError: (err) => {
      setUploadState("error");
      setUploadError(err?.response?.data?.message || "Failed to upload report");
    }
  });

  const deleteMutation = useMutation({
    mutationFn: reportsApi.deleteReport,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["reports"] });
      queryClient.invalidateQueries({ queryKey: ["health-twin"] });
      setSelectedReportId(null);
    }
  });

  const onDrop = useCallback(async (acceptedFiles) => {
    if (acceptedFiles.length === 0) return;
    const file = acceptedFiles[0];
    
    setUploadState("uploading");
    setUploadError("");
    setCurrentStep(0);
    
    // Simulate steps for UI polish, then do real upload
    const runSteps = async () => {
      for (let i = 0; i < AI_PROCESSING_STEPS.length; i++) {
        setCurrentStep(i);
        await new Promise((r) => setTimeout(r, AI_PROCESSING_STEPS[i].duration));
      }
    };
    
    Promise.all([runSteps(), uploadMutation.mutateAsync(file)]).catch(() => {});
  }, [uploadMutation]);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: { "application/pdf": [".pdf"], "image/jpeg": [".jpg", ".jpeg"], "image/png": [".png"] },
    maxSize: 20 * 1024 * 1024,
    multiple: false,
  });

  const toggleFinding = (id) => {
    setExpandedFindings((prev) => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id);
      else next.add(id);
      return next;
    });
  };

  return (
    <PageTransition>
      <div className="max-w-[1400px] mx-auto space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-foreground">Medical Reports</h1>
          <p className="text-muted-foreground text-sm mt-1">Upload reports for instant AI analysis and twin updates.</p>
        </div>

        {/* ── Uploader ────────────────────────────────────────────── */}
        <GlowCard delay={0} glowColor="indigo" noPadding>
          <div className="p-6">
            <AnimatePresence mode="wait">
              {uploadState === "idle" || uploadState === "error" ? (
                <motion.div key="idle" initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} {...getRootProps()} className={`relative border-2 border-dashed rounded-2xl p-10 text-center transition-all cursor-pointer ${isDragActive ? "border-primary bg-primary/5" : "border-border hover:border-primary/50 hover:bg-muted/30"}`}>
                  <input {...getInputProps()} />
                  <div className="w-16 h-16 mx-auto bg-primary/10 rounded-full flex items-center justify-center mb-4">
                    <Upload className="w-8 h-8 text-primary" />
                  </div>
                  <h3 className="text-lg font-bold text-foreground">Drag & Drop Report Here</h3>
                  <p className="text-sm text-muted-foreground mt-2 max-w-sm mx-auto">Supports PDF, JPG, PNG up to 20MB. Your data is securely encrypted.</p>
                  {uploadState === "error" && (
                    <p className="mt-4 text-sm font-medium text-red-400 bg-red-500/10 py-2 px-4 rounded-lg inline-block">{uploadError}</p>
                  )}
                  <button className="mt-6 btn-neon px-6 py-2.5 rounded-xl font-semibold text-white text-sm">Select File</button>
                </motion.div>
              ) : uploadState === "uploading" ? (
                <motion.div key="uploading" initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} className="py-12 px-6">
                  <div className="max-w-md mx-auto">
                    <div className="flex items-center justify-center mb-8">
                      <div className="relative">
                        <AIOrb size={80} />
                        <motion.div animate={{ rotate: 360 }} transition={{ duration: 4, repeat: Infinity, ease: "linear" }} className="absolute inset-0 rounded-full border-t-2 border-primary" />
                      </div>
                    </div>
                    <div className="space-y-4">
                      {AI_PROCESSING_STEPS.map((step, i) => (
                        <div key={step.id} className={`flex items-center gap-3 transition-opacity duration-300 ${i === currentStep ? "opacity-100" : i < currentStep ? "opacity-50" : "opacity-20"}`}>
                          <div className={`w-6 h-6 rounded-full flex items-center justify-center shrink-0 ${i < currentStep ? "bg-emerald-500/20 text-emerald-400" : i === currentStep ? "bg-primary/20 text-primary" : "bg-muted text-muted-foreground"}`}>
                            {i < currentStep ? <CheckCircle2 className="w-4 h-4" /> : i === currentStep ? <RefreshCw className="w-3.5 h-3.5 animate-spin" /> : <div className="w-2 h-2 rounded-full bg-current" />}
                          </div>
                          <span className={`text-sm font-medium ${i === currentStep ? "text-foreground" : "text-muted-foreground"}`}>{step.label}</span>
                        </div>
                      ))}
                    </div>
                  </div>
                </motion.div>
              ) : (
                <motion.div key="done" initial={{ opacity: 0, scale: 0.95 }} animate={{ opacity: 1, scale: 1 }} className="py-16 text-center">
                  <div className="w-20 h-20 mx-auto bg-emerald-500/10 rounded-full flex items-center justify-center mb-5">
                    <CheckCircle2 className="w-10 h-10 text-emerald-400" />
                  </div>
                  <h3 className="text-xl font-bold text-foreground">Analysis Complete!</h3>
                  <p className="text-sm text-muted-foreground mt-2">Your health twin has been updated.</p>
                </motion.div>
              )}
            </AnimatePresence>
          </div>
        </GlowCard>

        {/* ── Reports List & Viewer ────────────────────────────────── */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Left sidebar: List */}
          <div className="lg:col-span-1 space-y-3">
            <SectionHeader title="Your Reports" badge={reports.length > 0 ? reports.length : undefined} />
            {isLoading ? (
              <div className="flex justify-center py-8"><Loader2 className="w-6 h-6 animate-spin text-muted-foreground" /></div>
            ) : reports.length === 0 ? (
              <div className="text-center py-8 border border-dashed border-border rounded-xl">
                <p className="text-sm text-muted-foreground">No reports uploaded yet.</p>
              </div>
            ) : (
              reports.map((report) => (
                <button key={report.id} onClick={() => setSelectedReportId(report.id)} className={`w-full flex items-center gap-3 p-3 rounded-lg text-left transition-all ${selectedReport?.id === report.id ? "bg-primary/10 border border-primary/30 shadow-[0_0_15px_rgba(var(--primary-rgb),0.1)]" : "bg-card border border-border hover:border-primary/20 hover:bg-muted/30"}`}>
                  <div className={`w-10 h-10 rounded-lg flex items-center justify-center shrink-0 ${selectedReport?.id === report.id ? "bg-primary/20" : "bg-indigo-500/10"}`}>
                    <FileText className={`w-5 h-5 ${selectedReport?.id === report.id ? "text-primary" : "text-indigo-400"}`} />
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className={`text-sm font-semibold truncate ${selectedReport?.id === report.id ? "text-primary" : "text-foreground"}`}>
                      {report.originalFileName ?? report.fileName}
                    </p>
                    <p className="text-xs text-muted-foreground truncate">{timeAgo(report.createdAt)}</p>
                  </div>
                  {report.analysisResult?.riskLevel && <RiskBadge level={report.analysisResult.riskLevel} size="sm" />}
                </button>
              ))
            )}
          </div>

          {/* Right side: Viewer */}
          <div className="lg:col-span-2">
            {!selectedReport ? (
              <GlowCard delay={0.1} glowColor="indigo" className="h-full min-h-[400px] flex items-center justify-center text-center">
                <div className="max-w-xs mx-auto">
                  <Microscope className="w-12 h-12 text-muted-foreground mx-auto mb-4 opacity-50" />
                  <p className="font-semibold text-foreground">Select a report</p>
                  <p className="text-sm text-muted-foreground mt-1">Choose a report from the list to view its AI analysis.</p>
                </div>
              </GlowCard>
            ) : (
              <GlowCard delay={0.1} glowColor={selectedReport.analysisResult?.riskLevel === "HIGH" ? "amber" : "cyan"} noPadding className="h-full">
                {/* Header */}
                <div className="p-5 border-b border-border flex items-start justify-between bg-muted/20">
                  <div className="flex items-center gap-4">
                    <div className="w-12 h-12 rounded-xl bg-indigo-500/10 border border-indigo-500/20 flex items-center justify-center shrink-0">
                      <FileText className="w-6 h-6 text-indigo-400" />
                    </div>
                    <div>
                      <h2 className="text-lg font-bold text-foreground">{selectedReport.originalFileName ?? selectedReport.fileName}</h2>
                      <div className="flex items-center gap-3 text-xs text-muted-foreground mt-1">
                        <span>Uploaded {new Date(selectedReport.createdAt).toLocaleDateString()}</span>
                        {selectedReport.fileSize && <span>{formatFileSize(selectedReport.fileSize)}</span>}
                      </div>
                    </div>
                  </div>
                  <div className="flex gap-2">
                    {selectedReport.fileUrl && (
                      <a href={selectedReport.fileUrl} target="_blank" rel="noreferrer" className="w-9 h-9 rounded-lg bg-card border border-border flex items-center justify-center text-foreground hover:bg-muted transition-colors">
                        <Download className="w-4 h-4" />
                      </a>
                    )}
                    <button onClick={() => deleteMutation.mutate(selectedReport.id)} disabled={deleteMutation.isPending} className="w-9 h-9 rounded-lg bg-card border border-border flex items-center justify-center text-red-400 hover:bg-red-500/10 hover:border-red-500/20 transition-colors">
                      {deleteMutation.isPending ? <Loader2 className="w-4 h-4 animate-spin" /> : <Trash2 className="w-4 h-4" />}
                    </button>
                  </div>
                </div>

                <div className="p-6">
                  {selectedReport.status === "PROCESSING" ? (
                    <div className="py-12 text-center">
                      <Loader2 className="w-8 h-8 animate-spin text-primary mx-auto mb-4" />
                      <p className="text-sm font-medium text-foreground">AI is analyzing this report...</p>
                      <p className="text-xs text-muted-foreground mt-1">Check back in a few moments.</p>
                    </div>
                  ) : !selectedReport.analysisResult ? (
                    <div className="py-12 text-center text-muted-foreground">
                      <AlertTriangle className="w-8 h-8 mx-auto mb-3 opacity-50" />
                      <p>No AI analysis available for this report.</p>
                    </div>
                  ) : (
                    <>
                      {/* Analysis Header */}
                      <div className="flex items-center justify-between mb-6">
                        <div className="flex items-center gap-2">
                          <AIOrb size={24} />
                          <h3 className="font-bold text-foreground">AI Analysis</h3>
                        </div>
                        <RiskBadge level={selectedReport.analysisResult.riskLevel} />
                      </div>

                      {/* Summary */}
                      <div className="p-4 rounded-xl bg-primary/5 border border-primary/10 mb-6 relative overflow-hidden">
                        <Sparkles className="absolute -top-2 -right-2 w-16 h-16 text-primary/10" />
                        <p className="text-sm text-foreground leading-relaxed relative z-10">
                          {selectedReport.analysisResult.summary}
                        </p>
                      </div>

                      {/* Extracted Values */}
                      {selectedReport.analysisResult.extractedValues && Object.keys(selectedReport.analysisResult.extractedValues).length > 0 && (
                        <div className="mb-6">
                          <h4 className="text-xs font-semibold text-muted-foreground uppercase tracking-wider mb-3">Extracted Biomarkers</h4>
                          <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
                            {Object.entries(selectedReport.analysisResult.extractedValues).map(([key, value]) => (
                              <div key={key} className="p-3 bg-muted/40 rounded-lg border border-border">
                                <p className="text-[10px] text-muted-foreground uppercase">{key}</p>
                                <p className="text-sm font-bold text-foreground mt-0.5">{value}</p>
                              </div>
                            ))}
                          </div>
                        </div>
                      )}

                      {/* Findings */}
                      {selectedReport.analysisResult.findings && selectedReport.analysisResult.findings.length > 0 && (
                        <div className="mb-6">
                          <h4 className="text-xs font-semibold text-muted-foreground uppercase tracking-wider mb-3">Key Findings</h4>
                          <div className="space-y-3">
                            {selectedReport.analysisResult.findings.map((finding, idx) => (
                              <div key={idx} className="border border-border rounded-xl overflow-hidden bg-card">
                                <button onClick={() => toggleFinding(idx.toString())} className="w-full p-4 flex items-center justify-between hover:bg-muted/30 transition-colors text-left">
                                  <div className="flex items-center gap-3">
                                    <div className={`w-2 h-2 rounded-full ${finding.isCritical ? "bg-red-400 shadow-[0_0_8px_rgba(248,113,113,0.5)]" : "bg-emerald-400"}`} />
                                    <span className="font-semibold text-sm text-foreground">{finding.observation}</span>
                                  </div>
                                  {expandedFindings.has(idx.toString()) ? <ChevronUp className="w-4 h-4 text-muted-foreground" /> : <ChevronDown className="w-4 h-4 text-muted-foreground" />}
                                </button>
                                <AnimatePresence>
                                  {expandedFindings.has(idx.toString()) && (
                                    <motion.div initial={{ height: 0, opacity: 0 }} animate={{ height: "auto", opacity: 1 }} exit={{ height: 0, opacity: 0 }} className="border-t border-border bg-muted/20">
                                      <div className="p-4 space-y-3 text-sm">
                                        <div className="flex gap-2">
                                          <Info className="w-4 h-4 text-cyan-400 shrink-0 mt-0.5" />
                                          <p className="text-muted-foreground"><span className="text-foreground font-medium block">AI Interpretation:</span> {finding.implication}</p>
                                        </div>
                                        {finding.recommendation && (
                                          <div className="flex gap-2">
                                            <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0 mt-0.5" />
                                            <p className="text-muted-foreground"><span className="text-foreground font-medium block">Recommendation:</span> {finding.recommendation}</p>
                                          </div>
                                        )}
                                      </div>
                                    </motion.div>
                                  )}
                                </AnimatePresence>
                              </div>
                            ))}
                          </div>
                        </div>
                      )}

                      {/* Chat Link */}
                      <Link href="/ai-chat" className="mt-6 w-full p-4 rounded-xl border border-primary/20 bg-primary/5 hover:bg-primary/10 transition-colors flex items-center justify-between group">
                        <div className="flex items-center gap-3">
                          <div className="w-10 h-10 rounded-lg bg-primary/10 flex items-center justify-center">
                            <MessageSquare className="w-5 h-5 text-primary" />
                          </div>
                          <div>
                            <p className="font-semibold text-sm text-foreground">Discuss this report</p>
                            <p className="text-xs text-muted-foreground mt-0.5">Ask MedAssist AI for deeper insights</p>
                          </div>
                        </div>
                        <div className="w-8 h-8 rounded-full bg-card flex items-center justify-center group-hover:translate-x-1 transition-transform">
                          <ChevronDown className="w-4 h-4 -rotate-90 text-primary" />
                        </div>
                      </Link>
                    </>
                  )}
                </div>
              </GlowCard>
            )}
          </div>
        </div>
      </div>
    </PageTransition>
  );
}

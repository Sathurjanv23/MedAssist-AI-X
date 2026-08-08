"use client";
import { motion, useScroll, useTransform } from "framer-motion";
import Link from "next/link";
import { useRef, useState, useEffect } from "react";
import { Brain, FileText, Activity, Zap, Heart, ArrowRight, ChevronRight, Lock, Eye, BadgeCheck, Upload, Lightbulb, TrendingUp, AlertCircle, MessageSquare, Sun, Moon, } from "lucide-react";
import { useTheme } from "next-themes";
// ─────────────────────────────────────────────────────────────────────────────
// Landing Page
// ─────────────────────────────────────────────────────────────────────────────
const aiFeatures = [
    {
        icon: Brain,
        title: "AI Health Twin",
        description: "A living digital replica of your health — tracking vitals, risks, and lifestyle score in real time.",
        color: "from-indigo-500 to-violet-600",
        accent: "indigo",
        badge: "Showstopper",
    },
    {
        icon: FileText,
        title: "Medical Report Intelligence",
        description: "Upload any lab report, prescription or scan. AI extracts, explains, and flags what matters.",
        color: "from-cyan-500 to-blue-600",
        accent: "cyan",
        badge: "Core Feature",
    },
    {
        icon: MessageSquare,
        title: "AI Doctor Memory",
        description: "Your AI remembers every visit, medication, and insight — so your doctor never starts from scratch.",
        color: "from-emerald-500 to-teal-600",
        accent: "emerald",
        badge: "Unique",
    },
    {
        icon: TrendingUp,
        title: "AI Health Forecast",
        description: "Predict health risks weeks ahead using your personal health patterns and AI modeling.",
        color: "from-amber-500 to-orange-600",
        accent: "amber",
        badge: "AI-Powered",
    },
    {
        icon: AlertCircle,
        title: "AI Emergency Guardian",
        description: "One-tap emergency card showing blood group, allergies, medications, and emergency contacts.",
        color: "from-red-500 to-rose-600",
        accent: "red",
        badge: "Life-Saving",
    },
    {
        icon: Lightbulb,
        title: "AI Daily Copilot",
        description: "Conversational AI that answers health questions, explains reports, and gives daily insights.",
        color: "from-purple-500 to-pink-600",
        accent: "purple",
        badge: "ChatGPT-Style",
    },
];
const steps = [
    { step: "01", title: "Upload", description: "Drop any medical document — lab reports, prescriptions, scans.", icon: Upload },
    { step: "02", title: "Understand", description: "AI extracts and explains every value in simple language.", icon: Brain },
    { step: "03", title: "Predict", description: "Forecast health risks based on your personal patterns.", icon: TrendingUp },
    { step: "04", title: "Improve", description: "Get personalized, actionable recommendations every day.", icon: Heart },
];
const stats = [
    { value: "20+", label: "AI Health Features" },
    { value: "3", label: "Languages Supported" },
    { value: "99.9%", label: "Uptime SLA" },
    { value: "256-bit", label: "Encryption" },
];
const trustBadges = [
    { icon: Lock, title: "End-to-End Encrypted", description: "All medical data is encrypted at rest and in transit with AES-256.", color: "text-cyan-400" },
    { icon: Eye, title: "Privacy First", description: "Your data never trains AI models. You own your health data.", color: "text-indigo-400" },
    { icon: BadgeCheck, title: "Responsible AI", description: "AI provides insights, not diagnoses. Always verified by professionals.", color: "text-emerald-400" },
];
export default function LandingPage() {
    const { theme, setTheme } = useTheme();
    const heroRef = useRef(null);
    const { scrollYProgress } = useScroll({
        target: heroRef,
        offset: ["start start", "end start"],
    });
    const heroY = useTransform(scrollYProgress, [0, 1], ["0%", "40%"]);
    const heroOpacity = useTransform(scrollYProgress, [0, 0.7], [1, 0]);
    const [activeFeature, setActiveFeature] = useState(null);
    const [mounted, setMounted] = useState(false);
    useEffect(() => setMounted(true), []);
    return (<div className="min-h-screen bg-background overflow-x-hidden">
      {/* ── Sticky Navbar ────────────────────────────────────────────── */}
      <nav className="fixed top-0 left-0 right-0 z-50 h-16">
        <div className="glass border-b border-white/5 h-full">
          <div className="container-app h-full flex items-center justify-between">
            <Link href="/" className="flex items-center gap-2.5">
              <div className="w-9 h-9 rounded-xl gradient-brand flex items-center justify-center shadow-lg shadow-cyan-500/20">
                <Activity className="w-5 h-5 text-white"/>
              </div>
              <span className="font-bold text-foreground">
                MedAssist <span className="gradient-brand-text">AI X</span>
              </span>
            </Link>

            <div className="hidden md:flex items-center gap-6 text-sm text-muted-foreground">
              <a href="#features" className="hover:text-foreground transition-colors">Features</a>
              <a href="#how-it-works" className="hover:text-foreground transition-colors">How it Works</a>
              <a href="#trust" className="hover:text-foreground transition-colors">Security</a>
            </div>

            <div className="flex items-center gap-3">
              <button onClick={() => setTheme(theme === "dark" ? "light" : "dark")} className="w-9 h-9 rounded-lg hover:bg-muted flex items-center justify-center text-muted-foreground hover:text-foreground transition-colors">
                {mounted && (theme === "dark" ? <Sun className="w-4 h-4"/> : <Moon className="w-4 h-4"/>)}
              </button>
              <Link href="/login" className="text-sm text-muted-foreground hover:text-foreground transition-colors">
                Sign in
              </Link>
              <Link href="/register" className="btn-neon text-sm font-semibold px-5 py-2 rounded-xl relative overflow-hidden">
                <span className="relative z-10">Get Started</span>
              </Link>
            </div>
          </div>
        </div>
      </nav>

      {/* ── Hero Section ─────────────────────────────────────────────── */}
      <section ref={heroRef} className="relative min-h-screen flex items-center pt-16">
        {/* Background */}
        <div className="absolute inset-0 gradient-hero"/>
        <div className="absolute inset-0 grid-pattern opacity-40"/>

        {/* Animated orbs */}
        <div className="absolute top-1/4 left-1/4 w-96 h-96 rounded-full bg-cyan-500/8 blur-3xl animate-float"/>
        <div className="absolute bottom-1/4 right-1/4 w-80 h-80 rounded-full bg-indigo-500/8 blur-3xl animate-float" style={{ animationDelay: "2s" }}/>
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-64 h-64 rounded-full bg-violet-500/5 blur-3xl animate-float" style={{ animationDelay: "1s" }}/>

        <motion.div style={{ y: heroY, opacity: heroOpacity }} className="container-app relative z-10 py-20">
          <div className="max-w-4xl mx-auto text-center">
            {/* Badge */}
            <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.5 }} className="inline-flex items-center gap-2 px-4 py-2 rounded-full border border-primary/30 bg-primary/8 text-primary text-sm font-medium mb-8">
              <Zap className="w-3.5 h-3.5 fill-primary"/>
              AI Challenge Sri Lanka 2026 Finalist
              <span className="w-1.5 h-1.5 rounded-full bg-primary animate-pulse"/>
            </motion.div>

            {/* Title */}
            <motion.h1 initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.6, delay: 0.1 }} className="text-5xl sm:text-6xl lg:text-7xl font-bold text-foreground leading-tight tracking-tight">
              Your Personal{" "}
              <span className="gradient-brand-text glow-text-cyan">AI Healthcare</span>
              <br />
              Operating System
            </motion.h1>

            {/* Subtitle */}
            <motion.p initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.6, delay: 0.2 }} className="mt-6 text-xl text-muted-foreground max-w-2xl mx-auto leading-relaxed">
              Understand your health. Predict risks. Make better healthcare decisions.
              <br />
              <span className="text-foreground/80">Powered by local AI — your data stays private.</span>
            </motion.p>

            {/* CTA Buttons */}
            <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.6, delay: 0.3 }} className="mt-10 flex flex-col sm:flex-row items-center justify-center gap-4">
              <Link href="/register">
                <motion.button whileHover={{ scale: 1.03 }} whileTap={{ scale: 0.97 }} className="btn-neon px-8 py-3.5 rounded-2xl font-semibold text-white text-base flex items-center gap-2.5 min-w-[200px] justify-center">
                  <Heart className="w-4.5 h-4.5"/>
                  Start Health Journey
                  <ArrowRight className="w-4 h-4"/>
                </motion.button>
              </Link>
              <Link href="#features">
                <motion.button whileHover={{ scale: 1.03, borderColor: "hsl(var(--primary) / 0.6)" }} whileTap={{ scale: 0.97 }} className="px-8 py-3.5 rounded-2xl font-semibold text-foreground text-base border border-border hover:bg-muted transition-all flex items-center gap-2.5 min-w-[200px] justify-center">
                  <Brain className="w-4.5 h-4.5 text-primary"/>
                  Explore AI Features
                </motion.button>
              </Link>
            </motion.div>

            {/* Social proof */}
            <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ duration: 0.8, delay: 0.5 }} className="mt-12 flex flex-wrap items-center justify-center gap-8">
              {stats.map((stat, i) => (<motion.div key={stat.label} initial={{ opacity: 0, scale: 0.8 }} animate={{ opacity: 1, scale: 1 }} transition={{ delay: 0.6 + i * 0.1 }} className="text-center">
                  <div className="text-2xl font-bold gradient-brand-text">{stat.value}</div>
                  <div className="text-xs text-muted-foreground mt-0.5">{stat.label}</div>
                </motion.div>))}
            </motion.div>
          </div>

          {/* Hero Dashboard Preview */}
          <motion.div initial={{ opacity: 0, y: 60 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.8, delay: 0.4 }} className="mt-16 max-w-5xl mx-auto">
            <HeroDashboardPreview />
          </motion.div>
        </motion.div>
      </section>

      {/* ── AI Features Section ───────────────────────────────────────── */}
      <section id="features" className="py-24 relative">
        <div className="container-app">
          <div className="text-center mb-16">
            <motion.span initial={{ opacity: 0 }} whileInView={{ opacity: 1 }} className="text-xs font-semibold text-primary bg-primary/10 border border-primary/20 rounded-full px-4 py-1.5 inline-block mb-4">
              20+ AI Features
            </motion.span>
            <motion.h2 initial={{ opacity: 0, y: 20 }} whileInView={{ opacity: 1, y: 0 }} className="text-4xl font-bold text-foreground">
              Everything your health needs,{" "}
              <span className="gradient-brand-text">powered by AI</span>
            </motion.h2>
            <motion.p initial={{ opacity: 0, y: 10 }} whileInView={{ opacity: 1, y: 0 }} transition={{ delay: 0.1 }} className="mt-4 text-muted-foreground max-w-xl mx-auto">
              From understanding lab reports to predicting health risks — MedAssist AI X is your complete healthcare companion.
            </motion.p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
            {aiFeatures.map((feature, i) => (<motion.div key={feature.title} initial={{ opacity: 0, y: 30 }} whileInView={{ opacity: 1, y: 0 }} transition={{ duration: 0.5, delay: i * 0.08 }} onHoverStart={() => setActiveFeature(i)} onHoverEnd={() => setActiveFeature(null)} className="relative group glass-card p-6 cursor-pointer">
                {/* Background gradient on hover */}
                <div className={`absolute inset-0 rounded-xl bg-gradient-to-br ${feature.color} opacity-0 group-hover:opacity-5 transition-opacity duration-300`}/>

                <div className="relative z-10">
                  {/* Badge */}
                  <span className="text-xs font-semibold text-primary bg-primary/10 border border-primary/20 rounded-full px-2.5 py-0.5">
                    {feature.badge}
                  </span>

                  {/* Icon */}
                  <div className={`mt-4 w-12 h-12 rounded-2xl bg-gradient-to-br ${feature.color} flex items-center justify-center shadow-lg`}>
                    <feature.icon className="w-6 h-6 text-white"/>
                  </div>

                  {/* Content */}
                  <h3 className="mt-4 text-lg font-bold text-foreground">{feature.title}</h3>
                  <p className="mt-2 text-sm text-muted-foreground leading-relaxed">{feature.description}</p>

                  {/* CTA */}
                  <div className="mt-4 flex items-center gap-1 text-sm text-primary font-medium opacity-0 group-hover:opacity-100 transition-opacity">
                    Learn more <ChevronRight className="w-4 h-4"/>
                  </div>
                </div>
              </motion.div>))}
          </div>
        </div>
      </section>

      {/* ── How It Works ─────────────────────────────────────────────── */}
      <section id="how-it-works" className="py-24 bg-muted/20">
        <div className="container-app">
          <div className="text-center mb-16">
            <motion.h2 initial={{ opacity: 0, y: 20 }} whileInView={{ opacity: 1, y: 0 }} className="text-4xl font-bold text-foreground">
              How It <span className="gradient-brand-text">Works</span>
            </motion.h2>
            <p className="mt-4 text-muted-foreground">
              From document to insight in seconds.
            </p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 relative">
            {/* Connecting line */}
            <div className="hidden lg:block absolute top-8 left-[calc(12.5%+2rem)] right-[calc(12.5%+2rem)] h-px bg-gradient-to-r from-cyan-500/50 via-indigo-500/50 to-violet-500/50"/>

            {steps.map((step, i) => (<motion.div key={step.step} initial={{ opacity: 0, y: 30 }} whileInView={{ opacity: 1, y: 0 }} transition={{ duration: 0.5, delay: i * 0.12 }} className="relative text-center">
                {/* Step number circle */}
                <div className="relative mx-auto w-16 h-16 rounded-2xl gradient-brand flex items-center justify-center shadow-xl shadow-cyan-500/20 mb-5">
                  <step.icon className="w-7 h-7 text-white"/>
                  <span className="absolute -top-2 -right-2 w-6 h-6 rounded-full bg-card border-2 border-border text-xs font-bold text-foreground flex items-center justify-center">
                    {i + 1}
                  </span>
                </div>

                <h3 className="text-lg font-bold text-foreground mb-2">{step.title}</h3>
                <p className="text-sm text-muted-foreground">{step.description}</p>
              </motion.div>))}
          </div>
        </div>
      </section>

      {/* ── Trust & Security Section ──────────────────────────────────── */}
      <section id="trust" className="py-24">
        <div className="container-app">
          <div className="text-center mb-16">
            <motion.span initial={{ opacity: 0 }} whileInView={{ opacity: 1 }} className="text-xs font-semibold text-emerald-400 bg-emerald-400/10 border border-emerald-400/20 rounded-full px-4 py-1.5 inline-block mb-4">
              Healthcare-Grade Security
            </motion.span>
            <motion.h2 initial={{ opacity: 0, y: 20 }} whileInView={{ opacity: 1, y: 0 }} className="text-4xl font-bold text-foreground">
              Your health data is{" "}
              <span className="text-emerald-400">safe with us</span>
            </motion.h2>
            <p className="mt-4 text-muted-foreground max-w-lg mx-auto">
              Built with enterprise-grade security practices from day one.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {trustBadges.map((badge, i) => (<motion.div key={badge.title} initial={{ opacity: 0, y: 30 }} whileInView={{ opacity: 1, y: 0 }} transition={{ duration: 0.5, delay: i * 0.1 }} className="glass-card p-8 text-center">
                <div className={`mx-auto w-14 h-14 rounded-2xl bg-card border border-border flex items-center justify-center mb-5 ${badge.color}`}>
                  <badge.icon className="w-7 h-7"/>
                </div>
                <h3 className="text-lg font-bold text-foreground mb-3">{badge.title}</h3>
                <p className="text-sm text-muted-foreground leading-relaxed">{badge.description}</p>
              </motion.div>))}
          </div>

          {/* Disclaimer */}
          <motion.div initial={{ opacity: 0 }} whileInView={{ opacity: 1 }} className="mt-10 p-5 rounded-xl border border-amber-500/20 bg-amber-500/5 text-center">
            <p className="text-sm text-amber-400/90">
              <span className="font-semibold">⚕️ Medical Disclaimer:</span> MedAssist AI X provides healthcare information and insights for educational purposes.
              It is not a substitute for professional medical advice, diagnosis, or treatment.
              Always consult a qualified healthcare provider.
            </p>
          </motion.div>
        </div>
      </section>

      {/* ── CTA Section ──────────────────────────────────────────────── */}
      <section className="py-24 relative overflow-hidden">
        <div className="absolute inset-0 gradient-hero"/>
        <div className="absolute inset-0 dot-pattern opacity-30"/>
        <div className="container-app relative z-10 text-center">
          <motion.div initial={{ opacity: 0, scale: 0.9 }} whileInView={{ opacity: 1, scale: 1 }} transition={{ duration: 0.5 }} className="max-w-2xl mx-auto">
            <h2 className="text-4xl sm:text-5xl font-bold text-foreground mb-6">
              Ready to take control of
              <br />
              <span className="gradient-brand-text">your health?</span>
            </h2>
            <p className="text-lg text-muted-foreground mb-10">
              Join thousands of Sri Lankans who are making smarter healthcare decisions with MedAssist AI X.
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <Link href="/register">
                <motion.button whileHover={{ scale: 1.03 }} whileTap={{ scale: 0.97 }} className="btn-neon px-10 py-4 rounded-2xl font-bold text-white text-lg flex items-center gap-2.5 justify-center">
                  <Heart className="w-5 h-5"/>
                  Start for Free
                </motion.button>
              </Link>
              <Link href="/dashboard">
                <motion.button whileHover={{ scale: 1.03 }} whileTap={{ scale: 0.97 }} className="px-10 py-4 rounded-2xl font-bold text-foreground text-lg border border-border hover:bg-muted flex items-center gap-2.5 justify-center transition-all">
                  View Demo
                  <ArrowRight className="w-5 h-5"/>
                </motion.button>
              </Link>
            </div>
          </motion.div>
        </div>
      </section>

      {/* ── Footer ───────────────────────────────────────────────────── */}
      <footer className="border-t border-border py-10 bg-card/50">
        <div className="container-app">
          <div className="flex flex-col md:flex-row items-center justify-between gap-4">
            <div className="flex items-center gap-2.5">
              <div className="w-7 h-7 rounded-lg gradient-brand flex items-center justify-center">
                <Activity className="w-4 h-4 text-white"/>
              </div>
              <span className="font-bold text-foreground text-sm">MedAssist AI X</span>
            </div>
            <p className="text-xs text-muted-foreground text-center">
              © 2026 MedAssist AI X. Built for AI Challenge Sri Lanka 2026.
              <br />
              Your Personal AI Healthcare Operating System.
            </p>
            <div className="flex items-center gap-4 text-xs text-muted-foreground">
              <a href="#" className="hover:text-foreground transition-colors">Privacy</a>
              <a href="#" className="hover:text-foreground transition-colors">Terms</a>
              <a href="#" className="hover:text-foreground transition-colors">Contact</a>
            </div>
          </div>
        </div>
      </footer>
    </div>);
}
// ─────────────────────────────────────────────────────────────────────────────
// Hero Dashboard Preview (decorative)
// ─────────────────────────────────────────────────────────────────────────────
function HeroDashboardPreview() {
    return (<div className="glass rounded-2xl border border-border/60 overflow-hidden shadow-2xl shadow-cyan-500/10">
      {/* Browser chrome */}
      <div className="flex items-center gap-2 px-4 py-3 bg-card/80 border-b border-border/50">
        <div className="w-3 h-3 rounded-full bg-red-400"/>
        <div className="w-3 h-3 rounded-full bg-amber-400"/>
        <div className="w-3 h-3 rounded-full bg-emerald-400"/>
        <div className="ml-3 flex-1 h-6 bg-muted/60 rounded-lg text-xs text-muted-foreground flex items-center px-3">
          medassist.ai/dashboard
        </div>
      </div>

      {/* Dashboard preview content */}
      <div className="p-6 grid grid-cols-12 gap-4 bg-background/80">
        {/* Health Score card */}
        <div className="col-span-4 bg-card rounded-xl border border-border p-4">
          <p className="text-xs text-muted-foreground mb-2">Health Score</p>
          <div className="flex items-center gap-3">
            <div className="w-16 h-16 rounded-full border-4 border-cyan-500 flex items-center justify-center">
              <span className="text-lg font-bold text-cyan-400">82</span>
            </div>
            <div>
              <p className="font-semibold text-foreground">Excellent</p>
              <p className="text-xs text-emerald-400">↑ +3 this week</p>
            </div>
          </div>
        </div>

        {/* AI Summary */}
        <div className="col-span-8 bg-card rounded-xl border border-primary/20 p-4">
          <div className="flex items-center gap-2 mb-2">
            <div className="w-5 h-5 rounded-full gradient-brand flex items-center justify-center">
              <span className="text-white text-[8px] font-bold">AI</span>
            </div>
            <p className="text-xs font-semibold text-primary">AI Daily Summary</p>
          </div>
          <p className="text-sm text-muted-foreground">
            ✨ Your sleep improved <span className="text-foreground font-medium">15%</span> this week. Blood pressure trending down.
            Consider increasing iron intake based on your recent lab report.
          </p>
        </div>

        {/* Vitals row */}
        {[
            { label: "BP", value: "116/75", color: "text-cyan-400", unit: "mmHg" },
            { label: "Glucose", value: "88", color: "text-emerald-400", unit: "mg/dL" },
            { label: "Heart Rate", value: "67", color: "text-indigo-400", unit: "bpm" },
            { label: "Sleep", value: "8.0", color: "text-violet-400", unit: "hrs" },
        ].map((vital) => (<div key={vital.label} className="col-span-3 bg-card rounded-lg border border-border p-3">
            <p className="text-[10px] text-muted-foreground">{vital.label}</p>
            <p className={`text-lg font-bold ${vital.color}`}>{vital.value}</p>
            <p className="text-[9px] text-muted-foreground">{vital.unit}</p>
          </div>))}

        {/* Mini chart placeholder */}
        <div className="col-span-8 bg-card rounded-xl border border-border p-4">
          <p className="text-xs text-muted-foreground mb-3">Health Trend — Last 6 Weeks</p>
          <div className="flex items-end gap-2 h-14">
            {[60, 68, 72, 75, 78, 82].map((v, i) => (<div key={i} className="flex-1 rounded-t-sm bg-gradient-to-t from-cyan-500/60 to-cyan-500/20" style={{ height: `${(v / 82) * 100}%` }}/>))}
          </div>
        </div>

        {/* AI Insight card */}
        <div className="col-span-4 bg-card rounded-xl border border-amber-500/20 bg-amber-500/5 p-4">
          <p className="text-[10px] font-semibold text-amber-400 mb-1">⚡ AI Alert</p>
          <p className="text-xs text-muted-foreground">Increase iron intake — mild anemia detected in recent FBC.</p>
        </div>
      </div>
    </div>);
}

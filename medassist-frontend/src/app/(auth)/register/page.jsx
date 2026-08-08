"use client";
import { useState } from "react";
import { motion } from "framer-motion";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { Activity, Eye, EyeOff, Mail, Lock, User, Phone, ArrowRight, Droplets } from "lucide-react";
import { useAuthStore } from "@/store/auth.store";
import { authApi } from "@/lib/api";

const bloodGroups = ["A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"];

export default function RegisterPage() {
  const router = useRouter();
  const { login } = useAuthStore();
  const [step, setStep] = useState(1);
  const [showPw, setShowPw] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    phoneNumber: "",
    dateOfBirth: "",
    gender: "",
    bloodGroup: "",
  });

  const update = (key, value) => setForm((prev) => ({ ...prev, [key]: value }));

  const handleRegister = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      const payload = {
        firstName: form.firstName.trim(),
        lastName: form.lastName.trim(),
        email: form.email.trim().toLowerCase(),
        password: form.password,
        phoneNumber: form.phoneNumber || undefined,
        dateOfBirth: form.dateOfBirth || undefined,
        gender: form.gender || undefined,
        bloodGroup: form.bloodGroup || undefined,
      };
      const data = await authApi.register(payload);
      // data = { accessToken, refreshToken, user }
      login(data.user, data.accessToken, data.refreshToken);
      router.push("/dashboard");
    } catch (err) {
      const msg =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        "Registration failed. Please check your details and try again.";
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-background flex items-center justify-center p-4 relative">
      <div className="absolute inset-0 gradient-hero" />
      <div className="absolute inset-0 grid-pattern opacity-30" />
      <div className="absolute top-1/3 left-1/5 w-72 h-72 rounded-full bg-indigo-500/8 blur-3xl animate-float" />

      <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} className="relative z-10 w-full max-w-md">
        <div className="glass-card p-8">
          <Link href="/" className="flex items-center gap-3 mb-8">
            <div className="w-10 h-10 rounded-xl gradient-brand flex items-center justify-center shadow-lg">
              <Activity className="w-5.5 h-5.5 text-white" />
            </div>
            <div>
              <span className="font-bold gradient-brand-text text-lg leading-none block">MedAssist AI X</span>
              <span className="text-xs text-muted-foreground">Create your health account</span>
            </div>
          </Link>

          {/* Step indicator */}
          <div className="flex gap-2 mb-7">
            {[1, 2].map((s) => (
              <div key={s} className={`flex-1 h-1.5 rounded-full transition-all duration-500 ${s <= step ? "bg-primary" : "bg-muted"}`} />
            ))}
          </div>

          <h1 className="text-2xl font-bold text-foreground mb-1">
            {step === 1 ? "Create account" : "Complete profile"}
          </h1>
          <p className="text-sm text-muted-foreground mb-7">
            {step === 1 ? "Start your AI health journey" : "Add optional health details (can skip)"}
          </p>

          {error && (
            <motion.p initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="text-sm text-red-400 text-center bg-red-500/5 border border-red-500/20 rounded-xl px-3 py-2 mb-4">
              {error}
            </motion.p>
          )}

          <form onSubmit={step === 1 ? (e) => { e.preventDefault(); setError(""); setStep(2); } : handleRegister}>
            {step === 1 ? (
              <div className="space-y-4">
                <div className="grid grid-cols-2 gap-3">
                  {[
                    { label: "First Name", key: "firstName", icon: User, placeholder: "John", required: true },
                    { label: "Last Name", key: "lastName", icon: User, placeholder: "Doe", required: true },
                  ].map((f) => (
                    <div key={f.key}>
                      <label className="text-xs font-medium text-muted-foreground mb-1.5 block">{f.label}</label>
                      <div className="relative">
                        <f.icon className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                        <input
                          value={form[f.key]}
                          onChange={(e) => update(f.key, e.target.value)}
                          placeholder={f.placeholder}
                          required={f.required}
                          className="w-full h-11 pl-9 pr-3 bg-muted border border-border rounded-xl text-sm text-foreground placeholder:text-muted-foreground outline-none focus:border-primary/60 focus:ring-2 focus:ring-primary/20 transition-all"
                        />
                      </div>
                    </div>
                  ))}
                </div>

                <div>
                  <label className="text-xs font-medium text-muted-foreground mb-1.5 block">Email Address</label>
                  <div className="relative">
                    <Mail className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                    <input
                      id="register-email"
                      type="email"
                      required
                      value={form.email}
                      onChange={(e) => update("email", e.target.value)}
                      placeholder="you@example.com"
                      className="w-full h-11 pl-10 pr-4 bg-muted border border-border rounded-xl text-sm text-foreground placeholder:text-muted-foreground outline-none focus:border-primary/60 focus:ring-2 focus:ring-primary/20 transition-all"
                    />
                  </div>
                </div>

                <div>
                  <label className="text-xs font-medium text-muted-foreground mb-1.5 block">Password</label>
                  <div className="relative">
                    <Lock className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                    <input
                      id="register-password"
                      type={showPw ? "text" : "password"}
                      required
                      value={form.password}
                      onChange={(e) => update("password", e.target.value)}
                      placeholder="Min. 8 characters"
                      className="w-full h-11 pl-10 pr-10 bg-muted border border-border rounded-xl text-sm text-foreground placeholder:text-muted-foreground outline-none focus:border-primary/60 focus:ring-2 focus:ring-primary/20 transition-all"
                    />
                    <button type="button" onClick={() => setShowPw(!showPw)} className="absolute right-3.5 top-1/2 -translate-y-1/2 text-muted-foreground">
                      {showPw ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                    </button>
                  </div>
                </div>

                <motion.button id="register-next" type="submit" whileHover={{ scale: 1.02 }} whileTap={{ scale: 0.98 }} className="w-full btn-neon py-3 rounded-xl font-semibold text-white flex items-center justify-center gap-2">
                  Continue <ArrowRight className="w-4 h-4" />
                </motion.button>
              </div>
            ) : (
              <div className="space-y-4">
                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="text-xs font-medium text-muted-foreground mb-1.5 block">Phone Number</label>
                    <div className="relative">
                      <Phone className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                      <input value={form.phoneNumber} onChange={(e) => update("phoneNumber", e.target.value)} placeholder="+94 77 ..." className="w-full h-11 pl-9 pr-3 bg-muted border border-border rounded-xl text-sm text-foreground placeholder:text-muted-foreground outline-none focus:border-primary/60 transition-all" />
                    </div>
                  </div>
                  <div>
                    <label className="text-xs font-medium text-muted-foreground mb-1.5 block">Date of Birth</label>
                    <input type="date" value={form.dateOfBirth} onChange={(e) => update("dateOfBirth", e.target.value)} className="w-full h-11 px-3 bg-muted border border-border rounded-xl text-sm text-foreground outline-none focus:border-primary/60 transition-all" />
                  </div>
                </div>

                <div>
                  <label className="text-xs font-medium text-muted-foreground mb-1.5 block">Gender</label>
                  <div className="grid grid-cols-3 gap-2">
                    {["MALE", "FEMALE", "OTHER"].map((g) => (
                      <button key={g} type="button" onClick={() => update("gender", g)} className={`h-11 rounded-xl text-sm font-medium border transition-all ${form.gender === g ? "bg-primary/12 border-primary/40 text-primary" : "bg-muted border-border text-muted-foreground hover:text-foreground"}`}>
                        {g.charAt(0) + g.slice(1).toLowerCase()}
                      </button>
                    ))}
                  </div>
                </div>

                <div>
                  <label className="text-xs font-medium text-muted-foreground mb-1.5 flex items-center gap-1.5">
                    <Droplets className="w-3.5 h-3.5 text-red-400" /> Blood Group
                  </label>
                  <div className="grid grid-cols-4 gap-2">
                    {bloodGroups.map((bg) => (
                      <button key={bg} type="button" onClick={() => update("bloodGroup", bg)} className={`h-10 rounded-xl text-sm font-medium border transition-all ${form.bloodGroup === bg ? "bg-red-500/12 border-red-400/40 text-red-400" : "bg-muted border-border text-muted-foreground hover:text-foreground"}`}>
                        {bg}
                      </button>
                    ))}
                  </div>
                </div>

                <div className="flex gap-3">
                  <button type="button" onClick={() => setStep(1)} className="flex-1 h-12 border border-border rounded-xl text-sm font-semibold text-foreground hover:bg-muted transition-colors">
                    Back
                  </button>
                  <motion.button
                    id="register-submit"
                    type="submit"
                    disabled={loading}
                    whileHover={{ scale: loading ? 1 : 1.02 }}
                    whileTap={{ scale: loading ? 1 : 0.98 }}
                    className="flex-1 btn-neon py-3 rounded-xl font-semibold text-white flex items-center justify-center gap-2 disabled:opacity-60"
                  >
                    {loading ? (
                      <svg className="animate-spin w-4 h-4" viewBox="0 0 24 24" fill="none">
                        <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeDasharray="60" strokeDashoffset="20" />
                      </svg>
                    ) : (<>Create Account <ArrowRight className="w-4 h-4" /></>)}
                  </motion.button>
                </div>
              </div>
            )}
          </form>

          <p className="text-center text-sm text-muted-foreground mt-6">
            Already have an account?{" "}
            <Link href="/login" className="text-primary hover:underline font-medium">Sign in</Link>
          </p>
        </div>
      </motion.div>
    </div>
  );
}

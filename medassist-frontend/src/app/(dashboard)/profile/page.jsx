"use client";
import { useState, useEffect } from "react";
import { motion } from "framer-motion";
import { useMutation } from "@tanstack/react-query";
import { User, Mail, Phone, Calendar, Droplets, Camera, Save, Shield, Activity, Loader2 } from "lucide-react";
import { GlowCard, PageTransition, SectionHeader } from "@/components/common";
import { useAuthStore } from "@/store/auth.store";
import { usersApi } from "@/lib/api";

const bloodGroups = ["A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"];

export default function ProfilePage() {
  const { user, setUser } = useAuthStore();
  const [isEditing, setIsEditing] = useState(false);
  const [successMsg, setSuccessMsg] = useState("");
  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phoneNumber: "",
    dateOfBirth: "",
    bloodGroup: "",
    gender: "",
  });

  // Load user data into form
  useEffect(() => {
    if (user) {
      setForm({
        firstName: user.firstName || "",
        lastName: user.lastName || "",
        email: user.email || "",
        phoneNumber: user.phoneNumber || "",
        dateOfBirth: user.dateOfBirth || "",
        bloodGroup: user.bloodGroup || "",
        gender: user.gender || "",
      });
    }
  }, [user, isEditing]);

  const update = (k, v) => setForm((p) => ({ ...p, [k]: v }));

  const updateMutation = useMutation({
    mutationFn: usersApi.updateMe,
    onSuccess: (updatedUser) => {
      setUser(updatedUser); // Update global store
      setIsEditing(false);
      setSuccessMsg("Profile updated successfully");
      setTimeout(() => setSuccessMsg(""), 3000);
    },
    onError: (err) => {
      console.error("Update failed", err);
    }
  });

  const handleSave = () => {
    if (!form.firstName.trim() || !form.lastName.trim()) return;
    
    // Only send fields that can be updated
    const payload = {
      firstName: form.firstName.trim(),
      lastName: form.lastName.trim(),
      phoneNumber: form.phoneNumber,
      dateOfBirth: form.dateOfBirth,
      bloodGroup: form.bloodGroup,
      gender: form.gender,
    };
    
    updateMutation.mutate(payload);
  };

  if (!user) return null;

  return (
    <PageTransition>
      <div className="max-w-4xl mx-auto space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-foreground">My Profile</h1>
            <p className="text-muted-foreground text-sm mt-1">Manage your personal and medical details.</p>
          </div>
          {!isEditing ? (
            <motion.button whileHover={{ scale: 1.03 }} whileTap={{ scale: 0.97 }} onClick={() => setIsEditing(true)} className="btn-neon px-5 py-2.5 rounded-xl font-semibold text-white text-sm">
              Edit Profile
            </motion.button>
          ) : (
            <div className="flex items-center gap-3">
              <button onClick={() => setIsEditing(false)} className="px-5 py-2.5 rounded-xl font-medium text-foreground hover:bg-muted transition-colors text-sm">
                Cancel
              </button>
              <motion.button whileHover={{ scale: 1.03 }} whileTap={{ scale: 0.97 }} onClick={handleSave} disabled={updateMutation.isPending} className="btn-neon px-5 py-2.5 rounded-xl font-semibold text-white text-sm flex items-center gap-2">
                {updateMutation.isPending ? <Loader2 className="w-4 h-4 animate-spin" /> : <Save className="w-4 h-4" />} Save
              </motion.button>
            </div>
          )}
        </div>

        {successMsg && (
          <motion.div initial={{ opacity: 0, y: -10 }} animate={{ opacity: 1, y: 0 }} className="p-3 bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 text-sm font-medium rounded-xl text-center">
            {successMsg}
          </motion.div>
        )}
        {updateMutation.isError && (
          <motion.div initial={{ opacity: 0, y: -10 }} animate={{ opacity: 1, y: 0 }} className="p-3 bg-red-500/10 border border-red-500/20 text-red-400 text-sm font-medium rounded-xl text-center">
            {updateMutation.error?.response?.data?.message || "Failed to update profile"}
          </motion.div>
        )}

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Left Column: Avatar & Quick Stats */}
          <div className="space-y-6">
            <GlowCard delay={0} glowColor="indigo" className="flex flex-col items-center text-center">
              <div className="relative mb-4 group cursor-pointer">
                <div className="w-24 h-24 rounded-full gradient-brand flex items-center justify-center text-white text-3xl font-bold shadow-xl overflow-hidden">
                  {user.profileImageUrl ? (
                    <img src={user.profileImageUrl} alt="Profile" className="w-full h-full object-cover" />
                  ) : (
                    <>{form.firstName[0]}{form.lastName[0]}</>
                  )}
                  
                  {isEditing && (
                    <div className="absolute inset-0 bg-black/50 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                      <Camera className="w-6 h-6 text-white" />
                    </div>
                  )}
                </div>
                <div className="absolute -bottom-1 -right-1 w-6 h-6 rounded-full bg-emerald-500 border-2 border-card flex items-center justify-center">
                  <Shield className="w-3 h-3 text-white" />
                </div>
              </div>
              <h2 className="text-xl font-bold text-foreground">{user.firstName} {user.lastName}</h2>
              <p className="text-sm text-muted-foreground mt-1 flex items-center gap-1.5 justify-center">
                <Mail className="w-3.5 h-3.5" /> {user.email}
              </p>
              <div className="w-full mt-6 pt-6 border-t border-border flex justify-between">
                <div className="text-center">
                  <p className="text-xs text-muted-foreground uppercase tracking-wider">Role</p>
                  <p className="text-sm font-bold text-foreground mt-1 bg-primary/10 text-primary px-2 py-0.5 rounded-full inline-block">
                    {user.roles?.[0]?.replace("ROLE_", "") || "USER"}
                  </p>
                </div>
                <div className="text-center">
                  <p className="text-xs text-muted-foreground uppercase tracking-wider">Status</p>
                  <p className="text-sm font-bold text-emerald-400 mt-1">
                    {user.active ? "Active" : "Inactive"}
                  </p>
                </div>
              </div>
            </GlowCard>

            <GlowCard delay={0.1} glowColor="cyan" noPadding>
              <div className="p-5">
                <SectionHeader title="Account Security" />
                <div className="space-y-4">
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm font-medium text-foreground">Password</p>
                      <p className="text-xs text-muted-foreground mt-0.5">Last changed 3 months ago</p>
                    </div>
                    <button className="text-xs font-semibold text-primary hover:underline">Update</button>
                  </div>
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm font-medium text-foreground">2-Factor Auth</p>
                      <p className="text-xs text-muted-foreground mt-0.5">Add extra security layer</p>
                    </div>
                    <div className="w-9 h-5 rounded-full bg-muted border border-border relative cursor-not-allowed">
                      <div className="w-3.5 h-3.5 rounded-full bg-muted-foreground absolute left-0.5 top-0.5" />
                    </div>
                  </div>
                </div>
              </div>
            </GlowCard>
          </div>

          {/* Right Column: Form Fields */}
          <GlowCard delay={0.2} glowColor="indigo" className="lg:col-span-2" noPadding>
            <div className="p-6">
              <SectionHeader title="Personal Information" />
              
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-5 mb-8">
                <div>
                  <label className="text-xs font-medium text-muted-foreground mb-1.5 flex items-center gap-1.5">
                    <User className="w-3.5 h-3.5" /> First Name
                  </label>
                  <input value={form.firstName} onChange={(e) => update("firstName", e.target.value)} disabled={!isEditing} className="w-full h-11 px-3 bg-muted border border-border rounded-xl text-sm text-foreground disabled:opacity-70 disabled:cursor-not-allowed outline-none focus:border-primary/50 transition-colors" />
                </div>
                <div>
                  <label className="text-xs font-medium text-muted-foreground mb-1.5 flex items-center gap-1.5">
                    <User className="w-3.5 h-3.5" /> Last Name
                  </label>
                  <input value={form.lastName} onChange={(e) => update("lastName", e.target.value)} disabled={!isEditing} className="w-full h-11 px-3 bg-muted border border-border rounded-xl text-sm text-foreground disabled:opacity-70 disabled:cursor-not-allowed outline-none focus:border-primary/50 transition-colors" />
                </div>
                <div className="sm:col-span-2">
                  <label className="text-xs font-medium text-muted-foreground mb-1.5 flex items-center gap-1.5">
                    <Mail className="w-3.5 h-3.5" /> Email Address
                  </label>
                  <input value={form.email} disabled className="w-full h-11 px-3 bg-muted border border-border rounded-xl text-sm text-foreground opacity-70 cursor-not-allowed outline-none" />
                  <p className="text-[10px] text-muted-foreground mt-1">Email cannot be changed directly.</p>
                </div>
                <div>
                  <label className="text-xs font-medium text-muted-foreground mb-1.5 flex items-center gap-1.5">
                    <Phone className="w-3.5 h-3.5" /> Phone Number
                  </label>
                  <input value={form.phoneNumber} onChange={(e) => update("phoneNumber", e.target.value)} disabled={!isEditing} placeholder="+1 234 567 890" className="w-full h-11 px-3 bg-muted border border-border rounded-xl text-sm text-foreground disabled:opacity-70 disabled:cursor-not-allowed outline-none focus:border-primary/50 transition-colors" />
                </div>
                <div>
                  <label className="text-xs font-medium text-muted-foreground mb-1.5 flex items-center gap-1.5">
                    <Calendar className="w-3.5 h-3.5" /> Date of Birth
                  </label>
                  <input type="date" value={form.dateOfBirth} onChange={(e) => update("dateOfBirth", e.target.value)} disabled={!isEditing} className="w-full h-11 px-3 bg-muted border border-border rounded-xl text-sm text-foreground disabled:opacity-70 disabled:cursor-not-allowed outline-none focus:border-primary/50 transition-colors" />
                </div>
              </div>

              <SectionHeader title="Biological Information" />
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
                <div>
                  <label className="text-xs font-medium text-muted-foreground mb-1.5 flex items-center gap-1.5">
                    <Activity className="w-3.5 h-3.5" /> Gender
                  </label>
                  <select value={form.gender} onChange={(e) => update("gender", e.target.value)} disabled={!isEditing} className="w-full h-11 px-3 bg-muted border border-border rounded-xl text-sm text-foreground disabled:opacity-70 disabled:cursor-not-allowed outline-none focus:border-primary/50 transition-colors">
                    <option value="">Select...</option>
                    <option value="MALE">Male</option>
                    <option value="FEMALE">Female</option>
                    <option value="OTHER">Other</option>
                  </select>
                </div>
                <div>
                  <label className="text-xs font-medium text-muted-foreground mb-1.5 flex items-center gap-1.5">
                    <Droplets className="w-3.5 h-3.5 text-red-400" /> Blood Group
                  </label>
                  <select value={form.bloodGroup} onChange={(e) => update("bloodGroup", e.target.value)} disabled={!isEditing} className="w-full h-11 px-3 bg-muted border border-border rounded-xl text-sm text-foreground disabled:opacity-70 disabled:cursor-not-allowed outline-none focus:border-primary/50 transition-colors">
                    <option value="">Select...</option>
                    {bloodGroups.map(bg => <option key={bg} value={bg}>{bg}</option>)}
                  </select>
                </div>
              </div>
            </div>
          </GlowCard>
        </div>
      </div>
    </PageTransition>
  );
}

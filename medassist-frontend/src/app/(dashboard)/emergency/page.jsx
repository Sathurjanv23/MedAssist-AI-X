"use client";
import { useRef } from "react";
import { motion } from "framer-motion";
import { useQuery } from "@tanstack/react-query";
import { AlertCircle, Phone, FileText, Droplets, Heart, Activity, ArrowRight, Printer, ShieldAlert, Loader2 } from "lucide-react";
import { PageTransition, SectionHeader } from "@/components/common";
import { profileApi } from "@/lib/api";
import { useAuthStore } from "@/store/auth.store";

export default function EmergencyPage() {
  const { user } = useAuthStore();
  const printRef = useRef(null);

  const { data: profile, isLoading } = useQuery({
    queryKey: ["medical-profile"],
    queryFn: profileApi.getMedicalProfile,
    retry: 1,
  });

  const handlePrint = () => {
    window.print();
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-[60vh]">
        <Loader2 className="w-8 h-8 animate-spin text-red-500" />
      </div>
    );
  }

  const hasData = profile && Object.keys(profile).length > 0;
  const emergencyContacts = profile?.emergencyContacts || [];
  const allergies = profile?.allergies || [];
  const activeConditions = profile?.activeConditions || [];
  const currentMedications = profile?.currentMedications || [];

  return (
    <PageTransition>
      <div className="max-w-4xl mx-auto space-y-6">
        <div className="flex items-center justify-between no-print">
          <div>
            <h1 className="text-2xl font-bold text-red-500 flex items-center gap-2">
              <ShieldAlert className="w-6 h-6" /> Emergency Profile
            </h1>
            <p className="text-muted-foreground text-sm mt-1">
              Critical medical information for first responders and healthcare providers.
            </p>
          </div>
          <motion.button whileHover={{ scale: 1.03 }} whileTap={{ scale: 0.97 }} onClick={handlePrint} className="bg-card border border-border px-4 py-2 rounded-xl font-semibold text-foreground text-sm flex items-center gap-2 hover:bg-muted transition-colors">
            <Printer className="w-4 h-4" /> Print Card
          </motion.button>
        </div>

        {/* Printable Area */}
        <div ref={printRef} className="bg-card border-2 border-red-500/20 rounded-2xl overflow-hidden shadow-xl shadow-red-500/5 print:border-none print:shadow-none print:bg-white">
          {/* Header */}
          <div className="bg-red-500/10 border-b-2 border-red-500/20 p-6 flex items-center justify-between print:bg-red-50 print:border-red-200">
            <div className="flex items-center gap-4">
              <div className="w-16 h-16 rounded-full bg-red-500 flex items-center justify-center text-white text-2xl font-bold shadow-lg shadow-red-500/30">
                {user?.firstName?.[0]}{user?.lastName?.[0]}
              </div>
              <div>
                <p className="text-xs font-bold text-red-500 uppercase tracking-widest mb-1">Medical ID</p>
                <h2 className="text-2xl font-bold text-foreground print:text-black">
                  {user?.firstName} {user?.lastName}
                </h2>
                <div className="flex items-center gap-4 mt-1">
                  {user?.dateOfBirth && <p className="text-sm text-muted-foreground print:text-gray-600">DOB: {user.dateOfBirth}</p>}
                  {user?.phoneNumber && <p className="text-sm text-muted-foreground print:text-gray-600">{user.phoneNumber}</p>}
                </div>
              </div>
            </div>
            <div className="text-right">
              <div className="inline-flex flex-col items-center justify-center w-16 h-16 rounded-xl bg-red-500/10 border border-red-500/20 print:bg-red-50 print:border-red-200">
                <Droplets className="w-6 h-6 text-red-500 mb-1" />
                <span className="text-sm font-bold text-red-500">{profile?.bloodGroup || "N/A"}</span>
              </div>
            </div>
          </div>

          <div className="p-6 grid grid-cols-1 md:grid-cols-2 gap-8 print:grid-cols-2">
            {/* Left Column */}
            <div className="space-y-8">
              {/* Emergency Contacts */}
              <div>
                <h3 className="text-sm font-bold text-foreground uppercase tracking-wide flex items-center gap-2 mb-4 print:text-black">
                  <Phone className="w-4 h-4 text-red-500" /> Emergency Contacts
                </h3>
                {emergencyContacts.length > 0 ? (
                  <div className="space-y-3">
                    {emergencyContacts.map((contact, i) => (
                      <div key={i} className="p-3 rounded-xl bg-muted/50 border border-border print:border-gray-200 print:bg-gray-50">
                        <p className="font-semibold text-foreground print:text-black">{contact.name}</p>
                        <p className="text-sm text-muted-foreground print:text-gray-600">{contact.relationship}</p>
                        <a href={`tel:${contact.phone}`} className="text-sm font-medium text-primary mt-1 inline-block print:text-blue-600">
                          {contact.phone}
                        </a>
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="text-sm text-muted-foreground">No emergency contacts listed.</p>
                )}
              </div>

              {/* Allergies */}
              <div>
                <h3 className="text-sm font-bold text-foreground uppercase tracking-wide flex items-center gap-2 mb-4 print:text-black">
                  <AlertCircle className="w-4 h-4 text-red-500" /> Allergies
                </h3>
                {allergies.length > 0 ? (
                  <div className="flex flex-wrap gap-2">
                    {allergies.map((allergy, i) => (
                      <span key={i} className="px-3 py-1.5 rounded-lg bg-red-500/10 border border-red-500/20 text-red-500 font-medium text-sm print:border-red-300 print:bg-red-50">
                        {allergy}
                      </span>
                    ))}
                  </div>
                ) : (
                  <p className="text-sm text-muted-foreground">No known allergies.</p>
                )}
              </div>
            </div>

            {/* Right Column */}
            <div className="space-y-8">
              {/* Medical Conditions */}
              <div>
                <h3 className="text-sm font-bold text-foreground uppercase tracking-wide flex items-center gap-2 mb-4 print:text-black">
                  <Heart className="w-4 h-4 text-red-500" /> Medical Conditions
                </h3>
                {activeConditions.length > 0 ? (
                  <ul className="space-y-2">
                    {activeConditions.map((condition, i) => (
                      <li key={i} className="flex items-center gap-2 text-sm text-foreground print:text-black">
                        <div className="w-1.5 h-1.5 rounded-full bg-red-500" />
                        {condition}
                      </li>
                    ))}
                  </ul>
                ) : (
                  <p className="text-sm text-muted-foreground">No chronic conditions.</p>
                )}
              </div>

              {/* Current Medications */}
              <div>
                <h3 className="text-sm font-bold text-foreground uppercase tracking-wide flex items-center gap-2 mb-4 print:text-black">
                  <Activity className="w-4 h-4 text-red-500" /> Current Medications
                </h3>
                {currentMedications.length > 0 ? (
                  <ul className="space-y-2">
                    {currentMedications.map((med, i) => (
                      <li key={i} className="flex items-center gap-2 text-sm text-foreground print:text-black">
                        <div className="w-1.5 h-1.5 rounded-full bg-indigo-400" />
                        {med}
                      </li>
                    ))}
                  </ul>
                ) : (
                  <p className="text-sm text-muted-foreground">No current medications.</p>
                )}
              </div>

              {/* Notes */}
              {profile?.notes && (
                <div>
                  <h3 className="text-sm font-bold text-foreground uppercase tracking-wide flex items-center gap-2 mb-2 print:text-black">
                    <FileText className="w-4 h-4 text-red-500" /> Special Instructions
                  </h3>
                  <p className="text-sm text-foreground bg-muted/30 p-3 rounded-lg border border-border print:text-black print:border-gray-200">
                    {profile.notes}
                  </p>
                </div>
              )}
            </div>
          </div>
          
          <div className="bg-muted p-4 text-center border-t border-border print:bg-white print:border-gray-300">
            <p className="text-xs text-muted-foreground print:text-gray-500">
              Generated by MedAssist AI X. This information is provided by the user and their medical records.
            </p>
          </div>
        </div>

        {/* Action Call */}
        {!hasData && (
          <div className="flex items-center justify-between p-4 rounded-xl border border-primary/20 bg-primary/5 no-print">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center">
                <ShieldAlert className="w-5 h-5 text-primary" />
              </div>
              <div>
                <p className="font-semibold text-foreground text-sm">Update Emergency Profile</p>
                <p className="text-xs text-muted-foreground">Keep your medical data up to date for emergencies.</p>
              </div>
            </div>
            <a href="/profile" className="btn-neon px-4 py-2 rounded-xl font-semibold text-white text-xs flex items-center gap-1">
              Edit Profile <ArrowRight className="w-3.5 h-3.5" />
            </a>
          </div>
        )}
      </div>
    </PageTransition>
  );
}

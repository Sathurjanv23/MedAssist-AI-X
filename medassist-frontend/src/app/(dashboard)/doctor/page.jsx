"use client";
import { PageTransition } from "@/components/common";
import { Activity } from "lucide-react";

export default function DoctorPage() {
  return (
    <PageTransition>
      <div className="flex flex-col items-center justify-center min-h-[60vh] text-center max-w-md mx-auto">
        <div className="w-16 h-16 bg-teal-500/10 rounded-full flex items-center justify-center mb-6">
          <Activity className="w-8 h-8 text-teal-400" />
        </div>
        <h1 className="text-2xl font-bold text-foreground">Doctor Portal</h1>
        <p className="text-muted-foreground mt-2">
          This portal requires healthcare provider privileges. Your patient queue and clinical tools will appear here once authorized.
        </p>
      </div>
    </PageTransition>
  );
}

"use client";
import { PageTransition } from "@/components/common";
import { ShieldAlert } from "lucide-react";

export default function AdminPage() {
  return (
    <PageTransition>
      <div className="flex flex-col items-center justify-center min-h-[60vh] text-center max-w-md mx-auto">
        <div className="w-16 h-16 bg-orange-500/10 rounded-full flex items-center justify-center mb-6">
          <ShieldAlert className="w-8 h-8 text-orange-400" />
        </div>
        <h1 className="text-2xl font-bold text-foreground">Admin Portal</h1>
        <p className="text-muted-foreground mt-2">
          This portal requires administrator privileges. Real-time platform analytics and user management will appear here for authorized users.
        </p>
      </div>
    </PageTransition>
  );
}

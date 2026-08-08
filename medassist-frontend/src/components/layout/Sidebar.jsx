"use client";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { motion, AnimatePresence } from "framer-motion";
import { LayoutDashboard, Brain, FileText, MessageSquare, Pill, Clock, User, AlertCircle, Stethoscope, Shield, ChevronLeft, Activity, } from "lucide-react";
import { cn } from "@/lib/utils";
import { useUIStore, useAuthStore } from "@/store/auth.store";
import { AIOrb } from "@/components/common";
// ─────────────────────────────────────────────────────────────────────────────
// Navigation items
// ─────────────────────────────────────────────────────────────────────────────
const navItems = [
    { label: "Dashboard", href: "/dashboard", icon: LayoutDashboard, color: "text-cyan-400" },
    { label: "AI Health Twin", href: "/health-twin", icon: Brain, color: "text-indigo-400" },
    { label: "Medical Reports", href: "/reports", icon: FileText, color: "text-violet-400" },
    { label: "AI Copilot", href: "/ai-chat", icon: MessageSquare, color: "text-cyan-400" },
    { label: "Medicines", href: "/medicines", icon: Pill, color: "text-emerald-400" },
    { label: "Timeline", href: "/timeline", icon: Clock, color: "text-amber-400" },
];
const secondaryItems = [
    { label: "Emergency", href: "/emergency", icon: AlertCircle, color: "text-red-400" },
    { label: "Profile", href: "/profile", icon: User, color: "text-slate-400" },
];
const adminItems = [
    { label: "Doctor Portal", href: "/doctor", icon: Stethoscope, color: "text-teal-400" },
    { label: "Admin", href: "/admin", icon: Shield, color: "text-orange-400" },
];
// ─────────────────────────────────────────────────────────────────────────────
// Sidebar Component
// ─────────────────────────────────────────────────────────────────────────────
export function Sidebar() {
    const pathname = usePathname();
    const { sidebarOpen, toggleSidebar } = useUIStore();
    const { user } = useAuthStore();
    return (<motion.aside animate={{ width: sidebarOpen ? 256 : 72 }} transition={{ duration: 0.3, ease: "easeInOut" }} className="relative flex flex-col h-full bg-card border-r border-border overflow-hidden shrink-0">
      {/* ── Logo ──────────────────────────────────────────────────────── */}
      <div className="flex items-center gap-3 p-4 border-b border-border h-16 shrink-0">
        <div className="relative shrink-0">
          <div className="w-9 h-9 rounded-xl gradient-brand flex items-center justify-center shadow-lg">
            <Activity className="w-5 h-5 text-white"/>
          </div>
          <div className="absolute -top-0.5 -right-0.5 w-3 h-3 rounded-full bg-emerald-400 border-2 border-card animate-pulse"/>
        </div>
        <AnimatePresence>
          {sidebarOpen && (<motion.div initial={{ opacity: 0, x: -10 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: -10 }} transition={{ duration: 0.2 }} className="overflow-hidden">
              <span className="font-bold text-sm gradient-brand-text leading-tight block">
                MedAssist
              </span>
              <span className="text-xs text-muted-foreground leading-tight block">
                AI X
              </span>
            </motion.div>)}
        </AnimatePresence>
        <motion.button onClick={toggleSidebar} className="ml-auto shrink-0 w-7 h-7 rounded-lg bg-muted hover:bg-accent flex items-center justify-center text-muted-foreground hover:text-foreground transition-colors" animate={{ rotate: sidebarOpen ? 0 : 180 }} transition={{ duration: 0.3 }}>
          <ChevronLeft className="w-4 h-4"/>
        </motion.button>
      </div>

      {/* ── AI Status Banner ──────────────────────────────────────────── */}
      <AnimatePresence>
        {sidebarOpen && (<motion.div initial={{ opacity: 0, height: 0 }} animate={{ opacity: 1, height: "auto" }} exit={{ opacity: 0, height: 0 }} className="mx-3 mt-3 p-3 rounded-xl bg-primary/8 border border-primary/15">
            <div className="flex items-center gap-2">
              <AIOrb size={28}/>
              <div>
                <p className="text-xs font-semibold text-primary">AI Active</p>
                <p className="text-[10px] text-muted-foreground">Health monitoring on</p>
              </div>
            </div>
          </motion.div>)}
      </AnimatePresence>

      {/* ── Navigation ────────────────────────────────────────────────── */}
      <nav className="flex-1 overflow-y-auto py-3 px-2 space-y-0.5">
        {/* Primary nav */}
        <div className="space-y-0.5">
          {sidebarOpen && (<p className="text-[10px] font-semibold text-muted-foreground uppercase tracking-widest px-3 py-2">
              Main
            </p>)}
          {navItems.map((item) => (<NavItem key={item.href} {...item} active={pathname === item.href || pathname.startsWith(item.href + "/")} collapsed={!sidebarOpen}/>))}
        </div>

        <div className="my-2 mx-2 border-t border-border"/>

        {/* Secondary nav */}
        <div className="space-y-0.5">
          {sidebarOpen && (<p className="text-[10px] font-semibold text-muted-foreground uppercase tracking-widest px-3 py-2">
              Settings
            </p>)}
          {secondaryItems.map((item) => (<NavItem key={item.href} {...item} active={pathname === item.href} collapsed={!sidebarOpen}/>))}
        </div>

        {/* Admin items — show for ADMIN/DOCTOR */}
        {(user?.role === "ADMIN" || user?.role === "DOCTOR") && (<>
            <div className="my-2 mx-2 border-t border-border"/>
            <div className="space-y-0.5">
              {sidebarOpen && (<p className="text-[10px] font-semibold text-muted-foreground uppercase tracking-widest px-3 py-2">
                  Portal
                </p>)}
              {adminItems.map((item) => (<NavItem key={item.href} {...item} active={pathname === item.href} collapsed={!sidebarOpen}/>))}
            </div>
          </>)}
      </nav>

      {/* ── User Footer ───────────────────────────────────────────────── */}
      <div className="p-3 border-t border-border shrink-0">
        <Link href="/profile" className="flex items-center gap-3 rounded-lg p-2 hover:bg-muted transition-colors">
          <div className="w-8 h-8 rounded-full gradient-brand flex items-center justify-center shrink-0">
            <span className="text-white text-xs font-bold">
              {user?.firstName?.[0] ?? "A"}
            </span>
          </div>
          <AnimatePresence>
            {sidebarOpen && (<motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} className="overflow-hidden flex-1 min-w-0">
                <p className="text-sm font-medium text-foreground truncate">
                  {user?.firstName} {user?.lastName}
                </p>
                <p className="text-xs text-muted-foreground truncate">{user?.email}</p>
              </motion.div>)}
          </AnimatePresence>
        </Link>
      </div>
    </motion.aside>);
}
function NavItem({ label, href, icon: Icon, color, active, collapsed }) {
    return (<Link href={href} title={collapsed ? label : undefined}>
      <motion.div className={cn("sidebar-item", active && "active", collapsed && "justify-center px-0")} whileHover={{ x: 2 }} whileTap={{ scale: 0.97 }}>
        <Icon className={cn("w-4.5 h-4.5 shrink-0", active ? "text-primary" : color)}/>
        <AnimatePresence>
          {!collapsed && (<motion.span initial={{ opacity: 0, width: 0 }} animate={{ opacity: 1, width: "auto" }} exit={{ opacity: 0, width: 0 }} className="overflow-hidden whitespace-nowrap">
              {label}
            </motion.span>)}
        </AnimatePresence>
        {active && !collapsed && (<motion.div layoutId="active-indicator" className="ml-auto w-1.5 h-1.5 rounded-full bg-primary"/>)}
      </motion.div>
    </Link>);
}

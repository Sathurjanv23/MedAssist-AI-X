"use client";
import { useState, useRef, useEffect } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { Bell, Search, Activity, User, LogOut, Settings, HelpCircle, Loader2 } from "lucide-react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuthStore, useUIStore } from "@/store/auth.store";
import { authApi } from "@/lib/api";
import { timeAgo } from "@/lib/utils";
import { cn } from "@/lib/utils";

export function TopNav() {
  const router = useRouter();
  const { user, logout } = useAuthStore();
  const { notificationPanelOpen, setNotificationPanel } = useUIStore();
  
  const [isScrolled, setIsScrolled] = useState(false);
  const [profileOpen, setProfileOpen] = useState(false);
  const [searchFocused, setSearchFocused] = useState(false);
  
  const notifRef = useRef(null);
  const profileRef = useRef(null);

  // Hardcode an empty array since we don't have a real notifications API yet
  const notifications = [];
  const unreadCount = 0;

  useEffect(() => {
    const handleScroll = () => setIsScrolled(window.scrollY > 20);
    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (notifRef.current && !notifRef.current.contains(e.target)) setNotificationPanel(false);
      if (profileRef.current && !profileRef.current.contains(e.target)) setProfileOpen(false);
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [setNotificationPanel]);

  const handleLogout = async () => {
    try {
      await authApi.logout();
    } catch (e) {
      // ignore
    } finally {
      logout();
      router.push("/login");
    }
  };

  return (
    <nav className={cn(
      "sticky top-0 z-40 w-full transition-all duration-300",
      isScrolled ? "bg-background/80 backdrop-blur-xl border-b border-border shadow-sm" : "bg-transparent"
    )}>
      <div className="h-16 px-4 md:px-6 flex items-center justify-between gap-4">
        
        {/* Left: Search (visible on desktop) */}
        <div className="hidden md:block flex-1 max-w-md">
          <div className={cn(
            "relative transition-all duration-300",
            searchFocused ? "ring-2 ring-primary/20 rounded-xl" : ""
          )}>
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
            <input 
              type="text" 
              placeholder="Search reports, medicines, or health metrics..."
              onFocus={() => setSearchFocused(true)}
              onBlur={() => setSearchFocused(false)}
              className="w-full h-10 pl-9 pr-4 bg-muted/50 border border-transparent rounded-xl text-sm text-foreground placeholder:text-muted-foreground outline-none focus:border-primary/50 transition-colors hover:bg-muted"
            />
          </div>
        </div>

        {/* Mobile Logo */}
        <div className="md:hidden flex items-center gap-2">
          <div className="w-8 h-8 rounded-lg gradient-brand flex items-center justify-center">
            <Activity className="w-4 h-4 text-white" />
          </div>
          <span className="font-bold gradient-brand-text">MedAssist</span>
        </div>

        {/* Right Actions */}
        <div className="flex items-center gap-3 md:gap-4 shrink-0">
          
          {/* Notifications */}
          <div className="relative" ref={notifRef}>
            <motion.button 
              whileHover={{ scale: 1.05 }} 
              whileTap={{ scale: 0.95 }}
              onClick={() => setNotificationPanel(!notificationPanelOpen)}
              className="relative w-10 h-10 rounded-xl bg-muted/50 hover:bg-muted flex items-center justify-center border border-transparent hover:border-border transition-colors text-muted-foreground hover:text-foreground"
            >
              <Bell className="w-5 h-5" />
              {unreadCount > 0 && (
                <span className="absolute top-2 right-2.5 w-2 h-2 rounded-full bg-red-500 shadow-[0_0_8px_rgba(239,68,68,0.8)]" />
              )}
            </motion.button>

            {/* Notification Dropdown */}
            <AnimatePresence>
              {notificationPanelOpen && (
                <motion.div 
                  initial={{ opacity: 0, y: 10, scale: 0.95 }}
                  animate={{ opacity: 1, y: 0, scale: 1 }}
                  exit={{ opacity: 0, y: 10, scale: 0.95 }}
                  transition={{ duration: 0.2 }}
                  className="absolute right-0 mt-2 w-80 md:w-96 bg-card border border-border rounded-2xl shadow-xl overflow-hidden z-50 origin-top-right"
                >
                  <div className="p-4 border-b border-border flex items-center justify-between bg-muted/30">
                    <h3 className="font-bold text-foreground">Notifications</h3>
                    <button className="text-xs font-medium text-primary hover:underline">Mark all as read</button>
                  </div>
                  
                  <div className="max-h-[60vh] overflow-y-auto">
                    {notifications.length > 0 ? (
                      notifications.map(notif => (
                        <Link 
                          key={notif.id} 
                          href={notif.actionUrl || "#"}
                          onClick={() => setNotificationPanel(false)}
                          className={cn(
                            "flex gap-3 p-4 border-b border-border hover:bg-muted/50 transition-colors",
                            !notif.read && "bg-primary/4"
                          )}
                        >
                          <div className={cn("w-2 h-2 mt-1.5 rounded-full shrink-0", notif.read ? "bg-transparent" : "bg-primary")} />
                          <div>
                            <p className="text-sm font-medium text-foreground">{notif.title}</p>
                            <p className="text-xs text-muted-foreground mt-0.5">{notif.message}</p>
                            <p className="text-[10px] text-muted-foreground mt-1.5">{timeAgo(notif.timestamp)}</p>
                          </div>
                        </Link>
                      ))
                    ) : (
                      <div className="p-8 text-center text-muted-foreground">
                        <Bell className="w-8 h-8 mx-auto mb-3 opacity-20" />
                        <p className="text-sm">No new notifications</p>
                      </div>
                    )}
                  </div>
                </motion.div>
              )}
            </AnimatePresence>
          </div>

          {/* User Profile */}
          <div className="relative" ref={profileRef}>
            <motion.button 
              whileHover={{ scale: 1.05 }} 
              whileTap={{ scale: 0.95 }}
              onClick={() => setProfileOpen(!profileOpen)}
              className="w-10 h-10 rounded-xl gradient-brand p-[1px] flex items-center justify-center shadow-sm"
            >
              <div className="w-full h-full rounded-[11px] bg-card flex items-center justify-center overflow-hidden">
                {user?.profileImageUrl ? (
                  <img src={user.profileImageUrl} alt="Profile" className="w-full h-full object-cover" />
                ) : (
                  <span className="font-bold text-sm gradient-brand-text">
                    {user?.firstName?.[0] || "U"}{user?.lastName?.[0] || ""}
                  </span>
                )}
              </div>
            </motion.button>

            <AnimatePresence>
              {profileOpen && (
                <motion.div 
                  initial={{ opacity: 0, y: 10, scale: 0.95 }}
                  animate={{ opacity: 1, y: 0, scale: 1 }}
                  exit={{ opacity: 0, y: 10, scale: 0.95 }}
                  transition={{ duration: 0.2 }}
                  className="absolute right-0 mt-2 w-64 bg-card border border-border rounded-2xl shadow-xl overflow-hidden z-50 origin-top-right"
                >
                  <div className="p-4 border-b border-border bg-muted/30">
                    <p className="font-bold text-foreground truncate">{user?.firstName} {user?.lastName}</p>
                    <p className="text-xs text-muted-foreground truncate mt-0.5">{user?.email}</p>
                  </div>
                  
                  <div className="p-2 space-y-1">
                    {[
                      { icon: User, label: "My Profile", href: "/profile" },
                      { icon: Settings, label: "Settings", href: "#" },
                      { icon: HelpCircle, label: "Help & Support", href: "#" }
                    ].map(item => (
                      <Link 
                        key={item.label}
                        href={item.href}
                        onClick={() => setProfileOpen(false)}
                        className="flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium text-muted-foreground hover:text-foreground hover:bg-muted transition-colors"
                      >
                        <item.icon className="w-4 h-4" />
                        {item.label}
                      </Link>
                    ))}
                  </div>
                  
                  <div className="p-2 border-t border-border">
                    <button 
                      onClick={handleLogout}
                      className="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium text-red-400 hover:text-red-500 hover:bg-red-500/10 transition-colors"
                    >
                      <LogOut className="w-4 h-4" />
                      Sign Out
                    </button>
                  </div>
                </motion.div>
              )}
            </AnimatePresence>
          </div>
        </div>
      </div>
    </nav>
  );
}

export default TopNav;

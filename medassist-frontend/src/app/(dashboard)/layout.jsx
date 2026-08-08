import { Sidebar } from "@/components/layout/Sidebar";
import TopNav from "@/components/layout/TopNav";
export const metadata = {
    title: {
        default: "Dashboard",
        template: "%s | MedAssist AI X",
    },
};
export default function DashboardLayout({ children, }) {
    return (<div className="flex h-screen bg-background overflow-hidden">
      {/* Sidebar */}
      <Sidebar />

      {/* Main content area */}
      <div className="flex flex-col flex-1 min-w-0 overflow-hidden">
        <TopNav />
        <main className="flex-1 overflow-y-auto">
          <div className="gradient-mesh min-h-full p-6">
            {children}
          </div>
        </main>
      </div>
    </div>);
}

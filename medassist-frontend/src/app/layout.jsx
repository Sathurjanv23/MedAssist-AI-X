import { Inter, Outfit } from "next/font/google";
import "./globals.css";
import { Providers } from "./providers";
const inter = Inter({
    subsets: ["latin"],
    variable: "--font-inter",
    display: "swap",
});
const outfit = Outfit({
    subsets: ["latin"],
    variable: "--font-outfit",
    display: "swap",
});
export const metadata = {
    title: {
        default: "MedAssist AI X — Personal AI Healthcare OS",
        template: "%s | MedAssist AI X",
    },
    description: "MedAssist AI X is your Personal AI Healthcare Operating System. Understand your health, predict risks, and make better healthcare decisions with AI-powered insights.",
    keywords: [
        "AI healthcare",
        "medical AI",
        "health assistant",
        "medical reports",
        "health tracking",
        "AI doctor",
        "Sri Lanka health tech",
    ],
    authors: [{ name: "MedAssist AI X Team" }],
    creator: "MedAssist AI X",
    openGraph: {
        title: "MedAssist AI X — Personal AI Healthcare OS",
        description: "Your Personal AI Healthcare Operating System",
        type: "website",
        locale: "en_US",
    },
    manifest: "/manifest.json",
    themeColor: undefined,
};
export const viewport = {
    themeColor: [
        { media: "(prefers-color-scheme: dark)", color: "#030712" },
        { media: "(prefers-color-scheme: light)", color: "#f8fafc" },
    ],
};
export default function RootLayout({ children, }) {
    return (<html lang="en" data-scroll-behavior="smooth" suppressHydrationWarning>
      <body className={`${inter.variable} ${outfit.variable} antialiased`}>
        <Providers>{children}</Providers>
      </body>
    </html>);
}

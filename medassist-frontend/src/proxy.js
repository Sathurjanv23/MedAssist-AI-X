import { NextResponse } from "next/server";
// ─────────────────────────────────────────────────────────────────────────────
// MedAssist AI X — Route Protection Middleware
// ─────────────────────────────────────────────────────────────────────────────
const PUBLIC_ROUTES = ["/", "/login", "/register", "/about"];
const AUTH_ROUTES = ["/login", "/register"];
const ADMIN_ROUTES = ["/admin"];
const DOCTOR_ROUTES = ["/doctor"];
export function proxy(request) {
    const { pathname } = request.nextUrl;
    const token = request.cookies.get("medassist_token")?.value;
    // Allow public routes
    if (PUBLIC_ROUTES.some((r) => pathname === r)) {
        // Redirect authenticated users away from auth pages
        if (token && AUTH_ROUTES.includes(pathname)) {
            return NextResponse.redirect(new URL("/dashboard", request.url));
        }
        return NextResponse.next();
    }
    // Protect dashboard and sub-routes
    if (!token && !PUBLIC_ROUTES.includes(pathname)) {
        const loginUrl = new URL("/login", request.url);
        loginUrl.searchParams.set("redirect", pathname);
        return NextResponse.redirect(loginUrl);
    }
    return NextResponse.next();
}
export const config = {
    matcher: [
        "/((?!api|_next/static|_next/image|favicon.ico|manifest.json|icons).*)",
    ],
};

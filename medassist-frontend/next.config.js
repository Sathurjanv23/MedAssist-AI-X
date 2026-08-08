const nextConfig = {
    images: {
        remotePatterns: [
            { protocol: "http", hostname: "localhost" },
            { protocol: "https", hostname: "api.medassist.ai" },
            { protocol: "https", hostname: "cdn.medassist.ai" },
        ],
        formats: ["image/webp", "image/avif"],
    },
    experimental: {
        optimizePackageImports: ["lucide-react", "recharts"],
    },
    async headers() {
        return [
            {
                source: "/(.*)",
                headers: [
                    { key: "X-Frame-Options", value: "DENY" },
                    { key: "X-Content-Type-Options", value: "nosniff" },
                    { key: "Referrer-Policy", value: "strict-origin-when-cross-origin" },
                    { key: "X-XSS-Protection", value: "1; mode=block" },
                ],
            },
        ];
    },
};
module.exports = nextConfig;

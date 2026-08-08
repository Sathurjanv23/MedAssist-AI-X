"use client";
import { useState, useRef, useEffect } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { useMutation, useQuery } from "@tanstack/react-query";
import { Send, User, RotateCcw, AlertTriangle, MessageSquare, Loader2, StopCircle } from "lucide-react";
import { GlowCard, AIOrb, PageTransition, SectionHeader } from "@/components/common";
import { aiApi } from "@/lib/api";
import { useAuthStore } from "@/store/auth.store";
import { formatTime } from "@/lib/utils";

// Generate a random session ID for the chat (resets on page reload or explicitly)
const generateSessionId = () => Math.random().toString(36).substring(2, 15);

export default function AIChatPage() {
  const { user } = useAuthStore();
  const [messages, setMessages] = useState([
    {
      id: "msg_welcome",
      role: "assistant",
      content: `Hello ${user?.firstName || "there"}! I'm your MedAssist AI. I have access to your health twin, medical reports, and recent timeline. How can I help you today?`,
      timestamp: new Date().toISOString(),
    }
  ]);
  const [input, setInput] = useState("");
  const [sessionId, setSessionId] = useState(generateSessionId());
  const messagesEndRef = useRef(null);

  const { data: status } = useQuery({
    queryKey: ["ai-status"],
    queryFn: aiApi.status,
    refetchInterval: 30000, // Check every 30s
  });

  const chatMutation = useMutation({
    mutationFn: (msg) => aiApi.chat(msg, sessionId),
    onSuccess: (data) => {
      setMessages(prev => [...prev, {
        id: `msg_${Date.now()}`,
        role: "assistant",
        content: data.message,
        timestamp: new Date().toISOString(),
      }]);
    },
    onError: (err) => {
      setMessages(prev => [...prev, {
        id: `msg_err_${Date.now()}`,
        role: "assistant",
        content: err?.response?.data?.message || "I'm sorry, I'm having trouble connecting to my neural core right now. Please try again in a moment.",
        timestamp: new Date().toISOString(),
        isError: true,
      }]);
    }
  });

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages, chatMutation.isPending]);

  const handleSend = (e) => {
    e.preventDefault();
    if (!input.trim() || chatMutation.isPending) return;

    const userMsg = {
      id: `msg_u_${Date.now()}`,
      role: "user",
      content: input,
      timestamp: new Date().toISOString(),
    };

    setMessages(prev => [...prev, userMsg]);
    setInput("");
    chatMutation.mutate(input);
  };

  const resetChat = () => {
    setSessionId(generateSessionId());
    setMessages([
      {
        id: `msg_welcome_${Date.now()}`,
        role: "assistant",
        content: `Chat session reset. I'm ready for a new topic!`,
        timestamp: new Date().toISOString(),
      }
    ]);
  };

  const suggestedQuestions = [
    "Summarize my latest blood test report",
    "Is my blood pressure trending well?",
    "What should I eat to improve my hemoglobin?",
    "When is my next medication due?",
  ];

  return (
    <PageTransition>
      <div className="max-w-[1000px] mx-auto h-[calc(100vh-100px)] flex flex-col space-y-4">
        {/* Header */}
        <div className="flex items-center justify-between shrink-0">
          <div>
            <h1 className="text-2xl font-bold text-foreground flex items-center gap-3">
              <AIOrb size={28} /> AI Health Assistant
            </h1>
            <p className="text-muted-foreground text-sm mt-1">Context-aware conversational AI based on your medical data.</p>
          </div>
          <div className="flex items-center gap-3">
            {status?.status === "DOWN" ? (
              <span className="flex items-center gap-1.5 text-xs font-medium text-red-400 bg-red-500/10 border border-red-500/20 px-3 py-1.5 rounded-full">
                <StopCircle className="w-3.5 h-3.5" /> AI Offline
              </span>
            ) : (
              <span className="flex items-center gap-1.5 text-xs font-medium text-emerald-400 bg-emerald-500/10 border border-emerald-500/20 px-3 py-1.5 rounded-full">
                <span className="relative flex h-2 w-2">
                  <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
                  <span className="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
                </span>
                AI Online
              </span>
            )}
            <button onClick={resetChat} className="w-9 h-9 rounded-xl border border-border bg-card flex items-center justify-center text-muted-foreground hover:bg-muted hover:text-foreground transition-colors" title="Reset Chat">
              <RotateCcw className="w-4 h-4" />
            </button>
          </div>
        </div>

        {/* Disclaimer */}
        <div className="shrink-0 p-3 rounded-xl border border-primary/20 bg-primary/5 flex gap-3 text-xs text-primary/80">
          <AlertTriangle className="w-4 h-4 shrink-0 mt-0.5" />
          <p>
            <span className="font-semibold">Medical Disclaimer:</span> This AI provides information based on your uploaded records and general medical knowledge. It is <span className="font-bold underline">not</span> a doctor. Always consult a healthcare professional for medical advice, diagnosis, or treatment.
          </p>
        </div>

        {/* Chat Area */}
        <GlowCard delay={0.1} glowColor="indigo" noPadding className="flex-1 flex flex-col min-h-0">
          {/* Messages */}
          <div className="flex-1 overflow-y-auto p-4 sm:p-6 space-y-6">
            <AnimatePresence initial={false}>
              {messages.map((msg) => (
                <motion.div
                  key={msg.id}
                  initial={{ opacity: 0, y: 10, scale: 0.98 }}
                  animate={{ opacity: 1, y: 0, scale: 1 }}
                  className={`flex items-start gap-4 ${msg.role === "user" ? "flex-row-reverse" : ""}`}
                >
                  {/* Avatar */}
                  <div className={`w-10 h-10 rounded-full flex items-center justify-center shrink-0 shadow-lg ${
                    msg.role === "assistant" 
                      ? "bg-primary/10 border border-primary/20" 
                      : "gradient-brand text-white"
                  }`}>
                    {msg.role === "assistant" ? (
                      <MessageSquare className="w-5 h-5 text-primary" />
                    ) : (
                      <span className="font-bold text-sm">{user?.firstName?.[0] || "U"}</span>
                    )}
                  </div>

                  {/* Bubble */}
                  <div className={`max-w-[80%] ${msg.role === "user" ? "text-right" : "text-left"}`}>
                    <div className={`inline-block p-4 rounded-2xl ${
                      msg.role === "user" 
                        ? "bg-primary text-primary-foreground rounded-tr-sm shadow-md" 
                        : msg.isError
                          ? "bg-red-500/10 border border-red-500/20 text-red-400 rounded-tl-sm"
                          : "bg-muted border border-border text-foreground rounded-tl-sm shadow-sm"
                    }`}>
                      <p className="text-sm whitespace-pre-wrap leading-relaxed">{msg.content}</p>
                    </div>
                    <p className="text-[10px] text-muted-foreground mt-1 px-1">
                      {formatTime(msg.timestamp)}
                    </p>
                  </div>
                </motion.div>
              ))}

              {/* Typing indicator */}
              {chatMutation.isPending && (
                <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="flex items-start gap-4">
                  <div className="w-10 h-10 rounded-full bg-primary/10 border border-primary/20 flex items-center justify-center shrink-0 shadow-lg">
                    <AIOrb size={20} />
                  </div>
                  <div className="bg-muted border border-border p-4 rounded-2xl rounded-tl-sm flex items-center gap-1.5 h-[52px]">
                    <div className="w-2 h-2 rounded-full bg-primary/40 animate-bounce" style={{ animationDelay: "0ms" }} />
                    <div className="w-2 h-2 rounded-full bg-primary/60 animate-bounce" style={{ animationDelay: "150ms" }} />
                    <div className="w-2 h-2 rounded-full bg-primary animate-bounce" style={{ animationDelay: "300ms" }} />
                  </div>
                </motion.div>
              )}
            </AnimatePresence>
            <div ref={messagesEndRef} className="h-1" />
          </div>

          {/* Input Area */}
          <div className="shrink-0 p-4 border-t border-border bg-card rounded-b-2xl">
            {messages.length === 1 && (
              <div className="flex flex-wrap gap-2 mb-4">
                {suggestedQuestions.map((q, i) => (
                  <button
                    key={i}
                    onClick={() => setInput(q)}
                    className="text-xs text-primary border border-primary/20 bg-primary/5 hover:bg-primary/15 rounded-full px-3 py-1.5 transition-colors"
                  >
                    {q}
                  </button>
                ))}
              </div>
            )}
            
            <form onSubmit={handleSend} className="relative">
              <input
                value={input}
                onChange={(e) => setInput(e.target.value)}
                placeholder="Ask about your health, reports, or medicines..."
                disabled={chatMutation.isPending}
                className="w-full h-14 pl-5 pr-14 bg-muted border border-border rounded-xl text-sm text-foreground placeholder:text-muted-foreground outline-none focus:border-primary/50 transition-colors disabled:opacity-50"
              />
              <button
                type="submit"
                disabled={!input.trim() || chatMutation.isPending}
                className="absolute right-2 top-1/2 -translate-y-1/2 w-10 h-10 rounded-lg bg-primary hover:bg-primary/90 flex items-center justify-center text-white transition-colors disabled:opacity-50 disabled:hover:bg-primary"
              >
                {chatMutation.isPending ? <Loader2 className="w-4 h-4 animate-spin" /> : <Send className="w-4 h-4 ml-0.5" />}
              </button>
            </form>
          </div>
        </GlowCard>
      </div>
    </PageTransition>
  );
}

"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { Activity, Boxes, LayoutDashboard, PackagePlus, ShoppingCart, Zap } from "lucide-react";
import type { ReactNode } from "react";

const nav = [
  { href: "/", label: "Overview", icon: LayoutDashboard },
  { href: "/orders", label: "Orders", icon: ShoppingCart },
  { href: "/inventory", label: "Inventory", icon: Boxes },
  { href: "/activity", label: "Event activity", icon: Activity },
];

export function AppShell({ children }: { children: ReactNode }) {
  const pathname = usePathname();

  return (
    <div className="min-h-screen lg:grid lg:grid-cols-[250px_1fr]">
      <aside className="border-b border-white/10 bg-[#08131d]/90 px-5 py-5 lg:min-h-screen lg:border-b-0 lg:border-r lg:px-4">
        <div className="flex items-center gap-3 px-2">
          <div className="grid h-10 w-10 place-items-center rounded-2xl bg-gradient-to-br from-teal-300 to-blue-400 text-slate-950 shadow-lg shadow-cyan-500/10">
            <Zap size={20} strokeWidth={2.8} />
          </div>
          <div>
            <div className="text-sm font-black tracking-wide">COMMERCE</div>
            <div className="text-xs text-slate-400">Control Center</div>
          </div>
        </div>

        <nav className="mt-6 grid grid-cols-2 gap-2 sm:grid-cols-4 lg:grid-cols-1">
          {nav.map(({ href, label, icon: Icon }) => {
            const active = href === "/" ? pathname === "/" : pathname.startsWith(href);
            return (
              <Link
                key={href}
                href={href}
                className={`flex items-center gap-3 rounded-xl px-3 py-3 text-sm font-semibold transition ${
                  active
                    ? "bg-white/10 text-white ring-1 ring-white/10"
                    : "text-slate-400 hover:bg-white/5 hover:text-slate-100"
                }`}
              >
                <Icon size={17} />
                {label}
              </Link>
            );
          })}
        </nav>

        <div className="mt-6 hidden rounded-2xl border border-cyan-300/10 bg-cyan-300/[.035] p-4 lg:block">
          <div className="flex items-center gap-2 text-xs font-bold text-cyan-200">
            <PackagePlus size={15} /> LOCAL STACK
          </div>
          <p className="mt-2 text-xs leading-5 text-slate-400">
            Next.js UI → Spring Boot services → Kafka → MySQL 8.4
          </p>
        </div>
      </aside>

      <main className="min-w-0">
        <header className="flex items-center justify-between border-b border-white/10 px-5 py-4 md:px-8">
          <div>
            <div className="text-xs font-bold uppercase tracking-[.2em] text-teal-300">Event-driven commerce</div>
            <div className="mt-1 text-sm text-slate-400">Local operations dashboard</div>
          </div>
          <div className="badge info">Frontend :3000</div>
        </header>
        <div className="px-5 py-6 md:px-8 md:py-8">{children}</div>
      </main>
    </div>
  );
}

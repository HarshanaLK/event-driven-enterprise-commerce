import type { ReactNode } from "react";

export function PageTitle({ title, subtitle, actions }: { title: string; subtitle: string; actions?: ReactNode }) {
  return (
    <div className="mb-6 flex flex-col justify-between gap-4 sm:flex-row sm:items-end">
      <div>
        <h1 className="text-2xl font-black tracking-tight md:text-3xl">{title}</h1>
        <p className="mt-1 max-w-2xl text-sm leading-6 text-slate-400">{subtitle}</p>
      </div>
      {actions}
    </div>
  );
}

export function StatusBadge({ value }: { value: string }) {
  const normalized = value.toUpperCase();
  const cls = ["CONFIRMED", "CAPTURED", "UP", "ORDER_CONFIRMED"].includes(normalized)
    ? "ok"
    : ["FAILED", "CANCELLED", "DOWN", "ORDER_CANCELLED"].includes(normalized)
      ? "bad"
      : ["PENDING", "PAYMENT_PENDING"].includes(normalized)
        ? "warn"
        : "info";
  return <span className={`badge ${cls}`}>{value.replaceAll("_", " ")}</span>;
}

export function EmptyState({ children }: { children: ReactNode }) {
  return <div className="rounded-xl border border-dashed border-white/10 px-4 py-10 text-center text-sm text-slate-500">{children}</div>;
}

export function ErrorBox({ message }: { message: string }) {
  return <div className="rounded-xl border border-rose-400/20 bg-rose-400/[.06] px-4 py-3 text-sm text-rose-200">{message}</div>;
}

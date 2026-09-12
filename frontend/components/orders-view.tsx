"use client";

import { useCallback, useEffect, useMemo, useState } from "react";
import { Plus, RefreshCw, Search, Trash2 } from "lucide-react";
import type { Order, OrderLine } from "@/lib/types";
import { EmptyState, ErrorBox, PageTitle, StatusBadge } from "./ui";

const money = new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" });
const products = [
  { sku: "LAPTOP-PRO-14", productName: "Pro Laptop 14", unitPrice: 1499 },
  { sku: "MECH-KEY-01", productName: "Mechanical Keyboard", unitPrice: 129 },
  { sku: "MOUSE-WL-02", productName: "Wireless Mouse", unitPrice: 29.5 },
];

type DraftLine = OrderLine & { key: string };

function newLine(): DraftLine {
  const p = products[0];
  return { ...p, quantity: 1, key: crypto.randomUUID() };
}

export function OrdersView() {
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [query, setQuery] = useState("");
  const [customerId, setCustomerId] = useState("9cd71fd3-ab62-4d44-b564-f70f05d8af34");
  const [paymentToken, setPaymentToken] = useState("tok_demo_success");
  const [lines, setLines] = useState<DraftLine[]>([newLine()]);

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const r = await fetch("/api/orders", { cache: "no-store" });
      if (!r.ok) throw new Error("Could not load orders. Make sure order-service is running on port 8081.");
      setOrders(await r.json());
    } catch (e) {
      setError((e as Error).message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { void load(); }, [load]);

  const filtered = useMemo(() => {
    const q = query.toLowerCase().trim();
    return !q ? orders : orders.filter((o) => o.id.toLowerCase().includes(q) || o.customerId.toLowerCase().includes(q) || o.status.toLowerCase().includes(q));
  }, [orders, query]);

  const total = lines.reduce((sum, l) => sum + Number(l.unitPrice) * l.quantity, 0);

  async function createOrder(e: React.FormEvent) {
    e.preventDefault();
    setSaving(true);
    setError("");
    try {
      const r = await fetch("/api/orders", {
        method: "POST",
        headers: { "Content-Type": "application/json", "Idempotency-Key": `web-${crypto.randomUUID()}` },
        body: JSON.stringify({
          customerId,
          paymentMethodToken: paymentToken,
          items: lines.map(({ key: _key, ...line }) => line),
        }),
      });
      const body = await r.json();
      if (!r.ok) throw new Error(body.message ?? "Order creation failed");
      setLines([newLine()]);
      await load();
    } catch (e) {
      setError((e as Error).message);
    } finally {
      setSaving(false);
    }
  }

  function changeProduct(index: number, sku: string) {
    const p = products.find((x) => x.sku === sku)!;
    setLines((old) => old.map((line, i) => i === index ? { ...line, ...p } : line));
  }

  return (
    <>
      <PageTitle title="Orders" subtitle="Create checkout sagas and watch orders move from PENDING through inventory reservation and payment." actions={<button className="btn-secondary flex items-center gap-2" onClick={() => void load()}><RefreshCw size={15} className={loading ? "animate-spin" : ""} /> Refresh</button>} />
      {error && <div className="mb-5"><ErrorBox message={error} /></div>}

      <div className="grid gap-6 xl:grid-cols-[.85fr_1.3fr]">
        <form className="card p-5" onSubmit={createOrder}>
          <div className="flex items-center justify-between"><div><h2 className="font-bold">Create order</h2><p className="mt-1 text-xs text-slate-500">Uses the real order-service REST API</p></div><StatusBadge value="LIVE" /></div>
          <label className="mt-5 block text-xs font-bold uppercase tracking-wider text-slate-500">Customer UUID</label>
          <input className="input mt-2 mono text-xs" value={customerId} onChange={(e) => setCustomerId(e.target.value)} required />
          <label className="mt-4 block text-xs font-bold uppercase tracking-wider text-slate-500">Payment token</label>
          <input className="input mt-2" value={paymentToken} onChange={(e) => setPaymentToken(e.target.value)} required />
          <p className="mt-2 text-xs text-slate-500">Use <span className="mono text-rose-300">fail_*</span> to intentionally test the payment failure/compensation path.</p>

          <div className="mt-5 space-y-3">
            {lines.map((line, index) => (
              <div key={line.key} className="rounded-xl border border-white/10 bg-black/10 p-3">
                <div className="flex gap-2">
                  <select className="input" value={line.sku} onChange={(e) => changeProduct(index, e.target.value)}>{products.map((p) => <option key={p.sku} value={p.sku}>{p.productName}</option>)}</select>
                  {lines.length > 1 && <button type="button" className="btn-secondary" onClick={() => setLines((old) => old.filter((x) => x.key !== line.key))}><Trash2 size={15} /></button>}
                </div>
                <div className="mt-3 grid grid-cols-2 gap-3">
                  <div><div className="text-xs text-slate-500">Quantity</div><input className="input mt-1" type="number" min={1} value={line.quantity} onChange={(e) => setLines((old) => old.map((x, i) => i === index ? { ...x, quantity: Math.max(1, Number(e.target.value)) } : x))} /></div>
                  <div><div className="text-xs text-slate-500">Unit price</div><input className="input mt-1" type="number" min="0.01" step="0.01" value={line.unitPrice} onChange={(e) => setLines((old) => old.map((x, i) => i === index ? { ...x, unitPrice: Number(e.target.value) } : x))} /></div>
                </div>
              </div>
            ))}
          </div>
          <button type="button" className="btn-secondary mt-3 flex items-center gap-2" onClick={() => setLines((old) => [...old, newLine()])}><Plus size={15} /> Add item</button>
          <div className="mt-5 flex items-center justify-between border-t border-white/10 pt-4"><div><div className="text-xs text-slate-500">Order total</div><div className="text-xl font-black">{money.format(total)}</div></div><button className="btn-primary" disabled={saving}>{saving ? "Submitting…" : "Submit order"}</button></div>
        </form>

        <section className="card p-5">
          <div className="flex flex-col justify-between gap-3 sm:flex-row sm:items-center"><div><h2 className="font-bold">Order history</h2><p className="mt-1 text-xs text-slate-500">Newest first</p></div><div className="relative w-full sm:w-72"><Search className="absolute left-3 top-3 text-slate-500" size={15} /><input className="input pl-9" placeholder="Search ID, customer, status" value={query} onChange={(e) => setQuery(e.target.value)} /></div></div>
          <div className="mt-4 table-wrap">
            {filtered.length ? <table><thead><tr><th>Order</th><th>Status</th><th>Items</th><th>Total</th><th>Created</th></tr></thead><tbody>{filtered.map((o) => <tr key={o.id}><td><div className="mono text-xs text-teal-200">{o.id.slice(0, 12)}…</div><div className="mt-1 mono text-[11px] text-slate-500">{o.customerId.slice(0, 12)}…</div></td><td><StatusBadge value={o.status} /></td><td>{o.items.reduce((sum, x) => sum + x.quantity, 0)}</td><td>{money.format(Number(o.totalAmount))}</td><td className="text-slate-400">{new Date(o.createdAt).toLocaleString()}</td></tr>)}</tbody></table> : <EmptyState>{loading ? "Loading orders…" : "No orders match your search."}</EmptyState>}
          </div>
        </section>
      </div>
    </>
  );
}

"use client";

import { useCallback, useEffect, useState } from "react";
import { PackagePlus, RefreshCw } from "lucide-react";
import type { Stock } from "@/lib/types";
import { EmptyState, ErrorBox, PageTitle } from "./ui";

export function InventoryView() {
  const [rows, setRows] = useState<Stock[]>([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const [sku, setSku] = useState("");
  const [name, setName] = useState("");
  const [qty, setQty] = useState(10);
  const [adjustments, setAdjustments] = useState<Record<string, number>>({});

  const load = useCallback(async () => {
    setLoading(true); setError("");
    try {
      const r = await fetch("/api/inventory", { cache: "no-store" });
      if (!r.ok) throw new Error("Could not load inventory. Make sure inventory-service is running on port 8082.");
      setRows(await r.json());
    } catch (e) { setError((e as Error).message); }
    finally { setLoading(false); }
  }, []);

  useEffect(() => { void load(); }, [load]);

  async function create(e: React.FormEvent) {
    e.preventDefault(); setError("");
    try {
      const r = await fetch(`/api/inventory/${encodeURIComponent(sku)}`, { method: "PUT", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ productName: name, availableQuantity: qty }) });
      const body = await r.json(); if (!r.ok) throw new Error(body.message ?? "Could not create stock row");
      setSku(""); setName(""); setQty(10); await load();
    } catch (e) { setError((e as Error).message); }
  }

  async function adjust(skuValue: string) {
    const delta = adjustments[skuValue] ?? 0;
    if (!delta) return;
    setError("");
    try {
      const r = await fetch(`/api/inventory/${encodeURIComponent(skuValue)}/adjust`, { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ delta }) });
      const body = await r.json(); if (!r.ok) throw new Error(body.message ?? "Stock adjustment failed");
      setAdjustments((x) => ({ ...x, [skuValue]: 0 })); await load();
    } catch (e) { setError((e as Error).message); }
  }

  return (
    <>
      <PageTitle title="Inventory" subtitle="Manage stock rows used by the inventory reservation service." actions={<button className="btn-secondary flex items-center gap-2" onClick={() => void load()}><RefreshCw size={15} className={loading ? "animate-spin" : ""} /> Refresh</button>} />
      {error && <div className="mb-5"><ErrorBox message={error} /></div>}
      <div className="grid gap-6 xl:grid-cols-[.65fr_1.35fr]">
        <form className="card p-5" onSubmit={create}>
          <div className="flex items-center gap-2"><PackagePlus size={18} className="text-teal-300" /><h2 className="font-bold">Create stock item</h2></div>
          <label className="mt-5 block text-xs font-bold uppercase tracking-wider text-slate-500">SKU</label><input className="input mt-2 mono" value={sku} onChange={(e) => setSku(e.target.value.toUpperCase())} placeholder="MONITOR-27" required />
          <label className="mt-4 block text-xs font-bold uppercase tracking-wider text-slate-500">Product name</label><input className="input mt-2" value={name} onChange={(e) => setName(e.target.value)} placeholder="27-inch Monitor" required />
          <label className="mt-4 block text-xs font-bold uppercase tracking-wider text-slate-500">Initial available quantity</label><input className="input mt-2" type="number" min={0} value={qty} onChange={(e) => setQty(Number(e.target.value))} required />
          <button className="btn-primary mt-5 w-full">Create if missing</button>
        </form>

        <section className="card p-5">
          <h2 className="font-bold">Current stock</h2><p className="mt-1 text-xs text-slate-500">Positive adjustment adds stock; negative adjustment removes available stock.</p>
          <div className="mt-4 table-wrap">
            {rows.length ? <table><thead><tr><th>SKU</th><th>Product</th><th>Available</th><th>Reserved</th><th>Adjustment</th></tr></thead><tbody>{rows.map((row) => <tr key={row.sku}><td className="mono text-xs text-teal-200">{row.sku}</td><td>{row.productName}</td><td className="font-bold">{row.availableQuantity}</td><td>{row.reservedQuantity}</td><td><div className="flex min-w-56 gap-2"><input className="input" type="number" value={adjustments[row.sku] ?? 0} onChange={(e) => setAdjustments((x) => ({ ...x, [row.sku]: Number(e.target.value) }))} /><button className="btn-secondary" onClick={() => void adjust(row.sku)}>Apply</button></div></td></tr>)}</tbody></table> : <EmptyState>{loading ? "Loading inventory…" : "No stock rows found."}</EmptyState>}
          </div>
        </section>
      </div>
    </>
  );
}

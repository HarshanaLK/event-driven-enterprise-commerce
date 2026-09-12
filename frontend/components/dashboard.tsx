"use client";

import { useCallback, useEffect, useMemo, useState } from "react";
import { Boxes, CircleDollarSign, RefreshCw, ShoppingCart, Wifi } from "lucide-react";
import type { DashboardData } from "@/lib/types";
import { EmptyState, ErrorBox, PageTitle, StatusBadge } from "./ui";

const money = new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" });

export function Dashboard() {
  const [data, setData] = useState<DashboardData | null>(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const r = await fetch("/api/dashboard", { cache: "no-store" });
      if (!r.ok) throw new Error("Could not load dashboard data");
      setData(await r.json());
    } catch (e) {
      setError((e as Error).message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { void load(); }, [load]);

  const revenue = useMemo(
    () => data?.payments.filter((p) => p.status === "CAPTURED").reduce((sum, p) => sum + Number(p.amount), 0) ?? 0,
    [data]
  );
  const available = data?.inventory.reduce((sum, i) => sum + i.availableQuantity, 0) ?? 0;

  return (
    <>
      <PageTitle
        title="System overview"
        subtitle="A live view of the four Spring Boot services and the commerce workflow flowing through Kafka."
        actions={<button className="btn-secondary flex items-center gap-2" onClick={() => void load()}><RefreshCw size={15} className={loading ? "animate-spin" : ""} /> Refresh</button>}
      />
      {error && <div className="mb-5"><ErrorBox message={error} /></div>}

      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        <Metric icon={<ShoppingCart size={18} />} label="Total orders" value={data?.orders.length ?? 0} />
        <Metric icon={<CircleDollarSign size={18} />} label="Captured revenue" value={money.format(revenue)} />
        <Metric icon={<Boxes size={18} />} label="Available units" value={available} />
        <Metric icon={<Wifi size={18} />} label="Services online" value={`${Object.values(data?.serviceStatus ?? {}).filter(Boolean).length}/4`} />
      </div>

      <div className="mt-6 grid gap-6 xl:grid-cols-[1.5fr_.8fr]">
        <section className="card p-4 md:p-5">
          <div className="mb-4 flex items-center justify-between">
            <div><h2 className="font-bold">Recent orders</h2><p className="mt-1 text-xs text-slate-500">Latest order saga states</p></div>
          </div>
          {data?.orders.length ? (
            <div className="table-wrap">
              <table>
                <thead><tr><th>Order</th><th>Status</th><th>Total</th><th>Created</th></tr></thead>
                <tbody>{data.orders.slice(0, 8).map((o) => (
                  <tr key={o.id}><td><div className="mono text-xs text-slate-300">{o.id.slice(0, 8)}…</div><div className="mt-1 text-xs text-slate-500">{o.items.length} item(s)</div></td><td><StatusBadge value={o.status} /></td><td>{money.format(Number(o.totalAmount))}</td><td className="text-slate-400">{new Date(o.createdAt).toLocaleString()}</td></tr>
                ))}</tbody>
              </table>
            </div>
          ) : <EmptyState>{loading ? "Loading orders…" : "No orders yet. Create one from the Orders page."}</EmptyState>}
        </section>

        <section className="card p-4 md:p-5">
          <h2 className="font-bold">Service health</h2>
          <p className="mt-1 text-xs text-slate-500">Spring Boot actuator checks</p>
          <div className="mt-4 space-y-3">
            {Object.entries(data?.serviceStatus ?? { Order: false, Inventory: false, Payment: false, Notification: false }).map(([name, ok]) => (
              <div key={name} className="flex items-center justify-between rounded-xl border border-white/10 bg-black/10 px-3 py-3">
                <div><div className="text-sm font-semibold">{name} service</div><div className="mt-1 text-xs text-slate-500">localhost:{name === "Order" ? 8081 : name === "Inventory" ? 8082 : name === "Payment" ? 8083 : 8084}</div></div>
                <StatusBadge value={ok ? "UP" : "DOWN"} />
              </div>
            ))}
          </div>
        </section>
      </div>

      <section className="card mt-6 p-4 md:p-5">
        <h2 className="font-bold">Inventory snapshot</h2>
        <p className="mt-1 text-xs text-slate-500">Seeded and manually-created stock rows</p>
        <div className="mt-4 table-wrap">
          <table>
            <thead><tr><th>SKU</th><th>Product</th><th>Available</th><th>Reserved</th></tr></thead>
            <tbody>{data?.inventory.map((i) => <tr key={i.sku}><td className="mono text-xs text-teal-200">{i.sku}</td><td>{i.productName}</td><td>{i.availableQuantity}</td><td>{i.reservedQuantity}</td></tr>)}</tbody>
          </table>
        </div>
      </section>
    </>
  );
}

function Metric({ icon, label, value }: { icon: React.ReactNode; label: string; value: string | number }) {
  return <div className="card p-5"><div className="flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-slate-500">{icon}{label}</div><div className="mt-3 text-2xl font-black">{value}</div></div>;
}

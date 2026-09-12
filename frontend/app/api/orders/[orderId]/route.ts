import { NextResponse } from "next/server";
import { safeJson, serviceUrl } from "@/lib/server-api";
import type { Notification, Order, Payment } from "@/lib/types";

export const dynamic = "force-dynamic";

export async function GET(_request: Request, context: { params: Promise<{ orderId: string }> }) {
  const { orderId } = await context.params;
  const order = await safeJson<Order | null>(serviceUrl("order", `/api/orders/${orderId}`), null);
  if (!order) return NextResponse.json({ message: "Order not found" }, { status: 404 });

  const [payment, notifications] = await Promise.all([
    safeJson<Payment | null>(serviceUrl("payment", `/api/payments/order/${orderId}`), null),
    safeJson<Notification[]>(serviceUrl("notification", `/api/notifications/order/${orderId}`), []),
  ]);

  return NextResponse.json({ order, payment, notifications });
}

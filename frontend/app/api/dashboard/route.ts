import { NextResponse } from "next/server";
import { isHealthy, safeJson, serviceUrl } from "@/lib/server-api";
import type { DashboardData, Notification, Order, Payment, Stock } from "@/lib/types";

export const dynamic = "force-dynamic";

export async function GET() {
  const [orders, inventory, payments, notifications, orderOk, inventoryOk, paymentOk, notificationOk] =
    await Promise.all([
      safeJson<Order[]>(serviceUrl("order", "/api/orders"), []),
      safeJson<Stock[]>(serviceUrl("inventory", "/api/inventory"), []),
      safeJson<Payment[]>(serviceUrl("payment", "/api/payments"), []),
      safeJson<Notification[]>(serviceUrl("notification", "/api/notifications"), []),
      isHealthy("order"),
      isHealthy("inventory"),
      isHealthy("payment"),
      isHealthy("notification"),
    ]);

  const data: DashboardData = {
    orders,
    inventory,
    payments,
    notifications,
    serviceStatus: {
      Order: orderOk,
      Inventory: inventoryOk,
      Payment: paymentOk,
      Notification: notificationOk,
    },
  };

  return NextResponse.json(data);
}

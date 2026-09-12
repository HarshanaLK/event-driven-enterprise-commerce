import { NextResponse } from "next/server";
import { safeJson, serviceUrl } from "@/lib/server-api";
import type { Notification, Payment } from "@/lib/types";

export const dynamic = "force-dynamic";

export async function GET() {
  const [payments, notifications] = await Promise.all([
    safeJson<Payment[]>(serviceUrl("payment", "/api/payments"), []),
    safeJson<Notification[]>(serviceUrl("notification", "/api/notifications"), []),
  ]);
  return NextResponse.json({ payments, notifications });
}

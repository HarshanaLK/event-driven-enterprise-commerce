import { NextResponse } from "next/server";
import { proxyJson, serviceUrl } from "@/lib/server-api";

export const dynamic = "force-dynamic";

export async function GET() {
  try {
    return NextResponse.json(await proxyJson(serviceUrl("order", "/api/orders")));
  } catch (error) {
    return NextResponse.json({ message: (error as Error).message }, { status: 502 });
  }
}

export async function POST(request: Request) {
  try {
    const body = await request.text();
    const idempotencyKey = request.headers.get("Idempotency-Key") ?? `web-${crypto.randomUUID()}`;
    const result = await proxyJson(serviceUrl("order", "/api/orders"), {
      method: "POST",
      body,
      headers: { "Idempotency-Key": idempotencyKey },
    });
    return NextResponse.json(result, { status: 202 });
  } catch (error) {
    return NextResponse.json({ message: (error as Error).message }, { status: 502 });
  }
}

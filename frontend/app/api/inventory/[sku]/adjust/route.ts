import { NextResponse } from "next/server";
import { proxyJson, serviceUrl } from "@/lib/server-api";

export const dynamic = "force-dynamic";

export async function POST(request: Request, context: { params: Promise<{ sku: string }> }) {
  try {
    const { sku } = await context.params;
    const body = await request.text();
    return NextResponse.json(
      await proxyJson(serviceUrl("inventory", `/api/inventory/${encodeURIComponent(sku)}/adjust`), {
        method: "POST",
        body,
      })
    );
  } catch (error) {
    return NextResponse.json({ message: (error as Error).message }, { status: 502 });
  }
}

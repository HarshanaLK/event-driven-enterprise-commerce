import { NextResponse } from "next/server";
import { proxyJson, serviceUrl } from "@/lib/server-api";

export const dynamic = "force-dynamic";

export async function GET() {
  try {
    return NextResponse.json(await proxyJson(serviceUrl("inventory", "/api/inventory")));
  } catch (error) {
    return NextResponse.json({ message: (error as Error).message }, { status: 502 });
  }
}

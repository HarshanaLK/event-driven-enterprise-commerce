const urls = {
  order: process.env.ORDER_SERVICE_URL ?? "http://localhost:8081",
  inventory: process.env.INVENTORY_SERVICE_URL ?? "http://localhost:8082",
  payment: process.env.PAYMENT_SERVICE_URL ?? "http://localhost:8083",
  notification: process.env.NOTIFICATION_SERVICE_URL ?? "http://localhost:8084",
};

export function serviceUrl(service: keyof typeof urls, path: string) {
  return `${urls[service]}${path}`;
}

export async function proxyJson(url: string, init?: RequestInit) {
  const response = await fetch(url, {
    ...init,
    cache: "no-store",
    headers: {
      "Content-Type": "application/json",
      ...(init?.headers ?? {}),
    },
  });

  const raw = await response.text();
  let body: unknown = null;
  if (raw) {
    try {
      body = JSON.parse(raw);
    } catch {
      body = { message: raw };
    }
  }

  if (!response.ok) {
    const message =
      typeof body === "object" && body && "message" in body
        ? String((body as { message: unknown }).message)
        : `Backend request failed with ${response.status}`;
    throw new Error(message);
  }

  return body;
}

export async function safeJson<T>(url: string, fallback: T): Promise<T> {
  try {
    return (await proxyJson(url)) as T;
  } catch {
    return fallback;
  }
}

export async function isHealthy(service: keyof typeof urls) {
  try {
    const response = await fetch(serviceUrl(service, "/actuator/health"), {
      cache: "no-store",
      signal: AbortSignal.timeout(1500),
    });
    return response.ok;
  } catch {
    return false;
  }
}

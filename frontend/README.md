# Commerce Control Center

Next.js + TypeScript + Tailwind CSS frontend for the event-driven enterprise commerce backend.

## Features

- Overview dashboard with Spring Boot service health
- Recent order status and captured-revenue summary
- Create orders using the real order-service API
- Test successful and failed payment paths (`fail_*` tokens trigger the failure path)
- View and adjust inventory
- Create stock items
- Review payments and notification side effects
- Same-origin Next.js API proxy, so no backend CORS changes are required

## Local run

```powershell
cd "C:\Users\Anonymous\Desktop\event-driven\frontend"
Copy-Item .env.local.example .env.local
npm install
npm run dev
```

Open http://localhost:3000.

The four Spring Boot services must be running on ports 8081-8084.

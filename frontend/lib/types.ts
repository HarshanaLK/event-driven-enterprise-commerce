export type OrderLine = {
  sku: string;
  productName: string;
  quantity: number;
  unitPrice: number;
};

export type Order = {
  id: string;
  customerId: string;
  status: string;
  totalAmount: number;
  cancellationReason: string | null;
  createdAt: string;
  items: OrderLine[];
};

export type Stock = {
  sku: string;
  productName: string;
  availableQuantity: number;
  reservedQuantity: number;
};

export type Payment = {
  id: string;
  orderId: string;
  amount: number;
  status: string;
  providerReference: string | null;
  failureReason: string | null;
  createdAt: string;
};

export type Notification = {
  id: string;
  orderId: string;
  type: string;
  channel: string;
  message: string;
  createdAt: string;
};

export type DashboardData = {
  orders: Order[];
  inventory: Stock[];
  payments: Payment[];
  notifications: Notification[];
  serviceStatus: Record<string, boolean>;
};

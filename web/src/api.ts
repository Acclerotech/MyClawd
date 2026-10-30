export interface WorkOrder {
  id: number;
  reference: string;
  clientEntityId: number;
  providerEntityId: number;
  currentStep: number;
  createdAt: string;
}

async function get<T>(path: string): Promise<T> {
  const res = await fetch(path, { headers: { Accept: 'application/json' } });
  if (!res.ok) throw new Error(`${res.status} ${res.statusText}`);
  return (await res.json()) as T;
}

export const api = {
  workOrders: () => get<WorkOrder[]>('/api/work-order/work-orders'),
};

export type DeliveryStatus = 'CREATED' | 'IN_TRANSIT' | 'DELIVERED' | 'CANCELED';

export interface Delivery {
  trackingCode: string;
  recipient: string;
  status: DeliveryStatus;
  createdAt: string | null;
  updatedAt: string | null;
  deliveredAt: string | null;
  returnDeadline: string | null;
}

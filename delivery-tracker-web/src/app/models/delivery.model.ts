import { ApiLinks } from './api.model';

export type DeliveryStatus = 'CREATED' | 'IN_TRANSIT' | 'DELIVERED' | 'CANCELED';

export interface Delivery {
  readonly trackingCode: string;
  readonly recipient: string;
  readonly status: DeliveryStatus;
  readonly createdAt: string | null;
  readonly updatedAt: string | null;
  readonly deliveredAt: string | null;
  readonly returnDeadline: string | null;
  readonly _links?: ApiLinks;
}

export interface CreateDeliveryRequest {
  readonly recipient: string;
}

export interface UpdateDeliveryStatusRequest {
  readonly status: DeliveryStatus;
}

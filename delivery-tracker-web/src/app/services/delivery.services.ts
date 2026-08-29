import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  CreateDeliveryRequest,
  Delivery,
  DeliveryStatus,
  UpdateDeliveryStatusRequest,
} from '../models/delivery.model';
import { PageResponse } from '../models/page.model';

@Injectable({
  providedIn: 'root',
})
export class DeliveryService {
  private readonly apiUrl = `${environment.apiUrl}/deliveries`;

  constructor(private http: HttpClient) {}

  getDeliveryByCode(code: string): Observable<Delivery> {
    const normalizedCode = this.normalizeTrackingCode(code);
    return this.http.get<Delivery>(`${this.apiUrl}/${normalizedCode}`);
  }

  createDelivery(recipientName: string): Observable<Delivery> {
    const body: CreateDeliveryRequest = { recipient: recipientName };

    return this.http.post<Delivery>(this.apiUrl, body);
  }

  updateDeliveryStatus(code: string, newStatus: DeliveryStatus): Observable<Delivery> {
    const body: UpdateDeliveryStatusRequest = { status: newStatus };

    return this.http.patch<Delivery>(
      `${this.apiUrl}/${this.normalizeTrackingCode(code)}/status`,
      body,
    );
  }

  getAllDeliveries(page = 0, size = 10): Observable<PageResponse<Delivery>> {
    const params = new HttpParams().set('page', page).set('size', size);

    return this.http.get<PageResponse<Delivery>>(this.apiUrl, { params });
  }

  private normalizeTrackingCode(code: string): string {
    return encodeURIComponent(code.trim().toUpperCase());
  }
}

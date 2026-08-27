import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Delivery } from '../delivery.model';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class DeliveryService {
  private readonly apiUrl = `${environment.apiUrl}/deliveries`;

  constructor(private http: HttpClient) {}

  getDeliveryByCode(code: string): Observable<Delivery> {
    const normalizedCode = encodeURIComponent(code.trim().toUpperCase());
    return this.http.get<Delivery>(`${this.apiUrl}/${normalizedCode}`);
  }

  createDelivery(recipientName: string): Observable<Delivery> {
    const body = { recipient: recipientName };

    return this.http.post<Delivery>(this.apiUrl, body);
  }

  updateDeliveryStatus(code: string, newStatus: string): Observable<Delivery> {
    const body = { status: newStatus };

    return this.http.patch<Delivery>(`${this.apiUrl}/${encodeURIComponent(code)}/status`, body);
  }
}

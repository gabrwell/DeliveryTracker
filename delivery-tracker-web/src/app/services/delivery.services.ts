import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Delivery } from '../delivery.model'; 
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DeliveryService {
  
  private readonly apiUrl = 'http://localhost:8080/deliveries'; 

  constructor(private http: HttpClient) { }

  getDeliveryByCode(code: string): Observable<Delivery> {
    return this.http.get<Delivery>(`${this.apiUrl}/${code}`);
  }

 createDelivery(recipientName: string): Observable<any> {
    const body = { recipient: recipientName };
    
    return this.http.post<any>(this.apiUrl, body);
  }

  updateDeliveryStatus(code: string, newStatus: string): Observable<any> {
    const body = { status: newStatus }; 
    
    return this.http.patch<any>(`${this.apiUrl}/${code}/status`, body);
  }
}
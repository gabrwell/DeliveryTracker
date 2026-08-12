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

  createDelivery(deliveryData: Delivery): Observable<Delivery> {
    
    return this.http.post<Delivery>(this.apiUrl, deliveryData);
  }
}
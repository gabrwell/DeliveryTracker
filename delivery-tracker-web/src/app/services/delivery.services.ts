import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Delivery } from '../delivery.model'; 
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DeliveryService {
  
  // A URL base onde o seu Back-end está escutando
  private readonly apiUrl = 'http://localhost:8080/deliveries'; 

  constructor(private http: HttpClient) { }

  // Método que vai fazer a requisição GET para o banco de dados
  getDeliveryByCode(code: string): Observable<Delivery> {
    return this.http.get<Delivery>(`${this.apiUrl}/${code}`);
  }
}
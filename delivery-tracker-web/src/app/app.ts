import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';

import { DeliveryService } from './services/delivery.services'; 

@Component({
  selector: 'app-root',
  imports: [
    RouterOutlet,
    MatCardModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatIconModule,
    FormsModule
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('delivery-tracker-web');
  
  trackingCode = '';

  deliveryResult: any = null; 

  constructor(private deliveryService: DeliveryService) {}

  searchDelivery() {
    if (!this.trackingCode) {
      return; 
    }

    this.deliveryService.getDeliveryByCode(this.trackingCode).subscribe({
      next: (dadosDaApi: any) => {
        this.deliveryResult = dadosDaApi;
        console.log('Sucesso! Dados vindos do Java:', this.deliveryResult);
      },
      error: (erro: any) => {
        console.error('Erro na requisição:', erro);
        this.deliveryResult = null;
      }
    });
  }
}
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

  newRecipientName = ''; 

  createNewDelivery() {
    if (!this.newRecipientName) {
      alert('Por favor, digite o nome do destinatário!');
      return;
    }

    this.deliveryService.createDelivery({
      recipientName: this.newRecipientName,
      trackingCode: '',
      status: '',
      lastUpdated: ''
    }).subscribe({
      next: (dadosDaApi: any) => {
        alert('Entrega cadastrada com sucesso! Código gerado: ' + dadosDaApi.trackingCode);
        console.log('Nova entrega salva no banco:', dadosDaApi);
        this.newRecipientName = ''; // Limpa o campo após o sucesso
      },
      error: (erro: any) => {
        console.error('Erro ao cadastrar entrega:', erro);
        alert('Ocorreu um erro ao tentar cadastrar.');
      }
    });
  }

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
        console.error('Error:', erro);
        this.deliveryResult = null;
      }
    });
  }
}
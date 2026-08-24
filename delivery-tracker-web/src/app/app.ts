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

  constructor(private deliveryService: DeliveryService) {}

  
  searchDelivery() {
    if (!this.trackingCode) {
      return; 
    }

    

    this.deliveryService.getDeliveryByCode(this.trackingCode).subscribe({
      next: (apiData: any) => {
        this.deliveryResult = apiData;
        console.log('Success! Data from Java:', this.deliveryResult);
      },
      error: (error: any) => {
        console.error('Error:', error);
        this.deliveryResult = null;
      }
    });
  }

  createNewDelivery() {
    if (!this.newRecipientName) {
      alert('Please enter the recipient name!');
      return;
    }

    this.deliveryService.createDelivery(this.newRecipientName).subscribe({
      next: (apiData: any) => {
        alert('Delivery registered successfully! Generated code: ' + apiData.trackingCode);
        console.log('New delivery saved in the database:', apiData);
        this.newRecipientName = ''; 
      },
      error: (error: any) => {
        console.error('Error registering delivery:', error);
        alert('An error occurred while trying to register.');
      }
    });
  }

  updateStatus(newStatus: string) {
    if (!this.deliveryResult) return;

    this.deliveryService.updateDeliveryStatus(this.deliveryResult.trackingCode, newStatus).subscribe({
      next: (dadosAtualizados: any) => {
        alert(`Status updated to ${newStatus} successfully!`);
        this.deliveryResult = dadosAtualizados; 
      },
      error: (erro: any) => {
        console.error('Error updating status:', erro);
        alert('An error occurred while trying to update the status.');
      }
    });
  }
}
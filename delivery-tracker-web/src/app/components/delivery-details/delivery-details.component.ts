import { Component, input, output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { Delivery, DeliveryStatus } from '../../models/delivery.model';

@Component({
  selector: 'app-delivery-details',
  imports: [MatButtonModule, MatCardModule, MatProgressSpinnerModule],
  templateUrl: './delivery-details.component.html',
  styleUrl: './delivery-details.component.css',
})
export class DeliveryDetailsComponent {
  readonly delivery = input.required<Delivery>();
  readonly statusTransitionError = input('');
  readonly statusBeingUpdated = input<DeliveryStatus | null>(null);

  readonly statusUpdateRequested = output<DeliveryStatus>();

  requestStatusUpdate(status: DeliveryStatus): void {
    this.statusUpdateRequested.emit(status);
  }
}

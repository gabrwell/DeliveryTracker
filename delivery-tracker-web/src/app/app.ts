import { Component, signal } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { finalize } from 'rxjs';

import { DeliveryService } from './services/delivery.services';
import { NotificationService } from './services/notification.service';
import { isApiError } from './models/api.model';
import { Delivery, DeliveryStatus } from './models/delivery.model';

@Component({
  selector: 'app-root',
  imports: [
    MatCardModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    FormsModule,
  ],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('delivery-tracker-web');

  trackingCode = '';
  deliveryResult: Delivery | null = null;
  statusTransitionError = '';
  newRecipientName = '';

  readonly isSearching = signal(false);
  readonly isCreating = signal(false);
  readonly statusBeingUpdated = signal<DeliveryStatus | null>(null);

  constructor(
    private deliveryService: DeliveryService,
    private notificationService: NotificationService,
  ) {}

  searchDelivery() {
    if (!this.trackingCode.trim()) {
      this.showNotification('Please enter a tracking code.');
      return;
    }

    this.statusTransitionError = '';
    this.deliveryResult = null;
    this.isSearching.set(true);

    this.deliveryService
      .getDeliveryByCode(this.trackingCode)
      .pipe(finalize(() => this.isSearching.set(false)))
      .subscribe({
        next: (apiData) => {
          this.deliveryResult = apiData;
        },
        error: (error: unknown) => {
          this.showError(error, 'An error occurred while searching for the delivery.');
        },
      });
  }

  createNewDelivery() {
    const recipientName = this.newRecipientName.trim();

    if (!recipientName) {
      this.showNotification('Please enter the recipient name.');
      return;
    }

    this.isCreating.set(true);

    this.deliveryService
      .createDelivery(recipientName)
      .pipe(finalize(() => this.isCreating.set(false)))
      .subscribe({
        next: (apiData) => {
          this.showNotification(
            `Delivery registered successfully! Generated code: ${apiData.trackingCode}`,
          );
          this.newRecipientName = '';
        },
        error: (error: unknown) => {
          this.showError(error, 'An error occurred while trying to register the delivery.');
        },
      });
  }

  updateStatus(newStatus: DeliveryStatus) {
    if (!this.deliveryResult) return;

    this.statusTransitionError = '';
    this.statusBeingUpdated.set(newStatus);

    this.deliveryService
      .updateDeliveryStatus(this.deliveryResult.trackingCode, newStatus)
      .pipe(finalize(() => this.statusBeingUpdated.set(null)))
      .subscribe({
        next: (updatedDelivery) => {
          this.showNotification(`Status updated to ${newStatus} successfully!`);
          this.deliveryResult = updatedDelivery;
        },
        error: (error: unknown) => {
          if (error instanceof HttpErrorResponse && error.status === 409) {
            this.statusTransitionError = this.getErrorMessage(
              error,
              'This status transition is not allowed.',
            );
            return;
          }

          this.showError(error, 'An error occurred while trying to update the status.');
        },
      });
  }

  private showError(error: unknown, fallbackMessage: string): void {
    this.showNotification(this.getErrorMessage(error, fallbackMessage));
  }

  private showNotification(message: string): void {
    this.notificationService.show(message);
  }

  private getErrorMessage(error: unknown, fallbackMessage: string): string {
    if (!(error instanceof HttpErrorResponse)) {
      return fallbackMessage;
    }

    if (error.status === 0) {
      return 'Unable to connect to the server. Please try again.';
    }

    if (isApiError(error.error)) {
      return error.error.message;
    }

    return fallbackMessage;
  }
}

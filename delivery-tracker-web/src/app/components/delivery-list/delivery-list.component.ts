import { DatePipe } from '@angular/common';
import { Component, OnInit, output, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { finalize } from 'rxjs';

import { Delivery } from '../../models/delivery.model';
import { DeliveryService } from '../../services/delivery.services';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-delivery-list',
  imports: [
    DatePipe,
    MatButtonModule,
    MatCardModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatTableModule,
  ],
  templateUrl: './delivery-list.component.html',
  styleUrl: './delivery-list.component.css',
})
export class DeliveryListComponent implements OnInit {
  readonly deliverySelected = output<string>();
  readonly displayedColumns = ['trackingCode', 'recipient', 'status', 'createdAt', 'actions'];
  readonly pageSizeOptions = [5, 10, 25];
  readonly isLoading = signal(false);
  readonly hasLoadError = signal(false);

  deliveries: readonly Delivery[] = [];
  totalElements = 0;
  pageIndex = 0;
  pageSize = 10;

  constructor(
    private readonly deliveryService: DeliveryService,
    private readonly notificationService: NotificationService,
  ) {}

  ngOnInit(): void {
    this.loadDeliveries();
  }

  loadDeliveries(): void {
    this.isLoading.set(true);
    this.hasLoadError.set(false);

    this.deliveryService
      .getAllDeliveries(this.pageIndex, this.pageSize)
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        next: (page) => {
          this.deliveries = page.content;
          this.totalElements = page.totalElements;
          this.pageIndex = page.number;
          this.pageSize = page.size;
        },
        error: () => {
          this.deliveries = [];
          this.totalElements = 0;
          this.hasLoadError.set(true);
          this.notificationService.show('Unable to load deliveries. Please try again.');
        },
      });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadDeliveries();
  }

  selectDelivery(trackingCode: string): void {
    this.deliverySelected.emit(trackingCode);
  }
}

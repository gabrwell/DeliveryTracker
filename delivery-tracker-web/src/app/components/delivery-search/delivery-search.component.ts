import { Component, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-delivery-search',
  imports: [
    FormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './delivery-search.component.html',
  styleUrl: './delivery-search.component.css',
})
export class DeliverySearchComponent {
  readonly trackingCode = input.required<string>();
  readonly isSearching = input(false);

  readonly trackingCodeChange = output<string>();
  readonly searchRequested = output<string>();

  updateTrackingCode(value: string): void {
    this.trackingCodeChange.emit(value);
  }

  requestSearch(): void {
    this.searchRequested.emit(this.trackingCode());
  }
}

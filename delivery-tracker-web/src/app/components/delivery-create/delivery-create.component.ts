import { Component, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-delivery-create',
  imports: [
    FormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './delivery-create.component.html',
  styleUrl: './delivery-create.component.css',
})
export class DeliveryCreateComponent {
  readonly recipientName = input.required<string>();
  readonly isCreating = input(false);

  readonly recipientNameChange = output<string>();
  readonly createRequested = output<string>();

  updateRecipientName(value: string): void {
    this.recipientNameChange.emit(value);
  }

  requestCreation(): void {
    this.createRequested.emit(this.recipientName());
  }
}

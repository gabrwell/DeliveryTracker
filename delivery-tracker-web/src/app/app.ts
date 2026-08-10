import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';

  import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';

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


searchDelivery() {
    alert('The button is working! You searched for: ' + this.trackingCode);
    console.log('Code captured in TypeScript:', this.trackingCode);
  }
}


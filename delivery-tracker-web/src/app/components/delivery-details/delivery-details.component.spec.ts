import { TestBed } from '@angular/core/testing';

import { Delivery, DeliveryStatus } from '../../models/delivery.model';
import { DeliveryDetailsComponent } from './delivery-details.component';

describe('DeliveryDetailsComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeliveryDetailsComponent],
    }).compileComponents();
  });

  it('should emit the selected status', () => {
    const fixture = TestBed.createComponent(DeliveryDetailsComponent);
    const statusUpdateRequested = vi.fn();
    fixture.componentRef.setInput('delivery', deliveryWithStatus('CREATED'));
    fixture.componentInstance.statusUpdateRequested.subscribe(statusUpdateRequested);

    fixture.componentInstance.requestStatusUpdate('IN_TRANSIT');

    expect(statusUpdateRequested).toHaveBeenCalledWith('IN_TRANSIT');
  });

  it.each<DeliveryStatus>(['DELIVERED', 'CANCELED'])(
    'should hide status actions when the delivery is %s',
    (status) => {
      const fixture = TestBed.createComponent(DeliveryDetailsComponent);
      fixture.componentRef.setInput('delivery', deliveryWithStatus(status));

      fixture.detectChanges();

      expect(fixture.nativeElement.querySelector('mat-card-actions')).toBeNull();
      expect(fixture.nativeElement.textContent).toContain(
        'This delivery has reached a final status.',
      );
    },
  );
});

function deliveryWithStatus(status: DeliveryStatus): Delivery {
  return {
    trackingCode: 'ABC123',
    recipient: 'GABRIEL',
    status,
    createdAt: null,
    updatedAt: null,
    deliveredAt: status === 'DELIVERED' ? '2026-09-09T10:00:00' : null,
    returnDeadline: null,
  };
}

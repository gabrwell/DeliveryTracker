import { TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';

import { Delivery } from '../../models/delivery.model';
import { PageResponse } from '../../models/page.model';
import { DeliveryService } from '../../services/delivery.services';
import { NotificationService } from '../../services/notification.service';
import { DeliveryListComponent } from './delivery-list.component';

describe('DeliveryListComponent', () => {
  let deliveryServiceMock: { getAllDeliveries: ReturnType<typeof vi.fn> };
  let notificationServiceMock: { show: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    deliveryServiceMock = { getAllDeliveries: vi.fn().mockReturnValue(of(pageResponse())) };
    notificationServiceMock = { show: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [DeliveryListComponent],
      providers: [
        { provide: DeliveryService, useValue: deliveryServiceMock },
        { provide: NotificationService, useValue: notificationServiceMock },
      ],
    }).compileComponents();
  });

  it('should load and render the first delivery page', () => {
    const fixture = TestBed.createComponent(DeliveryListComponent);

    fixture.detectChanges();

    expect(deliveryServiceMock.getAllDeliveries).toHaveBeenCalledWith(0, 10);
    expect(fixture.nativeElement.textContent).toContain('ABC123');
    expect(fixture.nativeElement.textContent).toContain('Gabriel');
  });

  it('should request a new page when the paginator changes', () => {
    const fixture = TestBed.createComponent(DeliveryListComponent);
    fixture.detectChanges();
    deliveryServiceMock.getAllDeliveries.mockClear();

    fixture.componentInstance.onPageChange({
      pageIndex: 2,
      previousPageIndex: 1,
      pageSize: 5,
      length: 30,
    });

    expect(deliveryServiceMock.getAllDeliveries).toHaveBeenCalledWith(2, 5);
  });

  it('should emit the selected tracking code', () => {
    const fixture = TestBed.createComponent(DeliveryListComponent);
    const selected = vi.fn();
    fixture.componentInstance.deliverySelected.subscribe(selected);

    fixture.componentInstance.selectDelivery('ABC123');

    expect(selected).toHaveBeenCalledWith('ABC123');
  });

  it('should show feedback when loading fails', () => {
    deliveryServiceMock.getAllDeliveries.mockReturnValue(
      throwError(() => new Error('Request failed')),
    );
    const fixture = TestBed.createComponent(DeliveryListComponent);

    fixture.detectChanges();

    expect(fixture.componentInstance.hasLoadError()).toBe(true);
    expect(notificationServiceMock.show).toHaveBeenCalledWith(
      'Unable to load deliveries. Please try again.',
    );
  });
});

function pageResponse(): PageResponse<Delivery> {
  const sort = { empty: true, sorted: false, unsorted: true };

  return {
    content: [
      {
        trackingCode: 'ABC123',
        recipient: 'Gabriel',
        status: 'CREATED',
        createdAt: '2026-09-11T10:00:00',
        updatedAt: null,
        deliveredAt: null,
        returnDeadline: null,
      },
    ],
    empty: false,
    first: true,
    last: true,
    number: 0,
    numberOfElements: 1,
    pageable: {
      offset: 0,
      pageNumber: 0,
      pageSize: 10,
      paged: true,
      unpaged: false,
      sort,
    },
    size: 10,
    sort,
    totalElements: 1,
    totalPages: 1,
  };
}

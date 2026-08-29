import { TestBed } from '@angular/core/testing';
import { HttpErrorResponse } from '@angular/common/http';
import { throwError } from 'rxjs';
import { App } from './app';
import { Delivery, DeliveryStatus } from './delivery.model';
import { DeliveryService } from './services/delivery.services';

describe('App', () => {
  let deliveryServiceMock: {
    getDeliveryByCode: ReturnType<typeof vi.fn>;
    createDelivery: ReturnType<typeof vi.fn>;
    updateDeliveryStatus: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    deliveryServiceMock = {
      getDeliveryByCode: vi.fn(),
      createDelivery: vi.fn(),
      updateDeliveryStatus: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [App],
      providers: [{ provide: DeliveryService, useValue: deliveryServiceMock }],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should render title', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h1')?.textContent).toContain('Delivery Tracker');
  });

  it('should show in-transit and cancel actions for a created delivery', () => {
    const fixture = TestBed.createComponent(App);
    fixture.componentInstance.deliveryResult = deliveryWithStatus('CREATED');

    fixture.detectChanges();

    expect(actionLabels(fixture.nativeElement)).toEqual(['In Transit', 'Cancel']);
  });

  it('should show delivered and cancel actions for an in-transit delivery', () => {
    const fixture = TestBed.createComponent(App);
    fixture.componentInstance.deliveryResult = deliveryWithStatus('IN_TRANSIT');

    fixture.detectChanges();

    expect(actionLabels(fixture.nativeElement)).toEqual(['Delivered', 'Cancel']);
  });

  it.each<DeliveryStatus>(['DELIVERED', 'CANCELED'])(
    'should not show actions when delivery status is %s',
    (status) => {
      const fixture = TestBed.createComponent(App);
      fixture.componentInstance.deliveryResult = deliveryWithStatus(status);

      fixture.detectChanges();

      expect(actionLabels(fixture.nativeElement)).toEqual([]);
      expect(fixture.nativeElement.textContent).toContain(
        'This delivery has reached a final status.',
      );
    },
  );

  it('should display the backend message when a status transition returns conflict', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    app.deliveryResult = deliveryWithStatus('CREATED');
    deliveryServiceMock.updateDeliveryStatus.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 409,
            error: {
              message: 'Cannot change delivery status from CREATED to DELIVERED.',
            },
          }),
      ),
    );

    app.updateStatus('DELIVERED');
    fixture.detectChanges();

    const errorMessage = fixture.nativeElement.querySelector('[role="alert"]') as HTMLElement;
    expect(errorMessage.textContent).toContain(
      'Cannot change delivery status from CREATED to DELIVERED.',
    );
  });
});

function deliveryWithStatus(status: DeliveryStatus): Delivery {
  return {
    trackingCode: 'ABC123',
    recipient: 'GABRIEL',
    status,
    createdAt: null,
    updatedAt: null,
    deliveredAt: status === 'DELIVERED' ? '2026-08-29T10:00:00' : null,
    returnDeadline: null,
  };
}

function actionLabels(element: HTMLElement): string[] {
  return Array.from(element.querySelectorAll('mat-card-actions button')).map(
    (button) => button.textContent?.trim() ?? '',
  );
}

import { TestBed } from '@angular/core/testing';
import { HttpErrorResponse } from '@angular/common/http';
import { Subject, throwError } from 'rxjs';
import { App } from './app';
import { Delivery, DeliveryStatus } from './models/delivery.model';
import { DeliveryService } from './services/delivery.services';
import { NotificationService } from './services/notification.service';

describe('App', () => {
  let deliveryServiceMock: {
    getDeliveryByCode: ReturnType<typeof vi.fn>;
    createDelivery: ReturnType<typeof vi.fn>;
    updateDeliveryStatus: ReturnType<typeof vi.fn>;
  };
  let notificationServiceMock: {
    show: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    deliveryServiceMock = {
      getDeliveryByCode: vi.fn(),
      createDelivery: vi.fn(),
      updateDeliveryStatus: vi.fn(),
    };
    notificationServiceMock = {
      show: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        { provide: DeliveryService, useValue: deliveryServiceMock },
        { provide: NotificationService, useValue: notificationServiceMock },
      ],
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

  it('should keep the search action disabled while the request is pending', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    const request = new Subject<Delivery>();
    app.trackingCode = 'ABC123';
    deliveryServiceMock.getDeliveryByCode.mockReturnValue(request.asObservable());

    app.searchDelivery();
    fixture.detectChanges();

    const searchButton = fixture.nativeElement.querySelector(
      '[aria-label="Search delivery"]',
    ) as HTMLButtonElement;
    expect(app.isSearching()).toBe(true);
    expect(searchButton.disabled).toBe(true);

    request.next(deliveryWithStatus('CREATED'));
    request.complete();
    fixture.detectChanges();

    expect(app.isSearching()).toBe(false);
    expect(app.deliveryResult?.trackingCode).toBe('ABC123');
    expect(searchButton.disabled).toBe(false);
  });

  it('should register a trimmed recipient and show a success snackbar', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    const request = new Subject<Delivery>();
    app.newRecipientName = '  Gabriel  ';
    deliveryServiceMock.createDelivery.mockReturnValue(request.asObservable());

    app.createNewDelivery();

    expect(app.isCreating()).toBe(true);
    expect(deliveryServiceMock.createDelivery).toHaveBeenCalledWith('Gabriel');

    request.next(deliveryWithStatus('CREATED'));
    request.complete();

    expect(app.isCreating()).toBe(false);
    expect(app.newRecipientName).toBe('');
    expect(notificationServiceMock.show).toHaveBeenCalledWith(
      'Delivery registered successfully! Generated code: ABC123',
    );
  });

  it('should show the backend message when searching fails', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    app.trackingCode = 'UNKNOWN';
    deliveryServiceMock.getDeliveryByCode.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 404,
            error: {
              timestamp: '2026-08-29T10:00:00',
              status: 404,
              message: 'Delivery not found.',
            },
          }),
      ),
    );

    app.searchDelivery();

    expect(app.isSearching()).toBe(false);
    expect(app.deliveryResult).toBeNull();
    expect(notificationServiceMock.show).toHaveBeenCalledWith('Delivery not found.');
  });

  it('should show a connection message when the backend is unavailable', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    app.newRecipientName = 'Gabriel';
    deliveryServiceMock.createDelivery.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 0 })),
    );

    app.createNewDelivery();

    expect(app.isCreating()).toBe(false);
    expect(notificationServiceMock.show).toHaveBeenCalledWith(
      'Unable to connect to the server. Please try again.',
    );
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
              timestamp: '2026-08-29T10:00:00',
              status: 409,
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
    expect(app.statusBeingUpdated()).toBeNull();
    expect(notificationServiceMock.show).not.toHaveBeenCalled();
  });

  it('should disable every status action while an update is pending', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    const request = new Subject<Delivery>();
    app.deliveryResult = deliveryWithStatus('CREATED');
    deliveryServiceMock.updateDeliveryStatus.mockReturnValue(request.asObservable());

    app.updateStatus('IN_TRANSIT');
    fixture.detectChanges();

    const actionButtons = Array.from(
      fixture.nativeElement.querySelectorAll('mat-card-actions button'),
    ) as HTMLButtonElement[];
    expect(app.statusBeingUpdated()).toBe('IN_TRANSIT');
    expect(actionButtons.every((button) => button.disabled)).toBe(true);

    request.next(deliveryWithStatus('IN_TRANSIT'));
    request.complete();
    fixture.detectChanges();

    expect(app.statusBeingUpdated()).toBeNull();
    expect(app.deliveryResult.status).toBe('IN_TRANSIT');
    expect(notificationServiceMock.show).toHaveBeenCalledWith(
      'Status updated to IN_TRANSIT successfully!',
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

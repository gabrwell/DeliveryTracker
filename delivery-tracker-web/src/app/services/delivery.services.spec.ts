import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { environment } from '../../environments/environment';
import { Delivery } from '../models/delivery.model';
import { PageResponse } from '../models/page.model';
import { DeliveryService } from './delivery.services';

describe('DeliveryService', () => {
  let service: DeliveryService;
  let httpTesting: HttpTestingController;

  const apiUrl = `${environment.apiUrl}/deliveries`;
  const delivery: Delivery = {
    trackingCode: 'ABC123',
    recipient: 'GABRIEL',
    status: 'CREATED',
    createdAt: '2026-08-29T10:00:00',
    updatedAt: '2026-08-29T10:00:00',
    deliveredAt: null,
    returnDeadline: null,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [DeliveryService, provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(DeliveryService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should normalize and encode a tracking code when searching', () => {
    service.getDeliveryByCode(' ab 123 ').subscribe();

    const request = httpTesting.expectOne(`${apiUrl}/AB%20123`);
    expect(request.request.method).toBe('GET');
    request.flush(delivery);
  });

  it('should send a typed request when creating a delivery', () => {
    service.createDelivery('GABRIEL').subscribe();

    const request = httpTesting.expectOne(apiUrl);
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ recipient: 'GABRIEL' });
    request.flush(delivery);
  });

  it('should send a typed request when updating delivery status', () => {
    service.updateDeliveryStatus(' abc123 ', 'IN_TRANSIT').subscribe();

    const request = httpTesting.expectOne(`${apiUrl}/ABC123/status`);
    expect(request.request.method).toBe('PATCH');
    expect(request.request.body).toEqual({ status: 'IN_TRANSIT' });
    request.flush({ ...delivery, status: 'IN_TRANSIT' });
  });

  it('should request a typed page of deliveries', () => {
    const emptyPage: PageResponse<Delivery> = {
      content: [],
      empty: true,
      first: true,
      last: true,
      number: 0,
      numberOfElements: 0,
      pageable: {
        offset: 0,
        pageNumber: 0,
        pageSize: 10,
        paged: true,
        unpaged: false,
        sort: { empty: false, sorted: true, unsorted: false },
      },
      size: 10,
      sort: { empty: false, sorted: true, unsorted: false },
      totalElements: 0,
      totalPages: 0,
    };

    service.getAllDeliveries(0, 10).subscribe((response) => {
      expect(response).toEqual(emptyPage);
    });

    const request = httpTesting.expectOne(
      (candidate) =>
        candidate.url === apiUrl &&
        candidate.params.get('page') === '0' &&
        candidate.params.get('size') === '10',
    );
    expect(request.request.method).toBe('GET');
    request.flush(emptyPage);
  });
});

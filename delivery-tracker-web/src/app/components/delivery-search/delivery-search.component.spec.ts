import { TestBed } from '@angular/core/testing';

import { DeliverySearchComponent } from './delivery-search.component';

describe('DeliverySearchComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeliverySearchComponent],
    }).compileComponents();
  });

  it('should emit the tracking code when a search is requested', () => {
    const fixture = TestBed.createComponent(DeliverySearchComponent);
    const searchRequested = vi.fn();
    fixture.componentRef.setInput('trackingCode', 'ABC123');
    fixture.componentInstance.searchRequested.subscribe(searchRequested);

    fixture.componentInstance.requestSearch();

    expect(searchRequested).toHaveBeenCalledWith('ABC123');
  });

  it('should emit changes made to the tracking code', () => {
    const fixture = TestBed.createComponent(DeliverySearchComponent);
    const trackingCodeChange = vi.fn();
    fixture.componentInstance.trackingCodeChange.subscribe(trackingCodeChange);

    fixture.componentInstance.updateTrackingCode('NEW-CODE');

    expect(trackingCodeChange).toHaveBeenCalledWith('NEW-CODE');
  });
});

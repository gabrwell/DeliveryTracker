import { TestBed } from '@angular/core/testing';

import { DeliveryCreateComponent } from './delivery-create.component';

describe('DeliveryCreateComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeliveryCreateComponent],
    }).compileComponents();
  });

  it('should emit the recipient when creation is requested', () => {
    const fixture = TestBed.createComponent(DeliveryCreateComponent);
    const createRequested = vi.fn();
    fixture.componentRef.setInput('recipientName', 'Gabriel');
    fixture.componentInstance.createRequested.subscribe(createRequested);

    fixture.componentInstance.requestCreation();

    expect(createRequested).toHaveBeenCalledWith('Gabriel');
  });

  it('should emit changes made to the recipient name', () => {
    const fixture = TestBed.createComponent(DeliveryCreateComponent);
    const recipientNameChange = vi.fn();
    fixture.componentInstance.recipientNameChange.subscribe(recipientNameChange);

    fixture.componentInstance.updateRecipientName('Ana');

    expect(recipientNameChange).toHaveBeenCalledWith('Ana');
  });
});

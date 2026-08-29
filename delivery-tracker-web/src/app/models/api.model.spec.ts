import { isApiError } from './api.model';

describe('isApiError', () => {
  it('should accept the error contract returned by the backend', () => {
    expect(
      isApiError({
        timestamp: '2026-08-29T10:00:00',
        status: 409,
        message: 'Invalid transition.',
      }),
    ).toBe(true);
  });

  it('should reject incomplete response bodies', () => {
    expect(isApiError({ message: 'Invalid transition.' })).toBe(false);
  });
});

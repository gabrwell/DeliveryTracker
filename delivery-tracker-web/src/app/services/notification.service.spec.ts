import { MatSnackBar } from '@angular/material/snack-bar';
import { NotificationService } from './notification.service';

describe('NotificationService', () => {
  it('should display a snackbar with the standard configuration', () => {
    const snackBar = {
      open: vi.fn(),
    };
    const service = new NotificationService(snackBar as unknown as MatSnackBar);

    service.show('Operation completed.');

    expect(snackBar.open).toHaveBeenCalledWith('Operation completed.', 'Close', {
      duration: 5000,
      horizontalPosition: 'center',
      verticalPosition: 'bottom',
    });
  });
});

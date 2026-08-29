export interface ApiLink {
  readonly href: string;
}

export type ApiLinks = Readonly<Record<string, ApiLink>>;

export interface ApiError {
  readonly timestamp: string;
  readonly status: number;
  readonly message: string;
}

export function isApiError(value: unknown): value is ApiError {
  if (typeof value !== 'object' || value === null) {
    return false;
  }

  const candidate = value as Partial<ApiError>;

  return (
    typeof candidate.timestamp === 'string' &&
    typeof candidate.status === 'number' &&
    typeof candidate.message === 'string'
  );
}

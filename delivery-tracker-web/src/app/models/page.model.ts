export interface SortState {
  readonly empty: boolean;
  readonly sorted: boolean;
  readonly unsorted: boolean;
}

export interface PageableState {
  readonly offset: number;
  readonly pageNumber: number;
  readonly pageSize: number;
  readonly paged: boolean;
  readonly unpaged: boolean;
  readonly sort: SortState;
}

export interface PageResponse<T> {
  readonly content: readonly T[];
  readonly empty: boolean;
  readonly first: boolean;
  readonly last: boolean;
  readonly number: number;
  readonly numberOfElements: number;
  readonly pageable: PageableState;
  readonly size: number;
  readonly sort: SortState;
  readonly totalElements: number;
  readonly totalPages: number;
}

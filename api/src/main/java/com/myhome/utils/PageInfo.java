package com.myhome.utils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * is a data structure that represents the current page, page limit, total pages, and
 * total elements of a paginated dataset. It provides a convenient way to aggregate
 * these values into a single object for easier manipulation and analysis.
 */
@EqualsAndHashCode
@ToString
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PageInfo {
  private final int currentPage;
  private final int pageLimit;
  private final int totalPages;
  private final long totalElements;

  /**
   * generates a `PageInfo` object containing information about the number of pages,
   * page size, total pages, and total elements for a given `Pageable` and `Page`.
   * 
   * @param pageable pagination information for the result set, providing the number
   * of pages and the number of elements on each page.
   * 
   * 	- `getPageNumber(): int`: The page number of the current page being processed.
   * 	- `getPageSize(): int`: The number of elements per page in the current page.
   * 	- `getTotalPages(): int`: The total number of pages in the dataset.
   * 	- `getTotalElements()`: The total number of elements in the dataset.
   * 
   * @param page current page being processed, providing the total number of elements
   * on that page.
   * 
   * 	- `pageNumber`: The page number of the response.
   * 	- `pageSize`: The number of elements in each page of the response.
   * 	- `totalPages`: The total number of pages in the response.
   * 	- `totalElements`: The total number of elements returned in the response.
   * 
   * @returns a `PageInfo` object containing pagination metadata.
   * 
   * 	- The first element is pageNumber, which represents the current page number being
   * displayed.
   * 	- pageSize is the number of elements that can be displayed on each page.
   * 	- totalPages is the total number of pages in the collection.
   * 	- totalElements is the total number of elements in the collection.
   */
  public static PageInfo of(Pageable pageable, Page<?> page) {
    return new PageInfo(
        pageable.getPageNumber(),
        pageable.getPageSize(),
        page.getTotalPages(),
        page.getTotalElements()
    );
  }
}

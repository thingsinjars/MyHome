package com.myhome.utils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * is a data structure for representing page information in a paginated dataset. It
 * contains four fields: currentPage, pageLimit, totalPages, and totalElements. The
 * class provides a constructor for creating instances of the class and a static
 * method for generating instances from a Pageable object and a Page object.
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
   * generates a `PageInfo` object containing information about a pageable and its
   * associated page, including the page number, size, total pages, and total elements.
   * 
   * @param pageable pageable object containing information about the current page of
   * data being processed.
   * 
   * The returned object is `PageInfo`, which includes four attributes:
   * 
   * 1/ `pageNumber`: The number of the current page being displayed.
   * 2/ `pageSize`: The number of elements in a single page.
   * 3/ `totalPages`: The total number of pages in the result set.
   * 4/ `totalElements`: The total number of elements in the result set.
   * 
   * @param page current page being processed, providing information on its position
   * within the overall set of pages and the total number of elements in the dataset.
   * 
   * 	- `pageNumber`: The page number of the current page being processed.
   * 	- `pageSize`: The size of each page of elements returned in the response.
   * 	- `totalPages`: The total number of pages in the result set.
   * 	- `totalElements`: The total number of elements returned by the query.
   * 
   * @returns a `PageInfo` object containing information about the page of elements.
   * 
   * 	- The page number (0-based) represents the position of the page in the paginated
   * sequence.
   * 	- Page size refers to the number of elements displayed per page.
   * 	- Total pages indicate the total number of pages in the paginated sequence.
   * 	- Total elements represent the total number of elements in the paginated sequence.
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

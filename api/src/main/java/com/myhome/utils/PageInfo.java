package com.myhome.utils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * is a data structure that represents a page of results in a larger dataset. It
 * provides information on the current page being displayed, the total number of pages
 * available, and the total number of elements in the dataset. The class also includes
 * a static method for creating a new PageInfo instance from a Pageable and a Page object.
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
   * creates a `PageInfo` object containing page-related information, such as the current
   * page number, size, total pages, and total elements, based on provided `Pageable`
   * and `Page` objects.
   * 
   * @param pageable pagination state, providing the current page number and page size
   * for generating the page of data.
   * 
   * 	- `getPageNumber()` represents the current page number.
   * 	- `getPageSize()` indicates the number of elements per page.
   * 	- `getTotalPages()` displays the total number of pages in the collection.
   * 	- `getTotalElements()` shows the overall number of elements in the collection.
   * 
   * @param page current page being processed, providing the total number of elements
   * on that page.
   * 
   * 	- `pageNumber`: The number of the page being returned.
   * 	- `pageSize`: The size of the page being returned.
   * 	- `totalPages`: The total number of pages in the result set.
   * 	- `totalElements`: The total number of elements in the result set.
   * 
   * @returns a `PageInfo` object containing page number, size, total pages, and total
   * elements.
   * 
   * 	- `pageNumber`: The number of the page being accessed.
   * 	- `pageSize`: The size of each page being accessed.
   * 	- `totalPages`: The total number of pages in the collection.
   * 	- `totalElements`: The total number of elements in the collection.
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

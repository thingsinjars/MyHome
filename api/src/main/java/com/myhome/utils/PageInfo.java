package com.myhome.utils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * is a data structure that represents page-related information in Spring Data. It
 * contains fields for the current page number, page limit, total pages, and total
 * elements. The class also includes a static method for creating new instances of
 * the class based on a Pageable object and a Page object.
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
   * takes a `pageable` and a `page` as input and returns a `PageInfo` object containing
   * information about the page number, page size, total pages, and total elements.
   * 
   * @param pageable pagination information for the current page of data, providing the
   * page number, page size, total pages, and total elements.
   * 
   * 	- `getPageNumber()` returns the current page number.
   * 	- `getPageSize()` returns the number of elements per page.
   * 	- `getTotalPages()` returns the total number of pages in the result set.
   * 	- `getTotalElements()` returns the total number of elements in the result set.
   * 
   * @param page current page being processed, providing information on its position
   * and size within the overall paginated result.
   * 
   * 	- `pageNumber`: The current page number being rendered.
   * 	- `pageSize`: The total number of elements in the page being rendered.
   * 	- `totalPages`: The total number of pages in the result set.
   * 	- `totalElements`: The total number of elements in the result set.
   * 
   * @returns a `PageInfo` object containing information about the current page of data.
   * 
   * 	- The page number is represented as an integer variable representing the current
   * page being displayed by the user.
   * 	- The page size represents the total number of elements that can be displayed on
   * a single page.
   * 	- Total pages represent the total number of pages available in the data set.
   * 	- Total elements represent the total number of elements present in the entire dataset.
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

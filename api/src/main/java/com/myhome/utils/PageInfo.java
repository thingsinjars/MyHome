package com.myhome.utils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Encapsulates information about a page of data, including the current page number,
 * limit, total pages, and total elements. It provides a convenient way to retrieve
 * this information in a single object, allowing for easier manipulation and analysis
 * of large datasets.
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
   * Generates a `PageInfo` object containing information about the number of pages,
   * page size, total pages, and total elements for a given `Pageable` and `Page`.
   * 
   * @param pageable Pageable interface, which provides methods for retrieving a page
   * of elements from a source.
   * 
   * @param page current page of data being processed, providing the total number of
   * elements on that page.
   * 
   * @returns a `PageInfo` object containing various pagination-related metrics.
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

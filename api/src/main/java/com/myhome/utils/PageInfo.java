package com.myhome.utils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Represents a page of data in a paged result set. It is generated based on the
 * provided Pageable and Page objects. The class provides a way to encapsulate page
 * information for display or further processing purposes.
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
   * Initializes a new instance of the `PageInfo` class with four parameters: the current
   * page number, the page size, the total pages, and the total elements. These parameters
   * are derived from the provided `Pageable` object and the `Page` object.
   *
   * @param pageable pagination metadata, providing information about the current page
   * number and page size.
   *
   * @param page result of a pagination query, providing information about the total
   * pages and elements available.
   *
   * @returns a `PageInfo` object with page number, page size, total pages, and total
   * elements.
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

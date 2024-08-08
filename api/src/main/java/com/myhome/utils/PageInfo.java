package com.myhome.utils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Provides a means to encapsulate and manage pagination metadata. It is designed as
 * an immutable object with private constructor and a factory method of() for creating
 * instances based on Spring Pageable and Page objects. The class supports value
 * equality and provides a string representation of the page information.
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
   * Creates a new `PageInfo` object from provided parameters, including the current
   * page number, page size, total pages, and total elements from a given `page` and `Pageable`.
   *
   * @param pageable pagination information, providing the current page number and page
   * size for the given query result.
   *
   * @param page results of a query, providing information about the total number of
   * pages and elements that are being paginated.
   *
   * @returns a `PageInfo` object with specified parameters.
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

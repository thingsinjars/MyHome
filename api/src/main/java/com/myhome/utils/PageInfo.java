package com.myhome.utils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Represents a structured object for pagination information. It encapsulates essential
 * details such as current page number, total elements, and total pages from a Spring
 * Data Pageable and Page objects. The class provides a static method to create an
 * instance of PageInfo based on the provided pageable and page objects.
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
   * Creates a `PageInfo` object based on the given `Pageable` and `Page` parameters,
   * populating it with information about the current page number, page size, total
   * pages, and total elements.
   *
   * @param pageable pager's state, providing information about the current page number
   * and page size for pagination purposes.
   *
   * @param page result of a previous page request, providing access to total pages and
   * elements.
   *
   * @returns a `PageInfo` object with four attributes.
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

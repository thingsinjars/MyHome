package com.myhome.utils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Is designed to encapsulate pagination details. It provides a constructor that takes
 * page-related parameters and creates an instance of the class. The class is annotated
 * with various lombok annotations for easier development.
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
   * Creates a `PageInfo` object based on the given `Pageable` and `Page` objects. It
   * retrieves the current page number, page size, total pages, and total elements from
   * these objects and initializes the `PageInfo` with this information.
   *
   * @param pageable pagination information, which provides the current page number and
   * page size for processing the data.
   *
   * @param page result of pagination and provides access to the total number of pages
   * and elements, which are used to construct the `PageInfo` object.
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

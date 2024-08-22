package com.myhome.utils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Provides a utility class for managing pagination details. It encapsulates essential
 * page metadata such as current page number, total pages, and total elements. The
 * class offers a static factory method to create an instance based on Spring Data
 * Pageable and Page objects.
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
   * Initializes a `PageInfo` object based on input parameters. It takes two arguments:
   * `pageable`, which provides information about pagination, and `page`, which represents
   * a page of data. The object is populated with relevant pagination details such as
   * page number, page size, total pages, and total elements.
   *
   * @param pageable pagination configuration used to divide the data into pages,
   * providing information such as page number and page size.
   *
   * @param page object that encapsulates metadata about the current page, including
   * total number of pages and elements.
   *
   * @returns a `PageInfo` object with specified pagination details.
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

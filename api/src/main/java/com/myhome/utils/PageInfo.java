package com.myhome.utils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Represents an immutable object encapsulating pagination metadata. It is initialized
 * using static factory method of() that takes Pageable and Page objects as parameters.
 * This class is designed to provide a way to work with pagination information in a
 * concise manner.
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
   * Creates a new instance of the `PageInfo` class with four parameters: the current
   * page number, page size, total pages, and total elements, all obtained from the
   * provided `pageable` object and `page`.
   * 
   * @param pageable pagination information for retrieving data from a database or a
   * data storage system, providing the current page number and page size.
   * 
   * @param page result of a pagination query, providing information about the total
   * number of pages and elements.
   * 
   * @returns an instance of `PageInfo` with four attributes.
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

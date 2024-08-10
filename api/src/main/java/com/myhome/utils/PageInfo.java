package com.myhome.utils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Represents page information for data pagination in Spring Data. It provides a
 * constructor to create an instance from Pageable and Page objects. The class uses
 * Lombok annotations to enable automatic generation of equals, hashCode, and toString
 * methods.
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
   * Constructs a `PageInfo` object, which encapsulates information about pagination,
   * from provided `Pageable` and `Page` objects. It retrieves relevant data such as
   * page number, page size, total pages, and total elements. The resulting `PageInfo`
   * object is then returned.
   *
   * @param pageable pagination parameters, such as page number and page size, that are
   * used to determine the current page of data being queried.
   *
   * @param page result of pagination, providing information about the total number of
   * pages and elements.
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

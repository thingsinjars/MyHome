package com.myhome.security.filters;

import com.myhome.domain.User;
import com.myhome.services.CommunityService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Enforces authorization for community-related requests by checking if the authenticated
 * user is a community admin. It uses a regular expression to match community amenity
 * requests and verifies user permissions accordingly.
 */
public class CommunityAuthorizationFilter extends BasicAuthenticationFilter {
  private final CommunityService communityService;
  private static final String UUID_PATTERN =
      "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
  private static final Pattern ADD_AMENITY_REQUEST_PATTERN =
      Pattern.compile("/communities/" + UUID_PATTERN + "/amenities");

  public CommunityAuthorizationFilter(AuthenticationManager authenticationManager,
      CommunityService communityService) {
    super(authenticationManager);
    this.communityService = communityService;
  }

  /**
   * Checks if the current request URI matches a specific pattern and if the user is
   * not a community admin. If both conditions are met, it sets the response status to
   * 403 (Forbidden) and stops the filter chain. Otherwise, it proceeds with the filter
   * chain.
   *
   * @param request HTTP request being filtered, providing access to request details
   * such as the request URI.
   *
   * Extract the properties of the `request` object:
   * - `HttpServletRequest request` is a servlet request object.
   * - `HttpServletRequest` has properties such as `getRequestURI()`, `getMethod()`,
   * `getParameter()`, `getParameterNames()`, etc.
   * - The `request` object is used to get information from the HTTP request, such as
   * the request URI, method, parameters, etc.
   *
   * @param response HTTP response sent back to the client.
   *
   * Set its status to HttpServletResponse.SC_FORBIDDEN.
   *
   * @param chain sequence of filters that will be executed after the current filter
   * has completed its processing.
   *
   * Pass `chain` as an object of type `FilterChain`.
   * Its main properties are:
   * - `doFilter` method, which is the entry point for the filter chain.
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws IOException, ServletException {

    Matcher urlMatcher = ADD_AMENITY_REQUEST_PATTERN.matcher(request.getRequestURI());

    if (urlMatcher.find() && !isUserCommunityAdmin(request)) {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      return;
    }

    super.doFilterInternal(request, response, chain);
  }

  /**
   * Checks if the current user is a community admin for a specified community, based
   * on the community ID extracted from the HTTP request URI. It uses a service to
   * retrieve community admins and checks if the current user ID matches any of them.
   *
   * @param request HTTP request object, from which the community ID is extracted as
   * part of the request URI.
   *
   * Get the request URL.
   * Get the request URI.
   * Get the path info.
   * Get the servlet path.
   * Get the path info.
   * Get the context path.
   * Get the request protocol.
   * Get the request method.
   * Get the request headers.
   * Get the request parameters.
   * Get the path info.
   * Get the path info.
   * Get the path info.
   * Get the path info.
   * Get the path info.
   * Get the path info.
   *
   * @returns a boolean value indicating whether the user is a community admin.
   */
  private boolean isUserCommunityAdmin(HttpServletRequest request) {
    String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String communityId = request.getRequestURI().split("/")[2];

    return communityService.findCommunityAdminsById(communityId, null)
        .flatMap(admins -> admins.stream()
            .map(User::getUserId)
            .filter(userId::equals)
            .findFirst()
        )
        .isPresent();
  }
}

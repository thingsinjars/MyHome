package com.myhome.security;

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
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Enforces authorization for community administrators by checking if the authenticated
 * user is an admin of the community being accessed. It filters incoming requests
 * based on a specific URL pattern and verifies user membership in the community.
 */
public class CommunityAuthorizationFilter extends BasicAuthenticationFilter {
    private final CommunityService communityService;
    private final String uuidPattern = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
    private final Pattern addAdminRequestPattern = Pattern.compile("/communities/" + uuidPattern + "/admins");


    public CommunityAuthorizationFilter(AuthenticationManager authenticationManager,
                                        CommunityService communityService) {
        super(authenticationManager);
        this.communityService = communityService;
    }

    /**
     * Checks if the current request matches a predefined admin pattern and if the user
     * is not a community admin. If the request is an admin request but the user is not
     * authorized, it sets the response status to unauthorized. Otherwise, it proceeds
     * with the filter chain.
     *
     * @param request HTTP request being processed by the filter.
     *
     * Contain a `getRequestURI` method returning the path part of the request URL.
     * Contain a `find` method for the `addAdminRequestPattern` matcher.
     *
     * @param response HttpServletResponse object that is used to set the HTTP status
     * code to SC_UNAUTHORIZED when the user does not have admin privileges.
     *
     * Set, send, and reset the HTTP status code of the response.
     *
     * @param chain sequence of filters in the filter chain, allowing the current filter
     * to pass control to the next filter in the chain.
     *
     * Pass `chain` as an object.
     * The `chain` object has a `doFilter` method, which is the core of the filter chain.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        Matcher urlMatcher = addAdminRequestPattern.matcher(request.getRequestURI());

        if (urlMatcher.find() && !isUserCommunityAdmin(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        super.doFilterInternal(request, response, chain);
    }

    /**
     * Checks if a user is an admin of a community based on the provided community ID and
     * the user's ID, using authentication information from the SecurityContextHolder.
     * It returns true if the user is found to be an admin, false otherwise.
     *
     * @param request HTTP request object, from which the community ID is extracted by
     * parsing the request URI.
     *
     * Extract the `request` object properties:
     * - `HttpServletRequest` is the class type.
     * - `request` has properties such as `getRequestURI`, `getContextPath`, `getHeader`,
     * `getParameter`, etc.
     * - `requestURI` is a string containing the URL path of the current request.
     *
     * @returns a boolean indicating whether the user is a community admin.
     */
    private boolean isUserCommunityAdmin(HttpServletRequest request) {
        String userId = (String) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        String communityId = request
                .getRequestURI().split("/")[2];
        Optional<List<User>> optional = communityService
                .findCommunityAdminsById(communityId, null);

        if (optional.isPresent()) {
            List<User> communityAdmins = optional.get();
            User admin = communityAdmins
                    .stream()
                    .filter(communityAdmin -> communityAdmin.getUserId().equals(userId))
                    .findFirst()
                    .orElse(null);

            return admin != null;
        }

        return false;
    }
}
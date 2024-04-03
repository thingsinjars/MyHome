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
     * determines if the requested URL matches a pattern for administrative requests and
     * checks if the user is a community admin. If not, it responds with a HTTP status
     * code of `SC_UNAUTHORIZED`. Otherwise, it passes the request to the next filter in
     * the chain using `super.doFilterInternal()`.
     * 
     * @param request HTTP request that is being processed by the filter.
     * 
     * @param response HttpServletResponse object that contains information about the
     * current HTTP request and is used to handle the response accordingly.
     * 
     * @param chain filter chain that the current filter is part of, and allows the current
     * filter to perform its functionality as the next step in the chain.
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
     * determines whether a user is an administrator of a community based on their user
     * ID and the community ID in the HTTP request.
     * 
     * @param request HTTP request being processed, providing information about the current
     * user and community being accessed.
     * 
     * @returns a boolean value indicating whether the user is a community administrator
     * for the specified community.
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
























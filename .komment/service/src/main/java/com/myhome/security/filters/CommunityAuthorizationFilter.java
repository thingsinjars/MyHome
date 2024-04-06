{"name":"CommunityAuthorizationFilter.java","path":"service/src/main/java/com/myhome/security/filters/CommunityAuthorizationFilter.java","content":{"structured":{"description":"A custom filter in Spring Security called CommunityAuthorizationFilter that checks if a user has admin privileges in a specific community before allowing access to amenities related to that community. The filter uses a pattern to match URLs that indicate an attempt to add amenities, and it checks if the user who made the request is an administrator of the community by querying the CommunityService class. If the user is not an administrator, the filter returns a FORBIDDEN response.","image":"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\"\n \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n<!-- Generated by graphviz version 2.43.0 (0)\n -->\n<!-- Title: com.myhome.security.filters.CommunityAuthorizationFilter Pages: 1 -->\n<svg width=\"172pt\" height=\"93pt\"\n viewBox=\"0.00 0.00 172.00 93.00\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n<g id=\"graph0\" class=\"graph\" transform=\"scale(1 1) rotate(0) translate(4 89)\">\n<title>com.myhome.security.filters.CommunityAuthorizationFilter</title>\n<!-- Node1 -->\n<g id=\"Node000001\" class=\"node\">\n<title>Node1</title>\n<g id=\"a_Node000001\"><a xlink:title=\" \">\n<polygon fill=\"#999999\" stroke=\"#666666\" points=\"164,-30 0,-30 0,0 164,0 164,-30\"/>\n<text text-anchor=\"start\" x=\"8\" y=\"-18\" font-family=\"Helvetica,sans-Serif\" font-size=\"10.00\">com.myhome.security.filters.</text>\n<text text-anchor=\"middle\" x=\"82\" y=\"-7\" font-family=\"Helvetica,sans-Serif\" font-size=\"10.00\">CommunityAuthorizationFilter</text>\n</a>\n</g>\n</g>\n<!-- Node2 -->\n<g id=\"Node000002\" class=\"node\">\n<title>Node2</title>\n<g id=\"a_Node000002\"><a xlink:title=\" \">\n<polygon fill=\"white\" stroke=\"#666666\" points=\"151.5,-85 12.5,-85 12.5,-66 151.5,-66 151.5,-85\"/>\n<text text-anchor=\"middle\" x=\"82\" y=\"-73\" font-family=\"Helvetica,sans-Serif\" font-size=\"10.00\">BasicAuthenticationFilter</text>\n</a>\n</g>\n</g>\n<!-- Node2&#45;&gt;Node1 -->\n<g id=\"edge1_Node000001_Node000002\" class=\"edge\">\n<title>Node2&#45;&gt;Node1</title>\n<g id=\"a_edge1_Node000001_Node000002\"><a xlink:title=\" \">\n<path fill=\"none\" stroke=\"#63b8ff\" d=\"M82,-55.65C82,-47.36 82,-37.78 82,-30.11\"/>\n<polygon fill=\"#63b8ff\" stroke=\"#63b8ff\" points=\"78.5,-55.87 82,-65.87 85.5,-55.87 78.5,-55.87\"/>\n</a>\n</g>\n</g>\n</g>\n</svg>\n","items":[{"id":"0cefdd2a-8f97-a3bc-4d49-e1c7b2fd3904","ancestors":[],"type":"function","description":"TODO","name":"CommunityAuthorizationFilter","code":"public class CommunityAuthorizationFilter extends BasicAuthenticationFilter {\n  private final CommunityService communityService;\n  private static final String UUID_PATTERN =\n      \"[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}\";\n  private static final Pattern ADD_AMENITY_REQUEST_PATTERN =\n      Pattern.compile(\"/communities/\" + UUID_PATTERN + \"/amenities\");\n\n  public CommunityAuthorizationFilter(AuthenticationManager authenticationManager,\n      CommunityService communityService) {\n    super(authenticationManager);\n    this.communityService = communityService;\n  }\n\n  @Override\n  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,\n      FilterChain chain) throws IOException, ServletException {\n\n    Matcher urlMatcher = ADD_AMENITY_REQUEST_PATTERN.matcher(request.getRequestURI());\n\n    if (urlMatcher.find() && !isUserCommunityAdmin(request)) {\n      response.setStatus(HttpServletResponse.SC_FORBIDDEN);\n      return;\n    }\n\n    super.doFilterInternal(request, response, chain);\n  }\n\n  private boolean isUserCommunityAdmin(HttpServletRequest request) {\n    String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();\n    String communityId = request.getRequestURI().split(\"/\")[2];\n\n    return communityService.findCommunityAdminsById(communityId, null)\n        .flatMap(admins -> admins.stream()\n            .map(User::getUserId)\n            .filter(userId::equals)\n            .findFirst()\n        )\n        .isPresent();\n  }\n}","location":{"start":17,"insert":17,"offset":" ","indent":0},"item_type":"class","length":40},{"id":"6514fc1f-ab0a-6e90-5b40-6097dd6ef203","ancestors":["0cefdd2a-8f97-a3bc-4d49-e1c7b2fd3904"],"type":"function","description":"filters HTTP requests based on a pattern and user authentication. If the request URI matches the pattern and the user is not an administrator in a specific community, it returns a FORBIDDEN status code. Otherwise, it delegates to the superclass's `doFilterInternal` method.","params":[{"name":"request","type_name":"HttpServletRequest","description":"HTTP request being filtered.\n\n* `getRequestURI()` - returns the requested resource URI\n* `matcher()` - returns a `Matcher` object that matches the request URI pattern\n* `find()` - checks if the match is successful\n* `isUserCommunityAdmin()` - a method to check if the user is an admin of a community, without providing its implementation details.","complex_type":true},{"name":"response","type_name":"HttpServletResponse","description":"HttpServletResponse object that is used to send the filtered response back to the client.\n\n1. `HttpServletResponse response`: This is an instance of the `HttpServletResponse` class, which represents the HTTP response object for the current request. It contains various attributes related to the response, such as status code, headers, and buffered content.\n2. `chain`: This is a reference to the `FilterChain` chain associated with the current request. The `doFilterInternal` method is called recursively by each filter in the chain until the last filter is reached, at which point the response is generated and returned.\n3. `request`: This is an instance of the `HttpServletRequest` class, representing the HTTP request object for the current request. It contains various attributes related to the request, such as method, URL, headers, and parameters.\n4. `isUserCommunityAdmin(request)`: This is a method that takes the `request` object as an argument and returns a boolean value indicating whether the user is an administrator of a community. If the method returns `true`, then the response status code is set to `HttpServletResponse.SC_FORBIDDEN`.\n\nThe properties of the `response` object are:\n\n1. `statusCode`: This is an integer representing the HTTP status code for the current response. It can take on values between 100 and 599, with 200 being the most common value.\n2. `headers`: This is a collection of headers associated with the current response. Each header represents a key-value pair, where the key is the header name and the value is the corresponding header value.\n3. `bufferedContent`: This is a buffer that contains the content of the current response, which can be either a string or a stream. The buffered content is generated by the `doFilterInternal` method if the response status code is not `HttpServletResponse.SC_OK`.","complex_type":true},{"name":"chain","type_name":"FilterChain","description":"3rd party filter chain that the current filter is a part of, allowing the current filter to execute its logic and pass the request on to the next filter in the chain.\n\n* `HttpServletRequest request`: The current HTTP request being processed by the filter.\n* `HttpServletResponse response`: The current HTTP response being generated by the filter.\n* `FilterChain chain`: An instance of the `FilterChain` interface, representing the chain of filters that should be applied to the incoming request.\n* `IOException`, `ServletException`: Exceptions that can be thrown by the filter, either due to an error in the filter's execution or as a result of an error in the filtering process.","complex_type":true}],"usage":{"language":"java","code":"public void someMethod(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {\n    ...\n    filterChain.doFilterInternal(request, response);\n}\n","description":""},"name":"doFilterInternal","code":"@Override\n  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,\n      FilterChain chain) throws IOException, ServletException {\n\n    Matcher urlMatcher = ADD_AMENITY_REQUEST_PATTERN.matcher(request.getRequestURI());\n\n    if (urlMatcher.find() && !isUserCommunityAdmin(request)) {\n      response.setStatus(HttpServletResponse.SC_FORBIDDEN);\n      return;\n    }\n\n    super.doFilterInternal(request, response, chain);\n  }","location":{"start":30,"insert":30,"offset":" ","indent":2},"item_type":"method","length":13},{"id":"3f85dfc1-e258-bd84-134b-7ce09bc3477b","ancestors":["0cefdd2a-8f97-a3bc-4d49-e1c7b2fd3904"],"type":"function","description":"checks if a user is an admin of a community based on their ID and the community ID in the request.","params":[{"name":"request","type_name":"HttpServletRequest","description":"HttpServletRequest object, which contains information about the current request, such as the request method, URL, and headers.\n\n* `getRequestURI()` returns the URI of the request, which contains information about the request's path and query parameters.\n* `split()` is used to split the URI into an array of substrings, with the second element in the array being the community ID.\n* `null` is passed as the second argument to `findCommunityAdminsById()` to indicate that the community ID should be fetched from the request.","complex_type":true}],"returns":{"type_name":"OptionalBoolean","description":"a boolean value indicating whether the currently authenticated user is an admin of a specific community.\n\n* The function returns a boolean value indicating whether the current user is a community admin for a given community ID.\n* The input parameters include the HTTP request object and a community ID extracted from the request URL.\n* The function first retrieves the user principal (userId) from the SecurityContextHolder, which represents the authenticated user.\n* Then it queries the community service to find the list of admins for the given community ID.\n* The function then maps the list of admins to their user IDs and filters out the current user ID using the `filter()` method.\n* Finally, the function uses the `findFirst()` method to retrieve the first matching admin user ID, which indicates whether the current user is a community admin or not.","complex_type":true},"usage":{"language":"java","code":"String communityId = \"12345678-1234-1234-1234-1234567890ab\";\nString userId = \"00000000-0000-0000-0000-000000000000\";\nHttpServletRequest request = new HttpServletRequest(\"http://localhost:3000/communities/\" + communityId + \"/amenities\");\nrequest.getRequestURI().split(\"/\")[2];\n","description":""},"name":"isUserCommunityAdmin","code":"private boolean isUserCommunityAdmin(HttpServletRequest request) {\n    String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();\n    String communityId = request.getRequestURI().split(\"/\")[2];\n\n    return communityService.findCommunityAdminsById(communityId, null)\n        .flatMap(admins -> admins.stream()\n            .map(User::getUserId)\n            .filter(userId::equals)\n            .findFirst()\n        )\n        .isPresent();\n  }","location":{"start":44,"insert":44,"offset":" ","indent":2},"item_type":"method","length":12}]}}}
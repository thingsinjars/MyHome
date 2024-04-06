{"name":"UserService.java","path":"service/src/main/java/com/myhome/services/UserService.java","content":{"structured":{"description":"An interface for a service layer, providing several methods for managing users. These methods include creating new users, resending email confirmations, listing all users, and resetting passwords. Additionally, the interface includes methods for confirming the receipt of an email and marking a user's email as confirmed.","items":[{"id":"935ee416-aadc-7685-f84f-7018a84fdbaa","ancestors":[],"type":"function","description":"provides methods for creating and managing users in a system, including creating new users, resending email confirmations, listing all users, and resetting passwords.","name":"UserService","code":"public interface UserService {\n  Optional<UserDto> createUser(UserDto request);\n\n  boolean resendEmailConfirm(String userId);\n\n  Set<User> listAll();\n\n  Set<User> listAll(Pageable pageable);\n\n  Optional<UserDto> getUserDetails(String userId);\n\n  boolean requestResetPassword(ForgotPasswordRequest forgotPasswordRequest);\n\n  boolean resetPassword(ForgotPasswordRequest passwordResetRequest);\n\n  Boolean confirmEmail(String userId, String emailConfirmToken);\n}","location":{"start":30,"insert":30,"offset":" ","indent":0},"item_type":"interface","length":17}]}}}
{"name":"ForgotPasswordRequest.java","path":"service/src/main/java/com/myhome/controllers/request/ForgotPasswordRequest.java","content":{"structured":{"description":"A class called ForgotPasswordRequest, which contains three fields: email, token, and newPassword. The email field is required to be an email address, the token field is generated and unique for each request, and the newPassword field allows the user to enter their new password.","items":[{"id":"db0e7924-213a-f68e-b945-6600b8cae163","ancestors":[],"type":"function","description":"represents a data class for storing email address, token, and new password for forgot password functionality.\nFields:\n\t- email (String): in the ForgotPasswordRequest class is of type String and represents an email address associated with the requester's account. \n\t- token (String): in the ForgotPasswordRequest class is likely used to store a unique code or identifier for the user to reset their password. \n\t- newPassword (String): in the ForgotPasswordRequest class represents a string value that a user enters to reset their password. \n\n","name":"ForgotPasswordRequest","code":"@AllArgsConstructor\n@NoArgsConstructor\n@Data\npublic class ForgotPasswordRequest {\n  @Email\n  public String email;\n  public String token;\n  public String newPassword;\n}","location":{"start":9,"insert":9,"offset":" ","indent":0},"item_type":"class","length":9}]}}}
package com.myhome.controllers;

import com.myhome.MyHomeServiceApplication;
import com.myhome.domain.User;
import com.myhome.model.CreateUserRequest;
import com.myhome.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the successful registration of a new user through the API. The test creates
 * a request body with name, email, and password, posts it to the registration endpoint,
 * and verifies that the response status code is HTTP 201 Created and the returned
 * user ID refers to a user stored in the database. Additionally, the test ensures
 * that the corresponding values in the database match the input values.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = MyHomeServiceApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class UserControllerIntegrationTest {

  private static final String TEST_NAME = "name";
  private static final String TEST_EMAIL = "email@mail.com";
  private static final String TEST_PASSWORD = "password";

  @Value("${api.public.registration.url.path}")
  private String registrationPath;

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private UserRepository userRepository;

  /**
   * Tests the successful creation of a new user through a REST API, verifying that the
   * response status code is `HttpStatus.CREATED`, and that the returned user object
   * matches the input parameters, including name and email, and that the user ID refers
   * to an existing user in the database.
   */
  @Test
  void shouldSignUpSuccessful() {
    // Given a request
    CreateUserRequest requestBody = new CreateUserRequest()
        .name(TEST_NAME)
        .email(TEST_EMAIL)
        .password(TEST_PASSWORD);

    // When a request is made
    ResponseEntity<User> responseEntity =
        testRestTemplate.postForEntity(registrationPath, requestBody, User.class);
    User responseBody = responseEntity.getBody();

    // Then the response matches expectation
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseBody)
        .isNotNull()
        .usingRecursiveComparison()
        .ignoringFields("userId")
        .isEqualTo(new User()
            .withName(TEST_NAME)
            .withEmail(TEST_EMAIL));

    // And the returned user ID should refer to a user stored in the database
    Optional<User> databaseUser = userRepository.findByUserId(responseBody.getUserId());
    assertTrue(databaseUser.isPresent());

    // And the corresponding values in the database should match the input
    assertThat(databaseUser.get().getName()).isEqualTo(TEST_NAME);
    assertThat(databaseUser.get().getEmail()).isEqualTo(TEST_EMAIL);
  }
}

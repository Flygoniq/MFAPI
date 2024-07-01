package org.example.service;

import jakarta.inject.Inject;
import org.example.model.ResponseBody;
import org.example.model.SignupRequest;
import org.example.model.User;
import org.example.model.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
@org.springframework.transaction.annotation.Transactional
public class UserService {

  @Inject
  private UserRepository userRepository;

  @Inject
  private AuthService authService;

  private static final Pattern USER_ID_PATTERN = Pattern.compile("[^a-zA-Z0-9]");

  private static final Pattern PASSWORD_PATTERN = Pattern.compile("[^!-~]");

  private static final Pattern NICKNAME_PATTERN = Pattern.compile("[^ -~]");

  public ResponseEntity<ResponseBody> createUser(final SignupRequest signupRequest) {
    ResponseBody responseBody = new ResponseBody();
    HttpStatusCode httpStatusCode = HttpStatus.OK;
    List<User> existingUsers = userRepository.findByUserId(signupRequest.getUser_id());
    if (signupRequest.getUser_id() == null || signupRequest.getUser_id().isEmpty() || signupRequest.getPassword() == null || signupRequest.getPassword().isEmpty()) {
      responseBody.setMessage("Account creation failed");
      responseBody.setCause("required user_id and password");
      httpStatusCode = HttpStatus.BAD_REQUEST;
    } else if (signupRequest.getUser_id().length() < 6 || signupRequest.getUser_id().length() > 20) {
      responseBody.setMessage("Account creation failed");
      responseBody.setCause("user_id must be between 6 and 20 characters");
      httpStatusCode = HttpStatus.BAD_REQUEST;
    } else if (signupRequest.getUser_id().length() < 8 || signupRequest.getUser_id().length() > 20) {
      responseBody.setMessage("Account creation failed");
      responseBody.setCause("password must be between 8 and 20 characters");
      httpStatusCode = HttpStatus.BAD_REQUEST;
    } else if (USER_ID_PATTERN.matcher(signupRequest.getUser_id()).find()) {
      responseBody.setMessage("Account creation failed");
      responseBody.setCause("user_id may only contain alphanumeric characters");
      httpStatusCode = HttpStatus.BAD_REQUEST;
    } else if (PASSWORD_PATTERN.matcher(signupRequest.getPassword()).find()) {
      responseBody.setMessage("Account creation failed");
      responseBody.setCause("password must consist of ascii characters without spaces or control codes");
      httpStatusCode = HttpStatus.BAD_REQUEST;
    } else if (!existingUsers.isEmpty()) {
      responseBody.setMessage("Account creation failed");
      responseBody.setCause("already same user_id is used");
      httpStatusCode = HttpStatus.BAD_REQUEST;
    } else {
      responseBody.setMessage("Account successfully created");
      User newUser = new User(signupRequest.getUser_id(), signupRequest.getPassword());
      newUser = userRepository.save(newUser);
      responseBody.setUser(newUser);
    }

    return new ResponseEntity<>(responseBody, httpStatusCode);
  }

  public ResponseEntity<ResponseBody> getUser(String userId) {
    String authHeader = authService.getAuthContext();
    ResponseBody responseBody = new ResponseBody();
    HttpStatusCode httpStatusCode = HttpStatus.OK;
    String headerPassword = getPasswordFromHeader(authHeader);
    List<User> existingUsers = userRepository.findByUserId(userId);
    if (existingUsers.isEmpty()) {
      responseBody.setMessage("No User found");
      httpStatusCode = HttpStatus.NOT_FOUND;
    } else if (!existingUsers.get(0).getPassword().equals(headerPassword)) {
      responseBody.setMessage("Authentication Failed");
      httpStatusCode = HttpStatus.UNAUTHORIZED;
    } else {
      responseBody.setMessage("User details by " + userId);
      responseBody.setUser(existingUsers.get(0));
    }

    return new ResponseEntity<>(responseBody, httpStatusCode);
  }

  public ResponseEntity<ResponseBody> patchUser(String userId, final User patchRequest) {
    String authHeader = authService.getAuthContext();
    ResponseBody responseBody = new ResponseBody();
    HttpStatusCode httpStatusCode = HttpStatus.OK;
    String headerPassword = getPasswordFromHeader(authHeader);
    String headerUser = getUserIdFromHeader(authHeader);
    List<User> existingUsers = userRepository.findByUserId(userId);
    if (existingUsers.isEmpty()) {
      responseBody.setMessage("No User found");
      httpStatusCode = HttpStatus.NOT_FOUND;
    } else if (!existingUsers.get(0).getPassword().equals(headerPassword)) {
      responseBody.setMessage("Authentication Failed");
      httpStatusCode = HttpStatus.UNAUTHORIZED;
    } else if (!userId.equals(headerUser)) {
      responseBody.setMessage("No Permission for Update");
      httpStatusCode = HttpStatus.FORBIDDEN;
    } else if (patchRequest.getUserId() != null || patchRequest.getPassword() != null) {
      responseBody.setMessage("User updation failed");
      responseBody.setCause("not updatable user_id and password");
      httpStatusCode = HttpStatus.BAD_REQUEST;
    } else if (patchRequest.getNickname() == null && patchRequest.getComment() == null) {
      responseBody.setMessage("User updation failed");
      responseBody.setMessage("required nickname or comment");
      httpStatusCode = HttpStatus.BAD_REQUEST;
    } else if (patchRequest.getNickname().length() > 30) {
      responseBody.setMessage("User updation failed");
      responseBody.setCause("nickname must be less than 30 characters");
      httpStatusCode = HttpStatus.BAD_REQUEST;
    } else if (patchRequest.getComment().length() > 100) {
      responseBody.setMessage("User updation failed");
      responseBody.setCause("comment must be less than 30 characters");
      httpStatusCode = HttpStatus.BAD_REQUEST;
    } else if (NICKNAME_PATTERN.matcher(patchRequest.getNickname()).find()) {
      responseBody.setMessage("User updation failed");
      responseBody.setCause("nickname must consist of ascii characters without control codes");
      httpStatusCode = HttpStatus.BAD_REQUEST;
    } else if (NICKNAME_PATTERN.matcher(patchRequest.getComment()).find()) {
      responseBody.setMessage("User updation failed");
      responseBody.setCause("comment must consist of ascii characters without control codes");
      httpStatusCode = HttpStatus.BAD_REQUEST;
    } else {
      User existingUser = existingUsers.get(0);
      if (patchRequest.getNickname() != null) {
        existingUser.setNickname(patchRequest.getNickname());
      }
      if (patchRequest.getComment() != null) {
        existingUser.setComment(patchRequest.getComment());
      }
      userRepository.save(existingUser);
      responseBody.setMessage("User successfully updated");
      responseBody.setUser(patchRequest);
    }

    return new ResponseEntity<>(responseBody, httpStatusCode);
  }

  public ResponseEntity<ResponseBody> deleteUser() {
    String authHeader = authService.getAuthContext();
    ResponseBody responseBody = new ResponseBody();
    HttpStatusCode httpStatusCode = HttpStatus.OK;
    String headerPassword = getPasswordFromHeader(authHeader);
    String headerUser = getUserIdFromHeader(authHeader);
    List<User> existingUsers = userRepository.findByUserId(headerUser);
    if (existingUsers.isEmpty()) {
      responseBody.setMessage("No User found");
      httpStatusCode = HttpStatus.NOT_FOUND;
    } else if (!existingUsers.get(0).getPassword().equals(headerPassword)) {
      responseBody.setMessage("Authentication Failed");
      httpStatusCode = HttpStatus.UNAUTHORIZED;
    } else {
      responseBody.setMessage("Account and user successfully removed");
      userRepository.deleteById(headerUser);
    }
    return new ResponseEntity<>(responseBody, httpStatusCode);
  }

  private String getUserIdFromHeader(String authHeader) {
    int split = authHeader.indexOf(":");
    return authHeader.substring(0, split);
  }

  private String getPasswordFromHeader(String authHeader) {
    int split = authHeader.indexOf(":");
    if (split < 0) {
      return "";
    }
    return authHeader.substring(split + 1);
  }

}

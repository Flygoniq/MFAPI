package org.example.model;

import java.io.Serializable;

public class ResponseBody implements Serializable {

  private String message;
  private String cause;
  private User user;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getCause() {
    return cause;
  }

  public void setCause(String cause) {
    this.cause = cause;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    // Do not return password in response body
    this.user = new User(user.getUserId(), user.getNickname(), user.getComment());
  }
}

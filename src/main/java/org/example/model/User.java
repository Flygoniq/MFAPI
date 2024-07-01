package org.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity(name = "user")
@Table(name = "user")
public class User implements Serializable {

  @Id
  @Column(name = "user_id")
  private String userId;

  @Column(name = "password")
  private String password;

  @Column(name = "nickname")
  private String nickname;

  @Column(name = "comment")
  private String comment;

  public User() {
  }

  public User(String userId, String password) {
    this.userId = userId;
    this.password = password;
    this.nickname = userId;
  }

  public User(String userId, String nickname, String comment) {
    this.userId = userId;
    this.nickname = nickname;
    this.comment = comment;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
}
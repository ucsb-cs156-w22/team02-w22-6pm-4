package edu.ucsb.cs156.team02.models;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.AccessLevel;


import org.springframework.security.core.GrantedAuthority;
import edu.ucsb.cs156.team02.entities.User;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class CurrentUser {
  private User user;
  private Collection<? extends GrantedAuthority> roles;

  public User getUser(){
    return user;
  }
}

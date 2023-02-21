package com.dustin.loginregistration.services;

import com.dustin.loginregistration.models.LoginUser;
import com.dustin.loginregistration.models.User;
import com.dustin.loginregistration.repositories.UserRepository;
import java.util.Optional;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  public User register(User newUser, BindingResult result) {
    Optional<User> user = userRepository.findByEmail(newUser.getEmail());
    if (user.isPresent()) {
      result.rejectValue("email", "email", "Email already registered.");
    }
    if (!newUser.getPassword().equals(newUser.getConfirm())) {
      result.rejectValue("confirm", "Confirm", "Passwords must match!");
    }
    if (result.hasErrors()) {
      return null;
    }
    String hashed = BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt());
    newUser.setPassword(hashed);
    return userRepository.save(newUser);
  }

  public User login(LoginUser newLoginObject, BindingResult result) {
    if (!this.checkEmail(newLoginObject.getEmail())) {
      result.rejectValue("email", "noEmail", "Invalid Credentials");
    }
    if (result.hasErrors()) {
      return null;
    }
    User user = userRepository.findByEmail(newLoginObject.getEmail()).orElse(null);
    if(!BCrypt.checkpw(newLoginObject.getPassword(), user.getPassword())){
      result.rejectValue("password", "Password", "Invalid Credentials");
    }
    if(result.hasErrors()){
      return null;
    }
    return user;
  }

  public boolean checkEmail(String email) {
    Optional<User> user = userRepository.findByEmail(email);
    if (user.isPresent()) {
      return true;
    } else {
      return false;
    }
  }
}

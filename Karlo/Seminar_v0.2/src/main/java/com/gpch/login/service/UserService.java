package com.gpch.login.service;

import com.gpch.login.model.Role;
import com.gpch.login.model.User;
import com.gpch.login.repository.RoleRepository;
import com.gpch.login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;

@Service("userService")
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(1);
        Role userRole = roleRepository.findByRole("USER");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        return userRepository.save(user);
    }

    // user editanje
    public User updateUserName(String email, String name) { // name change
        User user = this.findUserByEmail(email);
        user.setName(name);
        return userRepository.save(user);
    }

    public User updateUserLastName(String email, String lastName) { // last name change
        User user = this.findUserByEmail(email);
        user.setLastName(lastName);
        return userRepository.save(user);
    }

    public User setZeroUserActive(String email){ // user delete
        User user = this.findUserByEmail(email);
        user.setActive(0);
        return userRepository.save(user);
    }
    public User updateUserPassword(String email, String password) { // user pw change
        User user = this.findUserByEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        return userRepository.save(user);
    }

}
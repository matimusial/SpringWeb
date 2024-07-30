package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.utils.BcryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void saveNewUser(User user) {
        user.setPassword(BcryptUtil.hashPassword(user.getPassword()));
        userRepository.save(user);
    }


    @Transactional
    public void updateUser(User user) {
        if (user.getUsername() == null){
            System.out.println("NULL W UPDATE USER");
            throw new IllegalArgumentException("Username cannot be null");
        }

        User existingUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getId() == null){
            user.setId(existingUser.getId());
        }
        if (user.getRole() == null){
            user.setRole(existingUser.getRole());
        }
        if (user.getEmail() == null){
            user.setEmail(existingUser.getEmail());
        }
        if (user.getPassword() == null){
            user.setPassword(BcryptUtil.hashPassword(existingUser.getPassword()));
        }
        if (user.getConfirmationCode() == null){
            user.setConfirmationCode(existingUser.getConfirmationCode());
        }
        if (user.getConfirmationCodeExpiry() == null){
            user.setConfirmationCodeExpiry(existingUser.getConfirmationCodeExpiry());
        }
        userRepository.save(user);
    }


    public List<User> findAllDesc(String col) {
        return userRepository.findAll(Sort.by(Sort.Direction.DESC, col));
    }
}

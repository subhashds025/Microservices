package com.Spring.Security.Service;

import com.Spring.Security.DTO.UserDto;
import com.Spring.Security.Entity.User;
import com.Spring.Security.Repository.UserRepository;
import com.Spring.Security.Response.APIResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
     private PasswordEncoder passwordEncoder;


    APIResponse<String> response = new APIResponse<>();

    public APIResponse<String> addUser(UserDto dto){
    if(userRepository.existsByUsername(dto.getUsername())){
        response.setData("User with this user name is already registered");
        response.setStatus(500);
        response.setMessage("Registration failed");
        return response;
    }
    if(userRepository.existsByEmail(dto.getEmail())){
        response.setData("User with this email already registered");
        response.setStatus(500);
        response.setMessage("Registration failed");
        return response;
    }
        User user=new User();
        BeanUtils.copyProperties(dto,user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        if(dto.getRole().equalsIgnoreCase("u")){
            user.setRole("ROLE_USER");
        }else{
            user.setRole("ROLE_ADMIN");
        }
        userRepository.save(user);
        response.setData("user Registered successfully");
        response.setStatus(201);

        response.setMessage("Registered successfully");

      return response;
    }
}

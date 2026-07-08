package com.Spring.Security.Controller;


import com.Spring.Security.DTO.LoginDto;
import com.Spring.Security.DTO.UserDto;
import com.Spring.Security.Entity.User;
import com.Spring.Security.Repository.UserRepository;
import com.Spring.Security.Response.APIResponse;
import com.Spring.Security.Service.JWTService;
import com.Spring.Security.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/v1")
public class securityController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserRepository userRepository;

  @PostMapping("/register")
    public ResponseEntity<APIResponse<?>> userRegiter(@RequestBody UserDto userdto){
       APIResponse<?> api = userService.addUser(userdto);
      return new ResponseEntity<>(api, HttpStatus.valueOf(api.getStatus()));
  }

  @GetMapping("/welcome")
    public int getInvite(){
      int a,b,c;
      a=3;
      b=4;
      c=a+b;
      return  c;
  }


  @PostMapping("/login")
    public ResponseEntity<APIResponse<?>> login(@RequestBody LoginDto dto){

      APIResponse<String> response = new APIResponse();
      UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(dto.getUsername(),dto.getPassword());

      try {
          Authentication authenticate = authManager.authenticate(token);

          if(authenticate.isAuthenticated()) {
             User user = userRepository.findByUsername(dto.getUsername());
              String JWTtoken=jwtService.generateToken(dto.getUsername(),user.getRole());
              response.setMessage("Login Sucessful");
              response.setStatus(200);
              response.setData(JWTtoken);
              return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
          }
      } catch (Exception e) {
          e.printStackTrace();
      }

      response.setMessage("Failed");
      response.setStatus(401);
      response.setData("Un-Authorized Access");
      return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));

  }

    @GetMapping("/get-user")
    public User getUser(@RequestParam String username) {
        return userRepository.findByUsername(username);
    }


}

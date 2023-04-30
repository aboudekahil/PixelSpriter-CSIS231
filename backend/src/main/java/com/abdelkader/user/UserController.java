package com.abdelkader.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> authLogin(@RequestBody String email, @RequestBody String password) {
        if(userRepository.existsByEmailAndPassword(email, password)){
            return ResponseEntity.ok().build();
        }
        // else
        return ResponseEntity.notFound().build();
    }
}

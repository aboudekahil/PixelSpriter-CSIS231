package com.abdelkader.user;

import com.abdelkader.country.CountryRepository;
import jakarta.persistence.EntityManager;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authLogin(@RequestBody UserAuthDTO user) {
        if (userService.auth(user)) {
            return ResponseEntity.ok().build();
        }
        // else
        return ResponseEntity.notFound().build();
    }

    @PostMapping("signup")
    public ResponseEntity<User> signup(
            @RequestBody UserCreationDTO user) {

        try {
            User saved = userService.createUser(user);
            return ResponseEntity.ok(saved);
        }catch (DataAccessException dataAccessException){
            return ResponseEntity.badRequest().build();
        }
    }
}

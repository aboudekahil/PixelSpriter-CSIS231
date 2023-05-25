package com.abdelkader.user;

import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> authLogin(UserAuthDTO user) {
        Optional<User> userFromDb = userService.auth(user);
        if (userFromDb.isPresent()) {
            UserDTO toSend = new UserDTO(userFromDb.get().getEmail(),
                    userFromDb.get().getUsername(),
                    userFromDb.get().getId());
            return ResponseEntity.ok(toSend);
        }
        // else
        return ResponseEntity.notFound().build();
    }

    @PostMapping("signup")
    public ResponseEntity<UserDTO> signup(
            UserCreationDTO user) {

        try {
            User saved = userService.createUser(user);
            UserDTO toSend = new UserDTO(saved.getEmail(), saved.getUsername(), saved.getId());
            return ResponseEntity.ok(toSend);
        }catch (DataAccessException dataAccessException){
            return ResponseEntity.badRequest().build();
        }
    }
}

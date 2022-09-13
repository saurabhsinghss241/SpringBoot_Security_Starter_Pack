package com.example.dxc.security.controllers;

import com.example.dxc.security.DTO.UserDTO;
import com.example.dxc.security.service.implementation.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public ResponseEntity<Collection<UserDTO>> getAllUsers(){
        Collection<UserDTO> users = this.userService.getAllUser();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable(name = "id") Long userId) throws Exception {
        UserDTO user = this.userService.getUserById(userId);
        return new ResponseEntity<>(user,HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO user) throws Exception {
        UserDTO savedUser = this.userService.createUser(user);
        return new ResponseEntity<>(savedUser,HttpStatus.CREATED);
    }

    @PostMapping("/{id}")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO user,@PathVariable(name = "id") Long userId) throws Exception {
        UserDTO updatedUser = this.userService.updateUserInfo(user,userId);
        return new ResponseEntity<>(updatedUser,HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable(name = "id") Long userId) throws Exception {
        this.userService.deleteUser(userId);
        return new ResponseEntity<>(null,HttpStatus.OK);
    }


}

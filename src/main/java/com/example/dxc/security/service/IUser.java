package com.example.dxc.security.service;

import com.example.dxc.security.DTO.UserDTO;
import com.example.dxc.security.entities.User;

import java.util.Collection;

public interface IUser {
    UserDTO createUser(UserDTO user) throws Exception;

    Collection<UserDTO> getAllUser();
    UserDTO getUserById(Long userId) throws Exception;

    UserDTO updateUserInfo(UserDTO user,Long userId) throws Exception;

    void deleteUser(Long userId) throws Exception;
}

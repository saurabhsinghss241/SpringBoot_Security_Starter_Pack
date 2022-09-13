package com.example.dxc.security.service.implementation;

import com.example.dxc.security.DTO.UserDTO;
import com.example.dxc.security.entities.Role;
import com.example.dxc.security.entities.User;
import com.example.dxc.security.repository.UserRepository;
import com.example.dxc.security.service.IUser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements IUser {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDTO createUser(UserDTO userDTO) throws Exception {
        User user = modelMapper.map(userDTO,User.class);

        Set<String> roleNames = new HashSet<>(userDTO.getRoles());
        List<Role> roles = new ArrayList<>();
        for(String roleName : roleNames){
            Role role = this.roleService.createRoleIfNotExist(roleName);
            roles.add(role);
        }
        if(roles.size()>0)
            user.setRoles(roles);

        User savedUser = this.userRepository.save(user);
        return this.modelMapper.map(savedUser,UserDTO.class);
    }

    @Override
    public Collection<UserDTO> getAllUser() {
        Collection<UserDTO> users = this.userRepository.findAll().stream().map((user)->modelMapper.map(user,UserDTO.class)).collect(Collectors.toList());
        return users;
    }

    @Override
    public UserDTO getUserById(Long userId) throws Exception {
        User user = this.userRepository.findById(userId).orElseThrow(()->new Exception("User not found Id:"+userId));
        return this.modelMapper.map(user,UserDTO.class);
    }

    @Override
    public UserDTO updateUserInfo(UserDTO userDTO, Long userId) throws Exception {
        User oldUserRecord = this.userRepository.findById(userId).orElseThrow(()->new Exception("User not found Id:"+userId));
        oldUserRecord.setEmail(userDTO.getEmail());
        oldUserRecord.setUsername(userDTO.getUsername());
        User updatedUser = this.userRepository.save(oldUserRecord);
        return this.modelMapper.map(updatedUser,UserDTO.class);
    }

    @Override
    public void deleteUser(Long userId) throws Exception {
        User user = this.userRepository.findById(userId).orElseThrow(()-> new Exception("User not Found Id:"+userId));
        this.userRepository.delete(user);
    }
}

package com.example.dxc.security.controllers;

import com.example.dxc.security.DTO.RoleDTO;
import com.example.dxc.security.service.implementation.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping("/")
    public ResponseEntity<Collection<RoleDTO>> getAllRoles(){
        Collection<RoleDTO> roles = this.roleService.getAllRoles();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable(name = "id") Long roleId) throws Exception {
        RoleDTO role = this.roleService.getRoleById(roleId);
        return new ResponseEntity<>(role,HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<RoleDTO> createNewRole(@RequestBody RoleDTO roleDTO) throws Exception {
        RoleDTO role = this.roleService.createRole(roleDTO);
        return new ResponseEntity<>(role,HttpStatus.CREATED);
    }

}

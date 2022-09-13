package com.example.dxc.security.controllers;

import com.example.dxc.security.DTO.PrivilegeDTO;
import com.example.dxc.security.service.implementation.PrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/privileges")
public class PrivilegeController {
    @Autowired
    private PrivilegeService privilegeService;

    @GetMapping("/")
    public ResponseEntity<Collection<PrivilegeDTO>> getAllPrivileges(){
        Collection<PrivilegeDTO> privileges = this.privilegeService.getAllPrivileges();
        return new ResponseEntity<>(privileges, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrivilegeDTO> getPrivilegeById(@PathVariable(name = "id") Long privilegeId) throws Exception {
        PrivilegeDTO privilege = this.privilegeService.getPrivilegeById(privilegeId);
        return new ResponseEntity<>(privilege,HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<PrivilegeDTO> createNewRole(@RequestBody PrivilegeDTO privilegeDTO) throws Exception {
        PrivilegeDTO privilege = this.privilegeService.createPrivilege(privilegeDTO);
        return new ResponseEntity<>(privilege,HttpStatus.CREATED);
    }
}

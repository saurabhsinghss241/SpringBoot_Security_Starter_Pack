package com.example.dxc.security.service;

import com.example.dxc.security.DTO.RoleDTO;
import com.example.dxc.security.entities.Role;

import java.util.Collection;

public interface IRole {
    RoleDTO createRole(RoleDTO roleDTO);
    Collection<RoleDTO> getAllRoles();
    RoleDTO getRoleById(Long roleId) throws Exception;
    Role createRoleIfNotExist(String roleName) throws Exception;
    void deleteRole(Long roleId) throws Exception;
}

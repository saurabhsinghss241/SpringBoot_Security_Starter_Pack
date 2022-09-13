package com.example.dxc.security.service.implementation;

import com.example.dxc.security.DTO.RoleDTO;
import com.example.dxc.security.entities.Privilege;
import com.example.dxc.security.entities.Role;
import com.example.dxc.security.repository.PrivilegeRepository;
import com.example.dxc.security.repository.RoleRepository;
import com.example.dxc.security.service.IRole;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleService implements IRole {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public RoleDTO createRole(RoleDTO roleDTO) {
        Optional<Role> existingRole = this.roleRepository.findByName(roleDTO.getName());
        if(existingRole.isEmpty()){
            Role role = this.modelMapper.map(roleDTO,Role.class);
            //Check for privileges if we don't have them create them.
            Collection<String> privileges = roleDTO.getPrivileges().stream().collect(Collectors.toSet());
            if(privileges!= null && privileges.size()>0){
                Collection<Privilege> rolePrivileges = new ArrayList<>();
                for(String name : privileges){
                    Privilege privilege = this.privilegeService.createPrivilegeIfNotExist(name);
                    rolePrivileges.add(privilege);
                }
                role.setPrivileges(rolePrivileges);
            }
            Role savedRole = this.roleRepository.save(role);
            return this.modelMapper.map(savedRole, RoleDTO.class);
        }
        return this.modelMapper.map(existingRole.get(),RoleDTO.class);

    }

    @Override
    public Collection<RoleDTO> getAllRoles() {
        Collection<RoleDTO> roles =  this.roleRepository.findAll().stream().map((role)->this.modelMapper.map(role, RoleDTO.class)).collect(Collectors.toSet());
        return roles;
    }

    @Override
    public RoleDTO getRoleById(Long roleId) throws Exception {
        Role role = this.roleRepository.findById(roleId).orElseThrow(()->new Exception("Role not found Id:"+roleId));
        return this.modelMapper.map(role, RoleDTO.class);
    }

    @Override
    public Role createRoleIfNotExist(String roleName) throws Exception {
        Optional<Role> roleInfo = this.roleRepository.findByName(roleName);
        if(roleInfo.isEmpty()){
            Role role = new Role(roleName);
            return this.roleRepository.save(role);
        }
        return roleInfo.get();
    }

    @Override
    public void deleteRole(Long roleId) throws Exception {
        Role role = this.roleRepository.findById(roleId).orElseThrow(()->new Exception("Role not found Id:"+roleId));
        this.roleRepository.delete(role);

    }
}

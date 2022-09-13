package com.example.dxc.security.service;

import com.example.dxc.security.DTO.PrivilegeDTO;
import com.example.dxc.security.entities.Privilege;

import java.util.Collection;

public interface IPrivilege {
    PrivilegeDTO createPrivilege(PrivilegeDTO privilegeDTO) throws Exception;
    Collection<PrivilegeDTO> getAllPrivileges();
    PrivilegeDTO getPrivilegeById(Long privilegeId) throws Exception;
    Privilege createPrivilegeIfNotExist(String privilegeName);
    void deletePrivilege(Long privilegeId) throws Exception;

}

package com.saurabh.security.service;

import com.saurabh.security.DTO.PrivilegeDTO;
import com.saurabh.security.entities.Privilege;

import java.util.Collection;

public interface IPrivilege {
    PrivilegeDTO createPrivilege(PrivilegeDTO privilegeDTO) throws Exception;
    Collection<PrivilegeDTO> getAllPrivileges();
    PrivilegeDTO getPrivilegeById(Long privilegeId) throws Exception;
    Privilege createPrivilegeIfNotExist(String privilegeName);
    void deletePrivilege(Long privilegeId) throws Exception;

}

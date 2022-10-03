package com.saurabh.security.service.implementation;

import com.saurabh.security.DTO.PrivilegeDTO;
import com.saurabh.security.entities.Privilege;
import com.saurabh.security.repository.PrivilegeRepository;
import com.saurabh.security.service.IPrivilege;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PrivilegeService implements IPrivilege {
    @Autowired
    private PrivilegeRepository privilegeRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PrivilegeDTO createPrivilege(PrivilegeDTO privilegeDTO) throws Exception {
        Optional<Privilege> existingPrivilege = this.privilegeRepository.findByName(privilegeDTO.getName());
        if(existingPrivilege.isEmpty()){
            Privilege privilege = this.modelMapper.map(privilegeDTO,Privilege.class);
            Privilege savedRecord = this.privilegeRepository.save(privilege);
            return this.modelMapper.map(savedRecord, PrivilegeDTO.class);
        }
        return this.modelMapper.map(existingPrivilege.get(), PrivilegeDTO.class);

    }

    @Override
    public Collection<PrivilegeDTO> getAllPrivileges() {
        Collection<PrivilegeDTO> privileges = this.privilegeRepository.findAll().stream().map(privilege -> this.modelMapper.map(privilege, PrivilegeDTO.class)).collect(Collectors.toSet());
        return privileges;
    }

    @Override
    public PrivilegeDTO getPrivilegeById(Long privilegeId) throws Exception {
        Privilege privilege = this.privilegeRepository.findById(privilegeId).orElseThrow(()->new Exception("Privilege not found Id:"+privilegeId));
        return this.modelMapper.map(privilege, PrivilegeDTO.class);
    }

    @Override
    public Privilege createPrivilegeIfNotExist(String privilegeName) {
        Optional<Privilege> privilegeInfo = this.privilegeRepository.findByName(privilegeName);
        if(privilegeInfo.isEmpty()){
            Privilege privilege = new Privilege(privilegeName);
            return this.privilegeRepository.save(privilege);
        }
        return privilegeInfo.get();
    }

    @Override
    public void deletePrivilege(Long privilegeId) throws Exception {
        Optional<Privilege> privilege = this.privilegeRepository.findById(privilegeId);
        if(privilege.isEmpty())
            throw new Exception(String.format("Privilege with Id: %s not exist.",privilegeId));
        this.privilegeRepository.delete(privilege.get());
    }


}

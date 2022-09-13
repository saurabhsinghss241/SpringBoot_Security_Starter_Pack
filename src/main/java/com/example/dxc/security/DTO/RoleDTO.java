package com.example.dxc.security.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

@Data
@NoArgsConstructor
public class RoleDTO {
    private Long id;
    private String name;
    Collection<String> privileges = new ArrayList<>();
}

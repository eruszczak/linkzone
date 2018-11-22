package com.example.reddit.service;

import com.example.reddit.exception.NotFoundException;
import com.example.reddit.model.Role;
import com.example.reddit.permissions.RoleName;
import com.example.reddit.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role findByName(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException(Role.class, roleName.toString()));
    }
}

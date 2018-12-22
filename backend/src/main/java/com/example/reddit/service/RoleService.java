package com.example.reddit.service;

import com.example.reddit.exception.NotFoundException;
import com.example.reddit.model.Role;
import com.example.reddit.permissions.RoleName;
import com.example.reddit.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role findByName(RoleName roleName) {
        Optional<Role> role = roleRepository.findByName(roleName);
        if (!role.isPresent()) {
            Role newRole = new Role();
            newRole.setName(roleName);
            return roleRepository.save(newRole);
        }
        return role.get();
    }
}

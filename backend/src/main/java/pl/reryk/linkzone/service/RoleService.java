package pl.reryk.linkzone.service;

import pl.reryk.linkzone.model.Role;
import pl.reryk.linkzone.permissions.RoleName;
import pl.reryk.linkzone.repository.RoleRepository;
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

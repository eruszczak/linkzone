package io.eryk.linkzone.repository;

import io.eryk.linkzone.model.Role;
import io.eryk.linkzone.permissions.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}

package pl.reryk.linkzone.repository;

import pl.reryk.linkzone.model.Role;
import pl.reryk.linkzone.permissions.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}

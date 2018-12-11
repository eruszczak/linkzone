package com.example.reddit.repository;

import com.example.reddit.model.GroupMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMembershipRepository extends JpaRepository<GroupMembership, Long> {
    List<GroupMembership> findByAccountUsername(String username);

    boolean existsByGroupIdAndAccountId(Long groupId, Long accountId);

    Optional<GroupMembership> findByAccountUsernameAndGroupName(String accountUsername, String groupName);
}

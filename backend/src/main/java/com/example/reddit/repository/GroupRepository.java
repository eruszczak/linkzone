package com.example.reddit.repository;

import com.example.reddit.model.Group;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByNameIgnoreCase(String name);
    Page<Group> findByNameIgnoreCaseContaining(Pageable pageable, String name);

    @Modifying
    @Transactional
    @Query("update Group g set g.bannerUrl = :bannerUrl where g.name = :name")
    void updateGroupBannerUrl(@Param("bannerUrl") String bannerUrl, @Param("name") String groupName);
}

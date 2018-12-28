package com.example.reddit.repository;

import com.example.reddit.dto.IGroupResponseDto;
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

    @Query(value = "SELECT g.id as id, g.name as name, g.description as description, g.banner_url as bannerUrl, g.created_at as createdAt, g.logo as logo," +
            " (SELECT COUNT(*) FROM group_membership gm WHERE gm.group_id = g.id) as subscribers," +
            " (SELECT COUNT(*) FROM group_membership gm WHERE gm.group_id = g.id AND gm.user_id = :userId) as isSubbed" +
            " FROM group_tbl g" +
            " WHERE lower(g.name) LIKE lower(:query)" +
            " ORDER BY subscribers DESC, isSubbed DESC, ?#{#pageable}",
            nativeQuery = true,
            countQuery = "SELECT COUNT(*) FROM group_tbl g WHERE lower(g.name) LIKE lower(:query)")
    Page<IGroupResponseDto> search(@Param("query") String query, @Param("userId") Long userId, @Param("pageable") Pageable pageable);

    Optional<Group> findByNameIgnoreCase(String name);

    Page<Group> findByNameIgnoreCaseContaining(Pageable pageable, String name);

    @Modifying
    @Transactional
    @Query("update Group g set g.logo = :logo where g.name = :name")
    void updateLogo(@Param("logo") String logo, @Param("name") String groupName);

    @Modifying
    @Transactional
    @Query("update Group g set g.bannerUrl = :bannerUrl where g.name = :name")
    void updateGroupBannerUrl(@Param("bannerUrl") String bannerUrl, @Param("name") String groupName);
}

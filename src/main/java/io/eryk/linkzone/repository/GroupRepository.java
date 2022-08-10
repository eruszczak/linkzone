package io.eryk.linkzone.repository;

import io.eryk.linkzone.dto.IGroupResponseDto;
import io.eryk.linkzone.model.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {

    String SElECT_PART = "SELECT g.id as id, g.name as name, g.description as description, g.banner_url as bannerUrl, g.created_at as createdAt, g.logo as logo, g.is_default as isDefault,\n";
    String COUNTS_PART = """
            (SELECT COUNT(*) FROM group_membership gm WHERE gm.group_id = g.id) as subscribers,
            (SELECT COUNT(*) FROM group_membership gm WHERE gm.group_id = g.id AND gm.user_id = :requestUserId) as isSubbed
            """;

    @Query(value = SElECT_PART + " x.postTypes as postTypes," +
            " (SELECT COUNT(*) FROM group_membership gm WHERE gm.group_id = g.id) as subscribers," +
            " (SELECT COUNT(*) FROM group_membership gm WHERE gm.group_id = g.id AND gm.user_id = :userId) as isSubbed" +
            " FROM group_tbl g" +
            " LEFT JOIN (SELECT t.group_id, GROUP_CONCAT(t.post_type) AS postTypes FROM tbl_post_type t GROUP BY t.group_id) x ON x.group_id = g.id\n" +
            " WHERE lower(g.name) LIKE lower(:query)" +
            " ORDER BY subscribers DESC, isSubbed DESC",
            nativeQuery = true,
            countQuery = "SELECT COUNT(*) FROM group_tbl g WHERE lower(g.name) LIKE lower(:query)")
    Page<IGroupResponseDto> search(@Param("query") String query, @Param("userId") Long userId, Pageable pageable);

    @Query(value = SElECT_PART + " x.postTypes as postTypes," +
            " (SELECT COUNT(*) FROM posts p WHERE p.group_id = g.id) as postCount," +
            " (SELECT COUNT(*) FROM group_membership gm WHERE gm.group_id = g.id) as subscribers," +
            " (SELECT COUNT(*) FROM group_membership gm WHERE gm.group_id = g.id AND gm.user_id = :userId) as isSubbed" +
            " FROM group_tbl g\n" +
            " LEFT JOIN (SELECT t.group_id, GROUP_CONCAT(t.post_type) AS postTypes FROM tbl_post_type t GROUP BY t.group_id) x ON x.group_id = g.id\n" +
            " WHERE lower(g.name) = lower(:query);",
            nativeQuery = true)
    Optional<IGroupResponseDto> findDtoByName(@Param("query") String query, @Param("userId") Long userId);

    Optional<Group> findByNameIgnoreCase(String name);

    List<Group> findByIsDefaultTrue();

    Page<Group> findByNameIgnoreCaseContaining(Pageable pageable, String name);

    @Query(value = SElECT_PART + "'admin' as groupStatus,\n" +
            COUNTS_PART +
            "FROM group_tbl g\n" +
            "INNER JOIN group_tbl_administrators gta ON gta.administrators_id = :userId AND gta.administrated_groups_id = g.id\n" +
            "UNION " + SElECT_PART + "'mod' as groupStatus,\n" +
            COUNTS_PART +
            "FROM group_tbl g\n" +
            "INNER JOIN group_tbl_moderators gtm ON gtm.moderators_id = :userId AND gtm.moderated_groups_id = g.id\n" +
            "UNION " + SElECT_PART + "'creator' as groupStatus,\n" +
            COUNTS_PART +
            "FROM group_tbl g WHERE g.creator_id = :userId", nativeQuery = true)
    List<IGroupResponseDto> getManagedGroups(@Param("userId") Long userId, @Param("requestUserId") Long requestUserId);

    @Query(value = SElECT_PART +
            COUNTS_PART +
            "FROM group_tbl g\n" +
            "JOIN group_membership gm ON gm.group_id = g.id AND gm.user_id = :userId", nativeQuery = true)
    List<IGroupResponseDto> getSubscribedGroups(@Param("userId") Long userId, @Param("requestUserId") Long requestUserId);

    @Modifying
    @Transactional
    @Query("update Group g set g.logo = :logo where g.name = :name")
    void updateLogo(@Param("logo") String logo, @Param("name") String groupName);

    @Modifying
    @Transactional
    @Query("update Group g set g.bannerUrl = :bannerUrl where g.name = :name")
    void updateGroupBannerUrl(@Param("bannerUrl") String bannerUrl, @Param("name") String groupName);
}

package pl.reryk.linkzone.repository;

import pl.reryk.linkzone.dto.IAccountStatsDto;
import pl.reryk.linkzone.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsernameIgnoreCase(String username);

    Optional<Account> findByEmailIgnoreCase(String email);

    Optional<Account> findByUsernameIgnoreCaseOrEmailIgnoreCase(String username, String email);

    @Query(value = "SELECT (SELECT COUNT(*) FROM comments c WHERE c.account_id = a.id) as commentCount," +
            "(SELECT COUNT(*) FROM posts p WHERE p.account_id = a.id) as postCount," +
            "(SELECT SUM(cu.is_upvote) FROM comment_upvote cu JOIN comments c ON c.id = cu.comment_id WHERE c.account_id = a.id) as commentPoints," +
            "(SELECT SUM(pu.is_upvote) FROM post_upvote pu JOIN posts p ON p.id = pu.post_id WHERE p.account_id = a.id) as postPoints" +
            " FROM accounts a WHERE a.id = :userId", nativeQuery = true)
    IAccountStatsDto calculateStats(@Param("userId") Long userId);

//    @Query("SELECT COUNT(a.id) FROM Account a")
//    long countUsers();
//    @Query(value = "SELECT * FROM accounts WHERE username = ?1", nativeQuery = true)
//    Account findByUsername(String username);

    @Modifying
    @Transactional
    @Query("update Account a set a.avatar = :avatar where a.id = :userId")
    void updateAvatar(@Param("avatar") String avatar, @Param("userId") Long userId);
}

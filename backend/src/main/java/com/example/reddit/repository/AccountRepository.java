package com.example.reddit.repository;

import com.example.reddit.dto.IAccountStatsDto;
import com.example.reddit.model.Account;
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

    @Query(value = "", nativeQuery = true)
    IAccountStatsDto calculateStats(Long userId);

//    @Query("SELECT COUNT(a.id) FROM Account a")
//    long countUsers();
//    @Query(value = "SELECT * FROM accounts WHERE username = ?1", nativeQuery = true)
//    Account findByUsername(String username);

    @Modifying
    @Transactional
    @Query("update Account a set a.avatar = :avatar where a.id = :userId")
    void updateAvatar(@Param("avatar") String avatar, @Param("userId") Long userId);
}

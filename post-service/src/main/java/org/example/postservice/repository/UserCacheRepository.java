package org.example.postservice.repository;

import org.example.postservice.Model.UserCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCacheRepository extends JpaRepository<UserCache,Long> {
    Optional<UserCache> findByEmail(String email);
    Optional<UserCache> findByUserId(Long userId);;
}

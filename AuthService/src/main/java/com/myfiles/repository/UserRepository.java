package com.myfiles.repository;

import java.util.Optional;

import com.myfiles.Dtos.RoleUpdateDto;
import com.myfiles.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myfiles.Entity.UserAccount;

@Repository
public interface UserRepository extends JpaRepository<UserAccount, Long> {
	Optional<UserAccount> findByEmail(String email);

    boolean existsByEmail(String email);
}

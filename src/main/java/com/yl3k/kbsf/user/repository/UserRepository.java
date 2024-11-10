package com.yl3k.kbsf.user.repository;

import com.yl3k.kbsf.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

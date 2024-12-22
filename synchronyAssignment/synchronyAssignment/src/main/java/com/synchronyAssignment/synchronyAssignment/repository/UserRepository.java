package com.synchronyAssignment.synchronyAssignment.repository;

import com.synchronyAssignment.synchronyAssignment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

}

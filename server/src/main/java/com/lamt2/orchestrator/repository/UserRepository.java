package com.lamt2.orchestrator.repository;

import com.lamt2.orchestrator.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    public UserEntity findByUsername(String username);

    @Query(
            "SELECT r.name FROM UserEntity u " +
            "JOIN UserRoleEntity ur ON u.id = ur.userId " +
            "JOIN RoleEntity r ON r.id = ur.roleId " +
            "WHERE u.username = :username"
    )
    public List<String> findAllUserRole(@Param("username") String username);
}

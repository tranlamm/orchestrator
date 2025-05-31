package com.lamt2.orchestrator;

import com.lamt2.orchestrator.entity.RoleEntity;
import com.lamt2.orchestrator.entity.UserEntity;
import com.lamt2.orchestrator.entity.UserRoleEntity;
import com.lamt2.orchestrator.repository.RoleRepository;
import com.lamt2.orchestrator.repository.UserRepository;
import com.lamt2.orchestrator.repository.UserRoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class SQLRepositoryMemTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Test
    public void testUserRepository() throws Exception {
        UserEntity user = new UserEntity(100L, "username", "$2a$10$heattYG3az8p91InycGqmu22NwfFjzNKKKiBQ/.xte9oFxoCyYo3O");
        userRepository.save(user);
        RoleEntity roleEntity = new RoleEntity(100L, "ROLE_ADMIN");
        roleRepository.save(roleEntity);
        UserRoleEntity userRoleEntity = new UserRoleEntity(100L, user.getId(), roleEntity.getId());
        userRoleRepository.save(userRoleEntity);

        List<String> listRole = userRepository.findAllUserRole("username");
        assertEquals(1, listRole.size());
        assertEquals("ROLE_ADMIN", listRole.get(0));
    }
}

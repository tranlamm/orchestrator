package com.lamt2.orchestrator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lamt2.orchestrator.controller.UserController;
import com.lamt2.orchestrator.entity.RoleEntity;
import com.lamt2.orchestrator.entity.UserEntity;
import com.lamt2.orchestrator.entity.UserRoleEntity;
import com.lamt2.orchestrator.repository.ModelResultRepository;
import com.lamt2.orchestrator.repository.UserRepository;
import com.lamt2.orchestrator.request.RequestLogin;
import com.lamt2.orchestrator.response.ModelSummaryResponse;
import com.lamt2.orchestrator.response.PaginatedModelSummaryResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@WebMvcTest(UserController.class)
@DataJpaTest
public class SQLRepositoryMemTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testUserRepository() throws Exception {
        UserEntity user = new UserEntity(1L, "username", "password");
        entityManager.persistAndFlush(user);
        RoleEntity roleEntity = new RoleEntity(1L, "ROLE_ADMIN");
        entityManager.persistAndFlush(roleEntity);
        UserRoleEntity userRoleEntity = new UserRoleEntity(1L, user.getId(), roleEntity.getId());
        entityManager.persistAndFlush(userRoleEntity);

        List<String> listRole = userRepository.findAllUserRole("username");
        assertEquals(1, listRole.size());
        assertEquals("ROLE_ADMIN", listRole.get(0));

        ObjectMapper objectMapper = new ObjectMapper();
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RequestLogin("username", "password", true)))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.params.redirectUrl").value("/home/train"));
    }
}

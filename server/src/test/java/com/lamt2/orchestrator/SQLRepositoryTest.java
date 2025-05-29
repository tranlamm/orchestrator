package com.lamt2.orchestrator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lamt2.orchestrator.controller.UserController;
import com.lamt2.orchestrator.entity.RoleEntity;
import com.lamt2.orchestrator.entity.UserEntity;
import com.lamt2.orchestrator.repository.UserRepository;
import com.lamt2.orchestrator.request.RequestLogin;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Import(SQLRepositoryTest.SQLRepositoryTestConfiguration.class)
@WebMvcTest(UserController.class)
public class SQLRepositoryTest {
    @TestConfiguration
    public static class SQLRepositoryTestConfiguration {
        @Bean
        public UserRepository userRepository() {
            return Mockito.mock(UserRepository.class);
        }
    }

    @Autowired
    UserRepository userRepository;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testUserRepository() throws Exception {
        UserEntity user = new UserEntity(1L, "username", "password");
        List<RoleEntity> listRole = Arrays.asList(new RoleEntity(1L, "ROLE_ADMIN"), new RoleEntity(2L, "ROLE_USER"));
        Mockito.when(userRepository.findByUsername("username")).thenReturn(user);
        Mockito.when(userRepository.findAllUserRole("username")).thenReturn(listRole.stream().map(RoleEntity::getName).collect(Collectors.toList()));
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

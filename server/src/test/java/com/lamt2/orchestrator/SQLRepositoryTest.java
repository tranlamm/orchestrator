package com.lamt2.orchestrator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lamt2.orchestrator.entity.RoleEntity;
import com.lamt2.orchestrator.entity.UserEntity;
import com.lamt2.orchestrator.repository.UserRepository;
import com.lamt2.orchestrator.request.RequestLogin;
import com.lamt2.orchestrator.service.security.CustomUserDetailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SQLRepositoryTest {

    @MockBean
    UserRepository userRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    CustomUserDetailService customUserDetailService;

    @Test
    public void testUserRepository() throws Exception {
        UserEntity user = new UserEntity(1L, "username", "$2a$10$heattYG3az8p91InycGqmu22NwfFjzNKKKiBQ/.xte9oFxoCyYo3O");
        List<RoleEntity> listRole = Arrays.asList(new RoleEntity(1L, "ROLE_ADMIN"), new RoleEntity(2L, "ROLE_USER"));
        Mockito.when(userRepository.findByUsername("username")).thenReturn(user);
        Mockito.when(userRepository.findAllUserRole("username")).thenReturn(listRole.stream().map(RoleEntity::getName).collect(Collectors.toList()));

        UserDetails userDetails = customUserDetailService.loadUserByUsername("username");
        List<GrantedAuthority> grantedAuthorityList = (List<GrantedAuthority>) userDetails.getAuthorities();
        grantedAuthorityList.forEach(e -> System.out.println(e.getAuthority()));
        System.out.println(userDetails.getUsername() + " " + userDetails.getPassword());

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

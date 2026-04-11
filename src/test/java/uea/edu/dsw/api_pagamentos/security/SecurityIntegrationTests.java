package uea.edu.dsw.api_pagamentos.security;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:gerenciador_pagamentos_security_tests"
})
class SecurityIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveAutenticarNoLoginComCredenciaisValidas() throws Exception {
        String payload = """
                {
                  "username": "admin@uea.edu.br",
                  "password": "admin123"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.expiresIn").isNumber());
    }

    @Test
    void deveBloquearAcessoSemTokenEmEndpointProtegido() throws Exception {
        mockMvc.perform(get("/pessoas"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assertTrue(status == 401 || status == 403);
                });
    }

    @Test
    @WithMockUser(roles = "USER")
    void devePermitirRoleUserListarCategorias() throws Exception {
        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deveNegarRoleUserAoExcluirPessoa() throws Exception {
        mockMvc.perform(delete("/pessoas/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void devePermitirRoleAdminExcluirPessoa() throws Exception {
        mockMvc.perform(delete("/pessoas/999999"))
                .andExpect(status().isNotFound());
    }
}

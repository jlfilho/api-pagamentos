package uea.edu.dsw.api_pagamentos.dashboard;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:gerenciador_pagamentos_dashboard_tests",
        "security.jwt.secret-key=3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b",
        "security.jwt.expiration-time=3600000"
})
class DashboardIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveRetornarContratoDoDashboardComParametrosDeData() throws Exception {
        String token = obterToken();

        mockMvc.perform(get("/dashboard")
                        .param("dataInicio", "2026-01-01")
                        .param("dataFim", "2026-01-31")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.periodoInicio").value("2026-01-01"))
                .andExpect(jsonPath("$.periodoFim").value("2026-01-31"))
                .andExpect(jsonPath("$.receitas.total").isNumber())
                .andExpect(jsonPath("$.receitas.porCategoria").isArray())
                .andExpect(jsonPath("$.despesas.total").isNumber())
                .andExpect(jsonPath("$.despesas.porCategoria").isArray())
                .andExpect(jsonPath("$.top10Despesas").isArray())
                .andExpect(jsonPath("$.top10Receitas").isArray())
                .andExpect(jsonPath("$.receitasPagasMensal").isArray())
                .andExpect(jsonPath("$.despesasPagasMensal").isArray());
    }

    @Test
    void deveRetornar400QuandoDashboardSemDataInicio() throws Exception {
        String token = obterToken();

        mockMvc.perform(get("/dashboard")
                        .param("dataFim", "2026-01-31")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void deveRetornar400QuandoDashboardComIntervaloInvalido() throws Exception {
        String token = obterToken();

        mockMvc.perform(get("/dashboard")
                        .param("dataInicio", "2026-02-10")
                        .param("dataFim", "2026-01-31")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void deveRetornar400QuandoDashboardComFormatoDataInvalido() throws Exception {
        String token = obterToken();

        mockMvc.perform(get("/dashboard")
                        .param("dataInicio", "31-01-2026")
                        .param("dataFim", "2026-01-31")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    private String obterToken() throws Exception {
        String payload = """
                {
                  "username": "admin@uea.edu.br",
                  "password": "admin123"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        return jsonNode.get("token").asText();
    }
}

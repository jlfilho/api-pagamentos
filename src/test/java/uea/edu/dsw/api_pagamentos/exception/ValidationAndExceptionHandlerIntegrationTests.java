package uea.edu.dsw.api_pagamentos.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:gerenciador_pagamentos_validation_tests"
})
class ValidationAndExceptionHandlerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveRetornar400QuandoCriarPessoaComPayloadInvalido() throws Exception {
        String token = obterToken();

        mockMvc.perform(post("/pessoas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar404ComPadraoErrorResponseQuandoCategoriaNaoEncontrada() throws Exception {
        String token = obterToken();

        mockMvc.perform(get("/categorias/999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").isString())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void deveRetornar409ComPadraoErrorResponseQuandoCategoriaEmUso() throws Exception {
        String token = obterToken();

        mockMvc.perform(delete("/categorias/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").isString())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void deveRetornar409ComPadraoErrorResponseParaRegraDeStatusRepetido() throws Exception {
        String token = obterToken();

        mockMvc.perform(patch("/pessoas/1/ativo")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("true"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").isString())
                .andExpect(jsonPath("$.timestamp").exists());
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

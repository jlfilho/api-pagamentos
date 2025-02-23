package uea.edu.dsw.api_pagamentos;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pagamentos")
public class TesteController {
    
    // Teste de API de pagamentos
    @RequestMapping("/teste")
    public String teste() {
        return "Teste de API de pagamentos";
    }
}

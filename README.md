# Tutorial: 11. SeguranÃ§a e AutenticaÃ§Ã£o com JWT

Este tutorial demonstra como implementar autenticaÃ§Ã£o e seguranÃ§a em uma API RESTful com Spring Boot, utilizando JWT (JSON Web Tokens) e a versÃ£o 0.11.5 do JJWT (dividida em mÃ³dulos: jjwt-api, jjwt-impl e jjwt-jackson). O exemplo abrange desde a criaÃ§Ã£o da branch no Git atÃ© a configuraÃ§Ã£o completa dos componentes para geraÃ§Ã£o, validaÃ§Ã£o de tokens e proteÃ§Ã£o dos endpoints.

## Como Executar Pelo CLI

No diretorio raiz do projeto, execute:

```powershell
.\mvnw.cmd spring-boot:run
```

Para executar com perfil especifico:

```powershell
$env:SPRING_PROFILES_ACTIVE='dev'; .\mvnw.cmd spring-boot:run
```

```powershell
$env:SPRING_PROFILES_ACTIVE='prod'; .\mvnw.cmd spring-boot:run
```

Depois de subir a API, acesse a documentacao em:

```text
http://localhost:8080/swagger-ui.html
```

JSON OpenAPI:

```text
http://localhost:8080/v3/api-docs
```

Observacao: no perfil `prod`, a documentacao pode estar desabilitada por configuracao.

---
## 1. CriaÃ§Ã£o da Branch no Git

Antes de iniciar a implementaÃ§Ã£o, crie uma branch para a nova funcionalidade no repositÃ³rio remoto (por exemplo, GitHub). No ambiente local, execute os comandos:

```bash
git fetch origin
git checkout -b 11-11-implementar-autenticacao-e-seguranca-com-jwt
```

> **ObservaÃ§Ã£o:** Use nomes de branch sem acentos para evitar problemas de encoding e mantenha o mesmo nome tanto no repositÃ³rio remoto quanto local.

---

## 2. DependÃªncias NecessÃ¡rias

No seu projeto Maven, inclua as seguintes dependÃªncias no arquivo **pom.xml**. Essas dependÃªncias garantem que o Spring Boot, Spring Security e JJWT (versÃ£o 0.11.5) estejam disponÃ­veis, alÃ©m do suporte Ã  validaÃ§Ã£o e Ã  persistÃªncia de dados via JPA.

```xml
<!-- Spring Boot Starter Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JJWT versÃ£o 0.11.5 (dividido em trÃªs mÃ³dulos) -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
```

> **Dica:**  
> - **jjwt-api:** ContÃ©m as interfaces e classes abstratas.  
> - **jjwt-impl:** Fornece a implementaÃ§Ã£o concreta.  
> - **jjwt-jackson:** Realiza a integraÃ§Ã£o com o Jackson para serializaÃ§Ã£o/deserializaÃ§Ã£o.

---

## 3. Entidade UsuÃ¡rio Persistida com JPA

Crie uma entidade que represente o usuÃ¡rio e implemente a interface `UserDetails` do Spring Security. Neste exemplo, as roles sÃ£o armazenadas como uma lista de _strings_ em uma tabela separada, utilizando o recurso `@ElementCollection`.

```java
package com.example.demo.model;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "role")
    private List<String> roles; // Exemplo: ["ROLE_USER", "ROLE_ADMIN"]

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
```

### 3.1. Atualize o Script SQL para Inserir um UsuÃ¡rio

No arquivo `data.sql`, adicione os comandos para inserir um usuÃ¡rio e suas roles:

```sql
INSERT INTO usuarios (username, password) VALUES ('admin@uea.edu.br', '$2a$10$Ebmi/uPZlhTEB7e39gsPTOfADOsL0IdEcEQllZyogM/WI/WKUMYdW');

INSERT INTO usuario_roles (usuario_id, role) VALUES (1, 'ROLE_USER');
INSERT INTO usuario_roles (usuario_id, role) VALUES (1, 'ROLE_ADMIN');
```

---

## 4. RepositÃ³rio para UsuÃ¡rios

Crie uma interface de repositÃ³rio para acessar os dados dos usuÃ¡rios. Essa interface estende o `JpaRepository` do Spring Data JPA:

```java
package com.example.demo.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
}
```

---

## 5. DTOs

Crie os DTOs para as requisiÃ§Ãµes e respostas da autenticaÃ§Ã£o.

### 5.1 LoginRequestDTO

Crie o DTO para receber os dados de login:

```java
package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO {
    @NotNull(message = "O email Ã© obrigatÃ³rio.")
    String username;
    @NotNull(message = "A senha Ã© obrigatÃ³ria.")
    String password;
}
```

### 5.2 LoginResponseDTO

Crie o DTO de resposta que conterÃ¡ o token JWT e o tempo de expiraÃ§Ã£o:

```java
package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    private String token;
    private long expiresIn;
}
```

---

## 6. Profiles e Propriedades de ConfiguraÃ§Ã£o

No arquivo **application.properties**, configure o perfil a ser utilizado e as propriedades relacionadas ao JWT:

```
spring.profiles.active=jwt

security.jwt.secret-key=3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b
# Tempo de expiraÃ§Ã£o: 1 hora (3600000 milissegundos)
security.jwt.expiration-time=3600000
```

---

## 7. ServiÃ§os para AutenticaÃ§Ã£o

### 7.1 JwtService

Este serviÃ§o Ã© responsÃ¡vel por gerar e validar os tokens JWT. Note que, na versÃ£o 0.11.5, a API utiliza `Jwts.builder()` e `Jwts.parserBuilder()`.

```java
package com.example.demo.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

### 7.2 AuthenticationService

Realiza a autenticaÃ§Ã£o do usuÃ¡rio utilizando o `AuthenticationManager` do Spring Security. Caso a autenticaÃ§Ã£o seja bem-sucedida, o serviÃ§o retorna o usuÃ¡rio autenticado; caso contrÃ¡rio, lanÃ§a uma exceÃ§Ã£o personalizada.

```java
package com.example.demo.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;

@Service
public class AuthenticationService {

    private final UsuarioRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
        UsuarioRepository userRepository,
        AuthenticationManager authenticationManager
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    public Usuario authenticate(LoginRequestDTO loginRequestDTO) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequestDTO.username(),
                loginRequestDTO.password()
            )
        );

        return userRepository.findByUsername(loginRequestDTO.username())
                .orElseThrow(() -> new UsernameNotFoundException("UsuÃ¡rio nÃ£o encontrado"));
    }
}
```

---

## 8. ConfiguraÃ§Ã£o de SeguranÃ§a

### 8.1 ApplicationConfiguration

Esta classe, ativa pelo perfil `jwt`, configura o UserDetailsService, o PasswordEncoder e o AuthenticationProvider (utilizando o DaoAuthenticationProvider) para o Spring Security.

```java
package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.repository.UsuarioRepository;

@Configuration
@Profile("jwt")
public class ApplicationConfiguration {

    private final UsuarioRepository userRepository;

    public ApplicationConfiguration(UsuarioRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("UsuÃ¡rio nÃ£o encontrado"));
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
```

### 8.2 JwtAuthenticationFilter

O filtro intercepta as requisiÃ§Ãµes, extrai o token JWT do header (Bearer) e, se vÃ¡lido, configura a autenticaÃ§Ã£o no contexto do Spring Security.

```java
package com.example.demo.security;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.demo.service.JwtService;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
        JwtService jwtService,
        UserDetailsService userDetailsService,
        HandlerExceptionResolver handlerExceptionResolver
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String userEmail = jwtService.extractUsername(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }
}
```

### 8.3 JwtSecurityConfig

Configura o Spring Security para utilizar o filtro JWT, definir endpoints pÃºblicos e habilitar as autorizaÃ§Ãµes via anotaÃ§Ãµes. TambÃ©m configura o CORS para permitir o acesso do frontend.

```java
package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.demo.security.JwtAuthenticationFilter;

@Configuration
@Profile("jwt")
@EnableWebSecurity
@EnableMethodSecurity
public class JwtSecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public JwtSecurityConfig(
        JwtAuthenticationFilter jwtAuthenticationFilter,
        AuthenticationProvider authenticationProvider
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html/**", "/v3/api-docs.yaml", "/swagger-resources/**"
                ).permitAll()
                .requestMatchers("/api/auth/login/**", "/logout**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:4200"); // URL do frontend
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

---

## 9. Controlador para AutenticaÃ§Ã£o

Crie o endpoint REST que receberÃ¡ as requisiÃ§Ãµes de login e retornarÃ¡ o token JWT.

```java
package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.LoginResponseDTO;
import com.example.demo.model.Usuario;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.JwtService;

@RequestMapping("/api/auth")
@RestController
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> authenticate(@Valid @RequestBody LoginRequestDTO loginUserDto) {
        Usuario authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponseDTO loginResponse = new LoginResponseDTO(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }
}
```

---

## 10. ConfiguraÃ§Ã£o de AutorizaÃ§Ã£o nos Controllers

Implemente as autorizaÃ§Ãµes usando a anotaÃ§Ã£o `@PreAuthorize` nos endpoints que exigem seguranÃ§a. Veja os exemplos a seguir:

### 10.1 Exemplo em PessoaController

```java
@GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<PessoaDTO>> listarPessoas() {
        List<PessoaDTO> pessoas = pessoaService.listarPessoas();
        if (pessoas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pessoas);
    }

    @GetMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<PessoaDTO> buscarPessoa(@PathVariable Long codigo) {
        PessoaDTO pessoa = pessoaService.buscarPessoaPorCodigo(codigo);
        return ResponseEntity.ok(pessoa);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<PessoaDTO> criarPessoa(@Valid @RequestBody PessoaDTO pessoa) {
        PessoaDTO criada = pessoaService.criarPessoa(pessoa);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{codigo}")
                .buildAndExpand(criada.getCodigo())
                .toUri();
        return ResponseEntity.created(uri).body(criada);
    }

    @PutMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<PessoaDTO> atualizarPessoa(@Valid @PathVariable Long codigo, @RequestBody PessoaDTO pessoa) {
        PessoaDTO atualizada = pessoaService.atualizarPessoa(codigo, pessoa);
        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarPessoa(@PathVariable Long codigo) {
        pessoaService.deletarPessoa(codigo);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{codigo}/ativo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PessoaDTO> atualizarStatus(@PathVariable Long codigo, @RequestBody Boolean ativo) {
        PessoaDTO atualizada = pessoaService.atualizarStatus(codigo, ativo);
        return ResponseEntity.ok(atualizada);
    }
```

### 10.2 Exemplo em CategoriaController

```java
@GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<Categoria>> listarTodas() {
        List<Categoria> categorias = categoriaService.listarTodas();
        if (categorias.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Categoria> buscarPorCodigo(@PathVariable Long codigo) {
        Categoria categoria = categoriaService.buscarPorCodigo(codigo);
        return ResponseEntity.ok(categoria);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Categoria> criarCategoria(@Valid @RequestBody Categoria categoria) {
        Categoria novaCategoria = categoriaService.salvar(categoria);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{codigo}")
                .buildAndExpand(novaCategoria.getCodigo())
                .toUri();
        return ResponseEntity.created(uri).body(novaCategoria);
    }

    @PutMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Categoria> atualizarCategoria(@PathVariable Long codigo, @Valid @RequestBody Categoria categoria) {
        Categoria categoriaAtualizada = categoriaService.atualizar(codigo, categoria);
        return ResponseEntity.ok(categoriaAtualizada);
    }

    @DeleteMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarCategoria(@PathVariable Long codigo) {
        categoriaService.deletar(codigo);
        return ResponseEntity.noContent().build();
    }
```

### 10.3 Exemplo em LancamentoController

```java
    // GET /lancamentos
     @GetMapping
     @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
     public ResponseEntity<Page<LancamentoDTO>> pesquisar(LancamentoFilterDTO lancamentoFilter, Pageable pageable) {
         Page<LancamentoDTO> lancamentos = lancamentoService.pesquisar(lancamentoFilter, pageable);
         return ResponseEntity.ok(lancamentos);
     }

    // GET /lancamentos/resumo
    @GetMapping("/resumo")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Page<ResumoLancamentoDTO>> resumir(LancamentoFilterDTO lancamentoFilter, Pageable pageable) {
        Page<ResumoLancamentoDTO> lancamentos = lancamentoService.resumir(lancamentoFilter, pageable);
        return ResponseEntity.ok(lancamentos);
    }

    // GET /lancamentos/{codigo}
    @GetMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<LancamentoDTO> buscarLancamento(@PathVariable Long codigo) {
        LancamentoDTO lancamento = lancamentoService.buscarLancamentoPorCodigo(codigo);
        return ResponseEntity.ok(lancamento);
    }

    // POST /lancamentos
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<LancamentoDTO> criarLancamento(@Valid @RequestBody LancamentoDTO lancamentoDTO) {
        LancamentoDTO lancamentoCriado = lancamentoService.criarLancamento(lancamentoDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{codigo}")
                .buildAndExpand(lancamentoCriado.getCodigo())
                .toUri();
        return ResponseEntity.created(uri).body(lancamentoCriado);
    }

    // PUT /lancamentos/{codigo}
    @PutMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<LancamentoDTO> atualizarLancamento(@Valid @PathVariable Long codigo,
            @RequestBody LancamentoDTO lancamentoDTO) {
        LancamentoDTO lancamentoAtualizado = lancamentoService.atualizarLancamento(codigo, lancamentoDTO);
        return ResponseEntity.ok(lancamentoAtualizado);
    }

    // DELETE /lancamentos/{codigo}
    @DeleteMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarLancamento(@PathVariable Long codigo) {
        lancamentoService.deletarLancamento(codigo);
        return ResponseEntity.noContent().build();
    }
```

---

## 11. Testando os Endpoints com AutenticaÃ§Ã£o

ApÃ³s configurar a API, siga este passo a passo para testar os endpoints com autenticaÃ§Ã£o:

1. **Testar o Endpoint de Login:**
   - Utilize uma ferramenta de testes de API (como Postman ou cURL) para enviar uma requisiÃ§Ã£o **POST** para o endpoint `/api/auth/login`.
   - Certifique-se de que o header `Content-Type` esteja definido como `application/json`.
   - Envie o seguinte payload (exemplo):
     ```json
     {
       "username": "admin@uea.edu.br",
       "password": "admin123"
     }
     ```
   - Exemplo usando cURL:
     ```bash
     curl -X POST http://localhost:8080/api/auth/login \
          -H "Content-Type: application/json" \
          -d '{"username": "admin@uea.edu.br", "password": "admin123"}'
     ```

2. **Verificar a Resposta do Login:**
   - Se a autenticaÃ§Ã£o for bem-sucedida, a resposta serÃ¡ um JSON contendo o token JWT e o tempo de expiraÃ§Ã£o:
     ```json
     {
       "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
       "expiresIn": 3600000
     }
     ```
   - Copie o token JWT retornado.

3. **Testar Endpoints Protegidos:**
   - Para acessar um endpoint que requer autenticaÃ§Ã£o (por exemplo, um GET em `/api/pessoas`), adicione o token JWT no cabeÃ§alho da requisiÃ§Ã£o.
   - O header deve ser: `Authorization: Bearer <TOKEN_JWT>`.
   - Exemplo com cURL:
     ```bash
     curl -X GET http://localhost:8080/api/pessoas \
          -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
     ```

4. **Verificar a Resposta dos Endpoints Protegidos:**
   - Se o token for vÃ¡lido e o usuÃ¡rio possuir as permissÃµes necessÃ¡rias, a resposta do endpoint protegido serÃ¡ exibida.
   - Caso o token esteja ausente, invÃ¡lido ou expirado, a API retornarÃ¡ um status 403 ou 401, conforme a configuraÃ§Ã£o.

5. **Testar com Ferramentas GrÃ¡ficas:**
   - No Postman, configure a variÃ¡vel do token em um ambiente e utilize-a nos headers das requisiÃ§Ãµes para facilitar o teste dos endpoints protegidos.
   - Verifique os logs e respostas para confirmar que a autenticaÃ§Ã£o estÃ¡ funcionando conforme esperado.

6. **Testar Logout (se implementado):**
   - Caso haja um endpoint para logout ou invalidaÃ§Ã£o de token, repita os passos acima para testar essa funcionalidade.

---

## 12. Commit, Push e Pull Request

ApÃ³s testar e confirmar que a API estÃ¡ funcionando corretamente, siga os passos abaixo:

1. FaÃ§a o commit das alteraÃ§Ãµes:
   ```bash
   git add .
   git commit -m "11-11-implementar-autenticacao-e-seguranca-com-jwt"
   ```
2. Envie as alteraÃ§Ãµes para o repositÃ³rio remoto:
   ```bash
   git push
   ```
3. Abra um Pull Request (PR) seguindo as diretrizes do projeto para revisÃ£o e integraÃ§Ã£o.

---

## 13. Sincronizando a Branch Main no Ambiente Local

ApÃ³s a integraÃ§Ã£o, atualize sua branch main:

```bash
git checkout main
git pull
```

---

## 14. Fechamento da Milestone e LanÃ§amento da Release

Finalize a milestone relacionada e realize o lanÃ§amento da release **v4.0.0** conforme o fluxo do projeto.



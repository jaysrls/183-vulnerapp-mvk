package ch.bbw.m183.vulnerapp;

import java.util.Arrays;
import java.util.Map;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.bbw.m183.vulnerapp.datamodel.Role;
import ch.bbw.m183.vulnerapp.datamodel.UserEntity;
import ch.bbw.m183.vulnerapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecurityWebTestClientTests {

    @LocalServerPort
    int port;

    WebTestClient webTestClient;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUpUsers() {
        this.webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
        userRepository.deleteAll();
        userRepository.save(new UserEntity()
                .setUsername("user")
                .setFullname("Normal User")
                .setPassword(passwordEncoder.encode("Password123"))
                .setRole(Role.USER));
        userRepository.save(new UserEntity()
                .setUsername("admin")
                .setFullname("Admin User")
                .setPassword(passwordEncoder.encode("AdminPassword123"))
                .setRole(Role.ADMIN));
    }

    @Test
    void getRootIsAccessibleToAnonymous() {
        webTestClient.get()
                .uri("/")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getBlogIsAccessibleToAnonymous() {
        webTestClient.get()
                .uri("/api/blog")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void postBlogIsBlockedForAnonymous() {
        webTestClient.post()
                .uri("/api/blog")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("title", "Test Blog", "body", "A valid blog body text."))
                .exchange()
                                .expectStatus().isForbidden();
    }

    @Test
    void postBlogIsForbiddenForAuthenticatedUserWithoutCsrf() {
        String sessionCookie = loginAndGetSessionCookie("user", "Password123");

        webTestClient.post()
                .uri("/api/blog")
                .header(HttpHeaders.COOKIE, sessionCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("title", "Test Blog", "body", "A valid blog body text."))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void postBlogIsAllowedForAuthenticatedUserWithCsrf() {
        String sessionCookie = loginAndGetSessionCookie("user", "Password123");
        String csrfToken = fetchCsrfToken(sessionCookie);

        webTestClient.post()
                .uri("/api/blog")
                .header(HttpHeaders.COOKIE, sessionCookie)
                .header("X-CSRF-TOKEN", csrfToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("title", "User Blog", "body", "A valid blog body text from user."))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> {
                    var body = result.getResponseBody();
                    assert body != null && !body.isBlank();
                });
    }

    @Test
    void postBlogIsAllowedForAuthenticatedAdminWithCsrf() {
        String sessionCookie = loginAndGetSessionCookie("admin", "AdminPassword123");
        String csrfToken = fetchCsrfToken(sessionCookie);

        webTestClient.post()
                .uri("/api/blog")
                .header(HttpHeaders.COOKIE, sessionCookie)
                .header("X-CSRF-TOKEN", csrfToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("title", "Admin Blog", "body", "A valid blog body text from admin."))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> {
                    var body = result.getResponseBody();
                    assert body != null && !body.isBlank();
                });
    }

    @Test
    void whoamiEndpointIsBlockedForAnonymous() {
        webTestClient.get()
                .uri("/api/user/whoami")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void whoamiEndpointIsAccessibleForAuthenticatedUser() {
        String sessionCookie = loginAndGetSessionCookie("user", "Password123");

        webTestClient.get()
                .uri("/api/user/whoami")
                .header(HttpHeaders.COOKIE, sessionCookie)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.username").isEqualTo("user");
    }

    @Test
    void adminEndpointsAreBlockedForAnonymousAndUsersButAllowedForAdmin() {
        webTestClient.get()
                .uri("/api/admin123/users")
                .exchange()
                .expectStatus().isUnauthorized();

        String userCookie = loginAndGetSessionCookie("user", "Password123");
        webTestClient.get()
                .uri("/api/admin123/users")
                .header(HttpHeaders.COOKIE, userCookie)
                .exchange()
                .expectStatus().isForbidden();

        String adminCookie = loginAndGetSessionCookie("admin", "AdminPassword123");
        webTestClient.get()
                .uri("/api/admin123/users")
                .header(HttpHeaders.COOKIE, adminCookie)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void actuatorHealthIsAccessibleToAnonymousAndShowsStatus() {
        webTestClient.get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP")
                .jsonPath("$.details").doesNotExist();
    }

    @Test
    void actuatorHealthIsAccessibleForAuthenticatedUser() {
        String sessionCookie = loginAndGetSessionCookie("user", "Password123");

        webTestClient.get()
                .uri("/actuator/health")
                .header(HttpHeaders.COOKIE, sessionCookie)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");
    }

    private String loginAndGetSessionCookie(String username, String password) {
        var csrfResult = webTestClient.get()
                .uri("/api/user/csrf-token")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        String csrfToken = parseCsrfToken(csrfResult.getResponseBody());
        String csrfCookie = extractSessionCookie(csrfResult.getResponseCookies().getFirst("JSESSIONID"));

        var loginResult = webTestClient.post()
                .uri("/api/user/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HttpHeaders.COOKIE, csrfCookie)
                .header("X-CSRF-TOKEN", csrfToken)
                .bodyValue("username=" + URLEncoder.encode(username, StandardCharsets.UTF_8)
                        + "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();
                ResponseCookie loginCookie = loginResult.getResponseCookies().getFirst("JSESSIONID");
                return loginCookie != null ? extractSessionCookie(loginCookie) : csrfCookie;
    }

        private static String parseCsrfToken(String json) {
                Pattern tokenPattern = Pattern.compile("\"token\"\s*:\s*\"([^\"]+)\"");
                Matcher matcher = tokenPattern.matcher(json);
                if (!matcher.find()) {
                        throw new IllegalStateException("Cannot parse CSRF token from JSON: " + json);
                }
                return matcher.group(1);
        }

        private static String extractSessionCookie(ResponseCookie cookie) {
                if (cookie == null) {
                        throw new IllegalStateException("Missing JSESSIONID cookie");
                }
                return "JSESSIONID=" + cookie.getValue();
        }

    private String fetchCsrfToken(String sessionCookie) {
        final String[] tokenHolder = new String[1];
        webTestClient.get()
                .uri("/api/user/csrf-token")
                .header(HttpHeaders.COOKIE, sessionCookie)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.token").value(String.class, token -> tokenHolder[0] = token);
        return tokenHolder[0];
    }
}

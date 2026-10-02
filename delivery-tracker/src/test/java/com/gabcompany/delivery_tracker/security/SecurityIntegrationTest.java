package com.gabcompany.delivery_tracker.security;

import com.gabcompany.delivery_tracker.model.Delivery;
import com.gabcompany.delivery_tracker.model.DeliveryStatus;
import com.gabcompany.delivery_tracker.repository.DeliveryRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "app.security.operator.username=test-operator",
        "app.security.operator.password=test-only-password"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @BeforeEach
    void setUp() {
        deliveryRepository.deleteAll();
        deliveryRepository.save(new Delivery("ABC123", "Private Recipient"));
    }

    @Test
    void shouldAllowPublicTrackingWithoutExposingPrivateInformation() throws Exception {
        mockMvc.perform(get("/tracking/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingCode").value("ABC123"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.recipient").doesNotExist())
                .andExpect(jsonPath("$.statusHistory").doesNotExist())
                .andExpect(jsonPath("$._links").doesNotExist())
                .andExpect(jsonPath("$.returnDeadline").doesNotExist());
    }

    @Test
    void shouldReturnNotFoundForAnUnknownPublicTrackingCode() throws Exception {
        mockMvc.perform(get("/tracking/UNKNOWN"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/deliveries", "/deliveries/ABC123", "/deliveries/ABC123/history", "/auth/csrf"
    })
    void shouldRequireAuthenticationForProtectedReads(String path) throws Exception {
        mockMvc.perform(get(path))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"DeliveryTracker\""))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void shouldNotCreateDeliveriesWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/deliveries")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"recipient\":\"Gabriel\"}"))
                .andExpect(status().isUnauthorized());

        assertEquals(1, deliveryRepository.count());
    }

    @Test
    void shouldNotUpdateStatusWithoutAuthenticationOrCsrf() throws Exception {
        mockMvc.perform(patch("/deliveries/ABC123/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"IN_TRANSIT\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));

        assertEquals(DeliveryStatus.CREATED,
                deliveryRepository.findByTrackingCode("ABC123").orElseThrow().getStatus());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/deliveries", "/deliveries/ABC123", "/deliveries/ABC123/history"})
    @WithMockUser(roles = "VIEWER")
    void shouldDenyProtectedReadsToUsersWithoutTheOperatorRole(String path) throws Exception {
        mockMvc.perform(get(path))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("Access denied."));
    }

    @Test
    @WithMockUser(roles = "VIEWER")
    void shouldDenyStatusUpdatesToUsersWithoutTheOperatorRole() throws Exception {
        mockMvc.perform(patch("/deliveries/ABC123/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"IN_TRANSIT\"}"))
                .andExpect(status().isForbidden());

        assertEquals(DeliveryStatus.CREATED,
                deliveryRepository.findByTrackingCode("ABC123").orElseThrow().getStatus());
    }

    @Test
    void shouldAuthenticateTheConfiguredOperatorAndReturnPrivateDetails() throws Exception {
        mockMvc.perform(get("/deliveries/ABC123")
                        .with(httpBasic("test-operator", "test-only-password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipient").value("Private Recipient"));
    }

    @Test
    void shouldRejectInvalidCredentialsWithJsonInsteadOfRedirectingToLogin() throws Exception {
        mockMvc.perform(get("/deliveries")
                        .with(httpBasic("test-operator", "incorrect-password")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Authentication is required."));
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void shouldRequireCsrfProtectionForWrites() throws Exception {
        mockMvc.perform(patch("/deliveries/ABC123/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"IN_TRANSIT\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        assertEquals(DeliveryStatus.CREATED,
                deliveryRepository.findByTrackingCode("ABC123").orElseThrow().getStatus());
    }

    @Test
    void shouldCreateDeliveryUsingRealAuthenticationAndTheIssuedCsrfToken() throws Exception {
        MvcResult csrfResult = mockMvc.perform(get("/auth/csrf")
                        .with(httpBasic("test-operator", "test-only-password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.headerName").value("X-CSRF-TOKEN"))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        String csrfToken = JsonPath.read(csrfResult.getResponse().getContentAsString(), "$.token");
        MockHttpSession session = (MockHttpSession) csrfResult.getRequest().getSession(false);
        assertNotNull(session);

        mockMvc.perform(post("/deliveries")
                        .with(httpBasic("test-operator", "test-only-password"))
                        .session(session)
                        .header("X-CSRF-TOKEN", csrfToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"recipient\":\"Gabriel\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipient").value("GABRIEL"));

        assertEquals(2, deliveryRepository.count());
    }

    @Test
    void shouldAllowPreflightForTheConfiguredFrontendOrigin() throws Exception {
        mockMvc.perform(options("/deliveries")
                        .header(HttpHeaders.ORIGIN, "http://localhost:4200")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
                                "authorization,content-type,x-csrf-token"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                        "http://localhost:4200"));
    }

    @Test
    void shouldRejectPreflightFromAnUnconfiguredOrigin() throws Exception {
        mockMvc.perform(options("/deliveries")
                        .header(HttpHeaders.ORIGIN, "https://untrusted.example")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }
}

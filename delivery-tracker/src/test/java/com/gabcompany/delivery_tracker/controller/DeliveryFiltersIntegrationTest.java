package com.gabcompany.delivery_tracker.controller;

import com.gabcompany.delivery_tracker.model.Delivery;
import com.gabcompany.delivery_tracker.model.DeliveryStatus;
import com.gabcompany.delivery_tracker.repository.DeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DeliveryFiltersIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @BeforeEach
    void setUp() {
        deliveryRepository.deleteAll();

        Delivery gabrielDelivery = new Delivery("ABC123", "Gabriel Silva");
        gabrielDelivery.changeStatus(DeliveryStatus.IN_TRANSIT);
        Delivery anaDelivery = new Delivery("DEF456", "Ana Souza");
        anaDelivery.changeStatus(DeliveryStatus.IN_TRANSIT);

        deliveryRepository.saveAll(List.of(
                gabrielDelivery,
                anaDelivery,
                new Delivery("GHI789", "Gabriela Costa")));
    }

    @Test
    void shouldListAllDeliveriesWhenNoFilterIsProvided() throws Exception {
        mockMvc.perform(get("/deliveries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(3));
    }

    @Test
    void shouldCombineStatusAndRecipientFiltersIgnoringCase() throws Exception {
        mockMvc.perform(get("/deliveries")
                        .param("status", "in_transit")
                        .param("recipient", "GABRIEL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].trackingCode").value("ABC123"));
    }

    @Test
    void shouldFilterDeliveriesByCreationPeriod() throws Exception {
        String createdFrom = LocalDateTime.now().minusMinutes(1).toString();
        String createdTo = LocalDateTime.now().plusMinutes(1).toString();

        mockMvc.perform(get("/deliveries")
                        .param("createdFrom", createdFrom)
                        .param("createdTo", createdTo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(3));
    }

    @Test
    void shouldExcludeDeliveriesCreatedBeforeTheInitialDate() throws Exception {
        String futureDate = LocalDateTime.now().plusMinutes(1).toString();

        mockMvc.perform(get("/deliveries").param("createdFrom", futureDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void shouldExcludeDeliveriesCreatedAfterTheFinalDate() throws Exception {
        String pastDate = LocalDateTime.now().minusMinutes(1).toString();

        mockMvc.perform(get("/deliveries").param("createdTo", pastDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void shouldKeepPaginationAndSortingWhenFiltersAreUsed() throws Exception {
        mockMvc.perform(get("/deliveries")
                        .param("recipient", "a")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "trackingCode,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].trackingCode").value("GHI789"))
                .andExpect(jsonPath("$.content[1].trackingCode").value("DEF456"));
    }

    @Test
    void shouldReturnBadRequestForInvalidStatusFilter() throws Exception {
        mockMvc.perform(get("/deliveries").param("status", "LOST"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Invalid delivery status: 'LOST'. Allowed values: "
                                + "[CREATED, IN_TRANSIT, DELIVERED, CANCELED]"));
    }

    @Test
    void shouldReturnBadRequestWhenCreationPeriodIsInverted() throws Exception {
        mockMvc.perform(get("/deliveries")
                        .param("createdFrom", "2026-09-22T00:00:00")
                        .param("createdTo", "2026-09-21T00:00:00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("createdFrom must be before or equal to createdTo."));
    }

    @Test
    void shouldReturnBadRequestForMalformedCreationDate() throws Exception {
        mockMvc.perform(get("/deliveries").param("createdFrom", "not-a-date"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Invalid value for parameter 'createdFrom': 'not-a-date'."));
    }
}

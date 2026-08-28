package com.gabcompany.delivery_tracker.controller;

import com.gabcompany.delivery_tracker.model.Delivery;
import com.gabcompany.delivery_tracker.model.DeliveryStatus;
import com.gabcompany.delivery_tracker.repository.DeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DeliveryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @BeforeEach
    void setUp() {
        deliveryRepository.deleteAll();
    }

    @Test
    void shouldUpdateStatusWhenTransitionIsValid() throws Exception {
        deliveryRepository.save(new Delivery("ABC123", "GABRIEL"));

        mockMvc.perform(patch("/deliveries/ABC123/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"IN_TRANSIT\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_TRANSIT"));

        Delivery updatedDelivery = deliveryRepository.findByTrackingCode("ABC123").orElseThrow();
        assertEquals(DeliveryStatus.IN_TRANSIT, updatedDelivery.getStatus());
    }

    @Test
    void shouldReturnConflictAndKeepCurrentStatusWhenTransitionIsInvalid() throws Exception {
        deliveryRepository.save(new Delivery("ABC123", "GABRIEL"));

        mockMvc.perform(patch("/deliveries/ABC123/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"DELIVERED\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value("Cannot change delivery status from CREATED to DELIVERED."));

        Delivery unchangedDelivery = deliveryRepository.findByTrackingCode("ABC123").orElseThrow();
        assertEquals(DeliveryStatus.CREATED, unchangedDelivery.getStatus());
    }
}

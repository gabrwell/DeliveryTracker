package com.gabcompany.delivery_tracker.config;

import com.gabcompany.delivery_tracker.model.Delivery;
import com.gabcompany.delivery_tracker.model.DeliveryStatus;
import com.gabcompany.delivery_tracker.repository.DeliveryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile("dev")
public class DataBaseSeeder implements CommandLineRunner {

    private final DeliveryRepository deliveryRepository;

    public DataBaseSeeder(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }


    @Override
    public void run(String... args) throws Exception {

        if (deliveryRepository.count() == 0) {
            Delivery d1 = new Delivery("BR100200300SP", "Carlos Silva");
            Delivery d2 = new Delivery("BR900800700RJ", "Ana Souza");
            d2.changeStatus(DeliveryStatus.IN_TRANSIT);
            Delivery d3 = new Delivery("BR555444333MG", "Marcos Paulo");

            deliveryRepository.saveAll(List.of(d1, d2, d3));

            System.out.println("Initial load of successfully completed deliveries");
        }

    }
}

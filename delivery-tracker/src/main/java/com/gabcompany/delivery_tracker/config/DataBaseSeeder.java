package com.gabcompany.delivery_tracker.config;

import com.gabcompany.delivery_tracker.model.Delivery;
import com.gabcompany.delivery_tracker.repository.DeliveryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class DataBaseSeeder implements CommandLineRunner {

    private final DeliveryRepository deliveryRepository;

    public DataBaseSeeder(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }


    @Override
    public void run(String... args) throws Exception {

        if (deliveryRepository.count() == 0) {
            Delivery d1 = new Delivery();
            d1.setTrackingCode("BR100200300SP");
            d1.setRecipient("Carlos Silva");
            d1.setStatus("IN WAIT");

            Delivery d2 = new Delivery();
            d2.setTrackingCode("BR900800700RJ");
            d2.setRecipient("Ana Souza");
            d2.setStatus("IN TRANSIT");

            Delivery d3 = new Delivery();
            d3.setTrackingCode("BR555444333MG");
            d3.setRecipient("Marcos Paulo");
            d3.setStatus("FINISHED");

            deliveryRepository.saveAll(Arrays.asList(d1,d2,d3));

            System.out.println("Initial load of successfully completed deliveries");
        }

    }
}

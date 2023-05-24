package com.zzpj.eventclient;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Stream;

public class EventManagerSupplier implements ServiceInstanceListSupplier {

    private final String serviceId;

    public EventManagerSupplier(String serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }

    @Override
    public Flux<List<ServiceInstance>> get() {

        ServiceInstance serviceInstance1 = new DefaultServiceInstance(serviceId + "1", serviceId, "localhost", 8020, false);
        ServiceInstance serviceInstance2 = new DefaultServiceInstance(serviceId + "2", serviceId, "localhost", 8021, false);
        ServiceInstance serviceInstance3 = new DefaultServiceInstance(serviceId + "3", serviceId, "localhost", 8022, false);

        return Flux.just(Stream.of(serviceInstance1, serviceInstance2, serviceInstance3).toList());
    }
}

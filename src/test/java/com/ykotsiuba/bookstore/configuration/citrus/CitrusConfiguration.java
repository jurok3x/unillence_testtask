package com.ykotsiuba.bookstore.configuration.citrus;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CitrusConfiguration {

    @Bean
    public GrpcEndpoint getGrpcEndpoint() {
        GrpcEndpointConfiguration configuration = new GrpcEndpointConfiguration("localhost", 9090);
        return new GrpcEndpoint(configuration);
    }
}

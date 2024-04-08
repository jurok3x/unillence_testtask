package com.ykotsiuba.bookstore.configuration.citrus;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CitrusConfiguration {

    private static final String HOST = "localhost";
    private static final int PORT = 9090;
    private static final long TIMEOUT = 2000L;

    @Bean
    public GrpcEndpoint getGrpcEndpoint() {
        GrpcEndpointConfiguration configuration = new GrpcEndpointConfiguration(HOST, PORT);
        configuration.setTimeout(TIMEOUT);
        return new GrpcEndpoint(configuration);
    }
}

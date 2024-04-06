package com.ykotsiuba.bookstore.configuration.citrus;

import com.consol.citrus.endpoint.AbstractEndpointConfiguration;

public class GrpcEndpointConfiguration extends AbstractEndpointConfiguration {

    private String host;
    private Integer port;

    public GrpcEndpointConfiguration(String host, Integer port) {
        super();
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }
}

package com.ykotsiuba.bookstore.configuration.citrus;

import com.consol.citrus.endpoint.AbstractEndpoint;
import com.consol.citrus.messaging.Consumer;
import com.consol.citrus.messaging.Producer;
import com.ykotsiuba.bookstore.BookServiceGrpc;
import com.ykotsiuba.bookstore.BookServiceGrpc.BookServiceBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcEndpoint extends AbstractEndpoint {
    private BookServiceBlockingStub stub;

    /**
     * Default constructor using endpoint configuration.
     *
     * @param configuration
     */
    public GrpcEndpoint(GrpcEndpointConfiguration configuration) {
        super(configuration);
        ManagedChannel channel = ManagedChannelBuilder.forAddress(configuration.getHost(), configuration.getPort())
                .usePlaintext()
                .build();

        stub = BookServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public Producer createProducer() {
        return new GrpcProducer(stub);
    }

    @Override
    public Consumer createConsumer() {
        return new GrpcConsumer();
    }
}

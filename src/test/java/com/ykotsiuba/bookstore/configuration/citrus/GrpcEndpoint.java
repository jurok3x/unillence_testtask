package com.ykotsiuba.bookstore.configuration.citrus;

import com.consol.citrus.endpoint.AbstractEndpoint;
import com.consol.citrus.messaging.Consumer;
import com.consol.citrus.messaging.Producer;
import com.google.protobuf.GeneratedMessageV3;
import com.ykotsiuba.bookstore.BookServiceGrpc;
import com.ykotsiuba.bookstore.BookServiceGrpc.BookServiceBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GrpcEndpoint extends AbstractEndpoint {
    private final BookServiceBlockingStub stub;

    private final GrpcEndpointConfiguration configuration;

    private final BlockingQueue<GeneratedMessageV3> messages;
    /**
     * Default constructor using endpoint configuration.
     *
     * @param configuration
     */
    public GrpcEndpoint(GrpcEndpointConfiguration configuration) {
        super(configuration);
        this.configuration = configuration;
        ManagedChannel channel = ManagedChannelBuilder.forAddress(configuration.getHost(), configuration.getPort())
                .usePlaintext()
                .build();
        messages = new LinkedBlockingQueue<>();
        stub = BookServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public Producer createProducer() {
        return new GrpcProducer(stub, messages);
    }

    @Override
    public Consumer createConsumer() {

        return new GrpcConsumer(messages, configuration);
    }
}

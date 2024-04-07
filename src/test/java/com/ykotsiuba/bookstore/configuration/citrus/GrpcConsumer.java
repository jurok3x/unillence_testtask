package com.ykotsiuba.bookstore.configuration.citrus;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.message.DefaultMessage;
import com.consol.citrus.message.Message;
import com.consol.citrus.messaging.Consumer;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GrpcConsumer implements Consumer {

    private final BlockingQueue<GeneratedMessageV3> messages;

    private final GrpcEndpointConfiguration configuration;

    public GrpcConsumer(BlockingQueue<GeneratedMessageV3> messages, GrpcEndpointConfiguration configuration) {
        this.messages = messages;
        this.configuration = configuration;
    }

    @Override
    public Message receive(TestContext context) {
        return receive(context, configuration.getTimeout());
    }

    @Override
    public Message receive(TestContext context, long timeout) {
        try {
            GeneratedMessageV3 grpcMessage = messages.poll(timeout, TimeUnit.MILLISECONDS);
            if (grpcMessage == null) {
                throw new TimeoutException("Timeout while waiting for a message");
            }
            String jsonResponse = JsonFormat.printer().print(grpcMessage);
            Message message = new DefaultMessage();
            message.setPayload(jsonResponse);
            return message;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return "grpc-consumer";
    }
}

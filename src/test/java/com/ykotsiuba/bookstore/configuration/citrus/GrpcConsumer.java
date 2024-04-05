package com.ykotsiuba.bookstore.configuration.citrus;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.http.message.HttpMessage;
import com.consol.citrus.message.DefaultMessage;
import com.consol.citrus.message.Message;
import com.consol.citrus.messaging.Consumer;

public class GrpcConsumer implements Consumer {

    @Override
    public Message receive(TestContext context) {
        return receive(context, 2000L);
    }

    @Override
    public Message receive(TestContext context, long timeout) {
        String grpcResponse = context.getVariable("grpcResponse");
        Message message = new DefaultMessage();
        message.setPayload(grpcResponse);
        return message;
    }

    @Override
    public String getName() {
        return "grpc-consumer";
    }
}

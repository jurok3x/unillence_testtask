package com.ykotsiuba.bookstore.configuration.citrus;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.message.Message;
import com.consol.citrus.messaging.Producer;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.ykotsiuba.bookstore.BookOuterClass;
import com.ykotsiuba.bookstore.BookServiceGrpc.BookServiceBlockingStub;

public class GrpcProducer implements Producer {

    private BookServiceBlockingStub stub;

    public GrpcProducer(BookServiceBlockingStub stub) {
        this.stub = stub;
    }


    @Override
    public void send(Message message, TestContext context) {
        String methodName = message.getHeader("method").toString();
        GpcMethods method = GpcMethods.valueOf(methodName);

        String payload = (String) message.getPayload();

        switch (method) {
            case READ_BOOK:
                try {
                    BookOuterClass.ReadBookRequest request = null;
                    BookOuterClass.ReadBookRequest.Builder builder = BookOuterClass.ReadBookRequest.newBuilder();
                    JsonFormat.parser().ignoringUnknownFields().merge(payload, builder);
                    request = builder.build();
                    BookOuterClass.Book response = stub.readBook(request);
                    String jsonResponse = JsonFormat.printer().print(response);
                    context.setVariable("grpcResponse", jsonResponse);
                } catch (InvalidProtocolBufferException e) {
                    throw new RuntimeException(e);
                }
                break;
            case CREATE_BOOK:
                try {
                    BookOuterClass.CreateBookRequest request = null;
                    BookOuterClass.CreateBookRequest.Builder builder = BookOuterClass.CreateBookRequest.newBuilder();
                    JsonFormat.parser().ignoringUnknownFields().merge(payload, builder);
                    request = builder.build();
                    BookOuterClass.Book response = stub.createBook(request);
                    String jsonResponse = JsonFormat.printer().print(response);
                    context.setVariable("grpcResponse", jsonResponse);
                } catch (InvalidProtocolBufferException e) {
                    throw new RuntimeException(e);
                }
                break;
            case READ_ALL_BOOKS:
                try {
                    BookOuterClass.Empty request = BookOuterClass.Empty.newBuilder().build();
                    BookOuterClass.BookList response = stub.readALLBooks(request);
                    String jsonResponse = JsonFormat.printer().print(response);
                    context.setVariable("grpcResponse", jsonResponse);
                } catch (InvalidProtocolBufferException e) {
                    throw new RuntimeException(e);
                }
                break;
            case DELETE_BOOK:
                try {
                    BookOuterClass.DeleteBookRequest request = null;
                    BookOuterClass.DeleteBookRequest.Builder builder = BookOuterClass.DeleteBookRequest.newBuilder();
                    JsonFormat.parser().ignoringUnknownFields().merge(payload, builder);
                    request = builder.build();
                    BookOuterClass.DeleteBookResponse response = stub.deleteBook(request);
                    String jsonResponse = response.getMessage();
                    context.setVariable("grpcResponse", jsonResponse);
                } catch (InvalidProtocolBufferException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                throw new RuntimeException("Unknown method.");
        }
    }

    @Override
    public String getName() {
        return "grpc-producer";
    }
}

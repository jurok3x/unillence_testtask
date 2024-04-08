package com.ykotsiuba.bookstore.configuration.citrus;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.message.Message;
import com.consol.citrus.messaging.Producer;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import com.ykotsiuba.bookstore.BookOuterClass;
import com.ykotsiuba.bookstore.BookServiceGrpc.BookServiceBlockingStub;

import java.util.concurrent.BlockingQueue;

public class GrpcProducer implements Producer {

    private static final String HEADER_NAME = "method";
    private final BookServiceBlockingStub stub;

    private final BlockingQueue<GeneratedMessageV3> messages;

    public GrpcProducer(BookServiceBlockingStub stub, BlockingQueue<GeneratedMessageV3> messages) {
        this.stub = stub;
        this.messages = messages;
    }


    @Override
    public void send(Message message, TestContext context) {
        String methodName = message.getHeader(HEADER_NAME).toString();
        GpcMethods method = GpcMethods.valueOf(methodName);

        byte[] payload = message.getPayload(byte[].class);

        switch (method) {
            case READ_BOOK:
                try {
                    BookOuterClass.ReadBookRequest request = BookOuterClass.ReadBookRequest.parseFrom(payload);
                    BookOuterClass.Book response = stub.readBook(request);
                    messages.add(response);
                } catch (InvalidProtocolBufferException e) {
                    throw new RuntimeException(e);
                }
                break;
            case CREATE_BOOK:
                try {
                    BookOuterClass.CreateBookRequest request = BookOuterClass.CreateBookRequest.parseFrom(payload);
                    BookOuterClass.Book response = stub.createBook(request);
                    messages.add(response);
                } catch (InvalidProtocolBufferException e) {
                    throw new RuntimeException(e);
                }
                break;
            case READ_ALL_BOOKS:
                try {
                    BookOuterClass.Empty request = BookOuterClass.Empty.newBuilder().build();
                    BookOuterClass.BookList response = stub.readALLBooks(request);
                    messages.add(response);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case DELETE_BOOK:
                try {
                    BookOuterClass.DeleteBookRequest request = BookOuterClass.DeleteBookRequest.parseFrom(payload);
                    BookOuterClass.DeleteBookResponse response = stub.deleteBook(request);
                    messages.add(response);
                } catch (InvalidProtocolBufferException e) {
                    throw new RuntimeException(e);
                }
                break;
            case UPDATE_BOOK:
                try {
                    BookOuterClass.UpdateBookRequest request = BookOuterClass.UpdateBookRequest.parseFrom(payload);
                    BookOuterClass.Book response = stub.updateBook(request);
                    messages.add(response);
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

package com.ykotsiuba.bookstore.service;

import com.consol.citrus.GherkinTestActionRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.junit.jupiter.CitrusSupport;
import com.consol.citrus.message.DefaultMessage;
import com.consol.citrus.message.Message;
import com.consol.citrus.message.MessageType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import com.ykotsiuba.bookstore.BookOuterClass;
import com.ykotsiuba.bookstore.TestBookstoreApplication;
import com.ykotsiuba.bookstore.configuration.citrus.GpcMethods;
import com.ykotsiuba.bookstore.configuration.citrus.GrpcEndpoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import static com.consol.citrus.actions.ReceiveMessageAction.Builder.receive;
import static com.consol.citrus.actions.SendMessageAction.Builder.send;
import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;

@CitrusSupport
@SpringBootTest
@Import(TestBookstoreApplication.class)
@TestPropertySource(properties = {
        "spring.liquibase.enabled=true",
        "spring.liquibase.change-log=classpath:db/changelog/test-changelog-master.xml"
})
public class BookITCitrus {

    private static final int EXPECTED_BOOKS_NUMBER = 2;
    private static final String HEADER_NAME = "method";
    public static final String BOOK_DELETED_MESSAGE = "Book deleted.";

    @Autowired
    GrpcEndpoint grpcEndpoint;

    @Test
    @CitrusTest
    public void testReadBookService(@CitrusResource GherkinTestActionRunner runner) throws InvalidProtocolBufferException {
        BookOuterClass.ReadBookRequest request = prepareFindByIdRequest();
        Message message = prepareMessage(request, GpcMethods.READ_BOOK);

        BookOuterClass.Book response = prepareFindByIdResponse();

        runner.$(send()
                        .endpoint(grpcEndpoint)
                        .message(message)
                .build());

        runner.$(receive()
                .endpoint(grpcEndpoint)
                .message()
                .validate(jsonPath()
                        .expression("$.title", response.getTitle())
                        .expression("$.author", response.getAuthor())
                        .expression("$.isbn", response.getIsbn())
                        .expression("$.quantity", response.getQuantity())
                )
                .build());
    }

    @Test
    @CitrusTest
    public void testReadAllBooksService(@CitrusResource GherkinTestActionRunner runner) {
        runner.$(send()
                .endpoint(grpcEndpoint)
                .message()
                .header(HEADER_NAME, GpcMethods.READ_ALL_BOOKS)
                .build());

        runner.$(receive()
                .endpoint(grpcEndpoint)
                .message()
                .validate(jsonPath()
                        .expression("$.books.size()", EXPECTED_BOOKS_NUMBER)
                )
                .build());
    }

    @Test
    @CitrusTest
    public void testCreateBookService(@CitrusResource GherkinTestActionRunner runner) throws InvalidProtocolBufferException {
        BookOuterClass.CreateBookRequest request = prepareCreateBookRequest();
        Message message = prepareMessage(request, GpcMethods.CREATE_BOOK);

        runner.$(send()
                .endpoint(grpcEndpoint)
                .message(message)
                .build());

        runner.$(receive()
                .endpoint(grpcEndpoint)
                .message()
                .validate(jsonPath()
                        .expression("$.title", request.getTitle())
                        .expression("$.author", request.getAuthor())
                        .expression("$.isbn", request.getIsbn())
                        .expression("$.quantity", request.getQuantity())
                )
                .build());

    }

    @Test
    @CitrusTest
    public void testDeleteBookService(@CitrusResource GherkinTestActionRunner runner) throws InvalidProtocolBufferException {
        BookOuterClass.CreateBookRequest createBookRequest = prepareCreateBookRequest();
        Message createBookMessage = prepareMessage(createBookRequest, GpcMethods.CREATE_BOOK);
        ObjectMapper mapper = new ObjectMapper();
        //Create new book to be deleted
        runner.$(send()
                .endpoint(grpcEndpoint)
                .message(createBookMessage)
                .build());

        BookOuterClass.Book bookResponse = null;
        //Read the book id
        String variableName = "newBook";
        runner.$(receive()
                .endpoint(grpcEndpoint)
                .message()
                        .extract(
                                (message, context) -> context.setVariable(variableName,
                                        message.getPayload())));
        final String[] id = {""};
        runner.then(context -> {
            String jsonString = context.getVariable(variableName);
            try {
                JsonNode jsonNode = mapper.readTree(jsonString);
                id[0] = jsonNode.get("id").asText();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        //Delete new book
        BookOuterClass.DeleteBookRequest deleteBookRequest = prepareDeleteRequest(id[0]);
        Message deleteBookMessage = prepareMessage(deleteBookRequest, GpcMethods.DELETE_BOOK);

        runner.$(send()
                .endpoint(grpcEndpoint)
                .message(deleteBookMessage)
                .build());

        runner.$(receive()
                .endpoint(grpcEndpoint)
                .message()
                .validate(jsonPath()
                        .expression("$.message", BOOK_DELETED_MESSAGE)
                )
                .build());
    }

    @Test
    @CitrusTest
    public void testUpdateBookService(@CitrusResource GherkinTestActionRunner runner) throws InvalidProtocolBufferException {
        BookOuterClass.UpdateBookRequest request = prepareUpdateBookRequest();
        Message message = prepareMessage(request, GpcMethods.UPDATE_BOOK);

        runner.$(send()
                .endpoint(grpcEndpoint)
                .message(message)
                .build());

        runner.$(receive()
                .endpoint(grpcEndpoint)
                .message()
                .validate(jsonPath()
                        .expression("$.author", request.getAuthor())
                )
                .build());
    }

    private Message prepareMessage(GeneratedMessageV3 grpcMessage, GpcMethods type) {
        byte[] byteArray = grpcMessage.toByteArray();
        DefaultMessage message = new DefaultMessage(byteArray);
        message.setHeader(HEADER_NAME, type);
        message.setType(MessageType.BINARY);
        return message;
    }

    private BookOuterClass.ReadBookRequest prepareFindByIdRequest() {
        return BookOuterClass.ReadBookRequest.newBuilder()
                .setId("00000000-0000-0000-0000-000000000002")
                .build();
    }

    private BookOuterClass.DeleteBookRequest prepareDeleteRequest(String id) {
        return BookOuterClass.DeleteBookRequest.newBuilder()
                .setId(id)
                .build();
    }

    private BookOuterClass.Book prepareFindByIdResponse() {
        return BookOuterClass.Book.newBuilder()
                .setId("00000000-0000-0000-0000-000000000002")
                .setAuthor("Author 2")
                .setTitle("Book Title 2")
                .setIsbn("ISBN-0987654321")
                .setQuantity(5)
                .build();
    }

    private BookOuterClass.CreateBookRequest prepareCreateBookRequest() {
        return BookOuterClass.CreateBookRequest.newBuilder()
                .setAuthor("Author 3")
                .setTitle("Book Title 3")
                .setIsbn("ISBN-0987654323")
                .setQuantity(50)
                .build();
    }

    private BookOuterClass.UpdateBookRequest prepareUpdateBookRequest() {
        return BookOuterClass.UpdateBookRequest.newBuilder()
                .setId("00000000-0000-0000-0000-000000000001")
                .setAuthor("new Author 3")
                .build();
    }
}

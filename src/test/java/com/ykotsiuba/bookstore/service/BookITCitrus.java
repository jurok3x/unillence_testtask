package com.ykotsiuba.bookstore.service;

import com.consol.citrus.GherkinTestActionRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.junit.jupiter.CitrusSupport;
import com.consol.citrus.message.MessageType;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
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
        String request = prepareFindByIdRequest();
        String expectedJsonResponse = prepareFindByIdResponse();

        runner.$(send()
                        .endpoint(grpcEndpoint)
                        .message()
                        .header(HEADER_NAME, GpcMethods.READ_BOOK)
                        .body(request)
                .build());

        runner.$(receive()
                .endpoint(grpcEndpoint)
                .message()
                .body(expectedJsonResponse)
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
        String requestJson = JsonFormat.printer().print(request);

        runner.$(send()
                .endpoint(grpcEndpoint)
                .message()
                .header(HEADER_NAME, GpcMethods.CREATE_BOOK)
                .body(requestJson)
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
        String deleteBookRequestJson = prepareDeleteRequest();

        BookOuterClass.CreateBookRequest createBookRequest = prepareCreateBookRequest();
        String createBookRequestJson = JsonFormat.printer().print(createBookRequest);

        runner.$(send()
                .endpoint(grpcEndpoint)
                .message()
                .header(HEADER_NAME, GpcMethods.CREATE_BOOK)
                .body(createBookRequestJson)
                .build());

        runner.$(receive()
                .endpoint(grpcEndpoint)
                .message()
                .type(MessageType.JSON)

        );

        runner.$(send()
                .endpoint(grpcEndpoint)
                .message()
                .header(HEADER_NAME, GpcMethods.DELETE_BOOK)
                .body(deleteBookRequestJson)
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
        String requestJson = JsonFormat.printer().print(request);

        runner.$(send()
                .endpoint(grpcEndpoint)
                .message()
                .header(HEADER_NAME, GpcMethods.UPDATE_BOOK)
                .body(requestJson)
                .build());

        runner.$(receive()
                .endpoint(grpcEndpoint)
                .message()
                .validate(jsonPath()
                        .expression("$.author", request.getAuthor())
                )
                .build());
    }

    private String prepareFindByIdRequest() throws InvalidProtocolBufferException {
        BookOuterClass.ReadBookRequest request = BookOuterClass.ReadBookRequest.newBuilder()
                .setId("00000000-0000-0000-0000-000000000002")
                .build();
        return JsonFormat.printer().print(request);
    }

    private String prepareDeleteRequest() throws InvalidProtocolBufferException {
        BookOuterClass.ReadBookRequest request = BookOuterClass.ReadBookRequest.newBuilder()
                .setId("00000000-0000-0000-0000-000000000001")
                .build();
        return JsonFormat.printer().print(request);
    }

    private String prepareFindByIdResponse() throws InvalidProtocolBufferException {
        BookOuterClass.Book expectedResponse = BookOuterClass.Book.newBuilder()
                .setId("00000000-0000-0000-0000-000000000002")
                .setAuthor("Author 2")
                .setTitle("Book Title 2")
                .setIsbn("ISBN-0987654321")
                .setQuantity(5)
                .build();
        return JsonFormat.printer().print(expectedResponse);
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

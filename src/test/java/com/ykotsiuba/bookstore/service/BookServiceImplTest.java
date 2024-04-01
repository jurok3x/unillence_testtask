package com.ykotsiuba.bookstore.service;

import com.ykotsiuba.bookstore.BookOuterClass;
import com.ykotsiuba.bookstore.BookServiceGrpc;
import com.ykotsiuba.bookstore.TestBookstoreApplication;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Import(TestBookstoreApplication.class)
@TestPropertySource(properties = {
        "spring.liquibase.enabled=true",
        "spring.liquibase.change-log=classpath:db/changelog/test-changelog-master.xml"
})
public class BookServiceImplTest {

    private static final int EXPECTED_BOOKS_COUNT = 2;
    private BookServiceGrpc.BookServiceBlockingStub stub;
    private ManagedChannel channel;

    @BeforeEach
    void setUp() {
        channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();

        stub = BookServiceGrpc.newBlockingStub(channel);
    }

    @AfterEach
    void tearDown() {
        channel.shutdown();
    }

    @Test
    public void testFindByIdService() {
        BookOuterClass.ReadBookRequest request = BookOuterClass.ReadBookRequest.newBuilder()
                .setId("00000000-0000-0000-0000-000000000002")
                .build();
        BookOuterClass.Book response = stub.readBook(request);
        assertNotNull(response.getAuthor());
    }

    @Test
    public void testFindByAllService() {
        BookOuterClass.Empty request = BookOuterClass.Empty.newBuilder()
                .build();
        BookOuterClass.BookList response = stub.readALLBooks(request);
        assertEquals(EXPECTED_BOOKS_COUNT, response.getBooksCount());
    }

    @Test
    public void testCreateBookService() {
        BookOuterClass.CreateBookRequest request = BookOuterClass.CreateBookRequest.newBuilder()
                .setTitle("Title")
                .setAuthor("John Doe")
                .setIsbn("ISBN-1234567892")
                .setQuantity(20)
                .build();
        BookOuterClass.Book response = stub.createBook(request);
        assertNotNull(response.getId());
    }

    @Test
    public void testUpdateBookService() {
        BookOuterClass.UpdateBookRequest request = BookOuterClass.UpdateBookRequest.newBuilder()
                .setId("00000000-0000-0000-0000-000000000001")
                .setBook(BookOuterClass.Book.newBuilder()
                        .setId("00000000-0000-0000-0000-000000000001")
                        .setTitle("Title")
                        .setAuthor("John Doe")
                        .setIsbn("ISBN-1234567892")
                        .setQuantity(20)
                        .build()
                )
                .build();
        BookOuterClass.Book response = stub.updateBook(request);
        assertEquals("John Doe", response.getAuthor());
    }

    @Test
    public void testDeleteBookService() {
        BookOuterClass.DeleteBookRequest deleteBookRequest = BookOuterClass.DeleteBookRequest.newBuilder()
                .setId("00000000-0000-0000-0000-000000000001")
                .build();
        BookOuterClass.DeleteBookResponse deleteBookResponse = stub.deleteBook(deleteBookRequest);
        assertNotNull(deleteBookResponse.getMessage());

        BookOuterClass.Empty getAllBooksRequest = BookOuterClass.Empty.newBuilder()
                .build();
        BookOuterClass.BookList getAllBooksResponse = stub.readALLBooks(getAllBooksRequest);
        assertEquals(EXPECTED_BOOKS_COUNT - 1, getAllBooksResponse.getBooksCount());
    }

}
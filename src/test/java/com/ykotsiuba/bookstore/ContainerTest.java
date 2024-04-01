package com.ykotsiuba.bookstore;

import com.ykotsiuba.bookstore.entity.Book;
import com.ykotsiuba.bookstore.repository.BookRepository;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Import(TestBookstoreApplication.class)
@TestPropertySource(properties = {
        "spring.liquibase.enabled=true",
        "spring.liquibase.change-log=classpath:db/changelog/test-changelog-master.xml"
})
public class ContainerTest {

    @Autowired
    private BookRepository repository;

    @Test
    void test() {
        List<Book> books = repository.findAll();
        assertFalse(books.isEmpty());
    }
}

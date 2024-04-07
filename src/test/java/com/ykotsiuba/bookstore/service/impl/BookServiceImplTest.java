package com.ykotsiuba.bookstore.service.impl;

import com.ykotsiuba.bookstore.dto.BookDTO;
import com.ykotsiuba.bookstore.dto.CreateBookRequestDTO;
import com.ykotsiuba.bookstore.dto.UpdateBookRequestDTO;
import com.ykotsiuba.bookstore.entity.Book;
import com.ykotsiuba.bookstore.mapper.BookMapper;
import com.ykotsiuba.bookstore.mapper.BookMapperImpl;
import com.ykotsiuba.bookstore.repository.BookRepository;
import com.ykotsiuba.bookstore.service.BookService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class BookServiceImplTest {

    @Mock
    private BookRepository repository;

    private BookService service;

    private BookMapper mapper;


    @BeforeEach
    void setUp() {
        mapper  = new BookMapperImpl();
        repository = mock(BookRepository.class);
        service = new BookServiceImpl(repository, mapper);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(repository);
    }

    @Test
    void whenCreateBook_thenReturnCorrectBook() {
        Book book = prepareBook();
        given(repository
                .save(any(Book.class)))
                .willReturn(book);

        CreateBookRequestDTO request = prepareCreateRequest();
        Mono<BookDTO> savedBookMono = service.save(request);

        assertNotNull(savedBookMono);

        StepVerifier.create(savedBookMono)
                .expectNextMatches(savedBook -> savedBook.getTitle().equals(book.getTitle()))
                .expectComplete()
                .verify();

        verify(repository).save(any(Book.class));
    }

    @Test
    void whenUpdateBook_thenReturnCorrectBook() {
        Book book = prepareBook();
        Book updatedBook = prepareBook();
        UpdateBookRequestDTO request = prepareUpdateRequest();
        updatedBook.setAuthor(request.getAuthor().get());
        given(repository
                .findById(any(UUID.class)))
                .willReturn(Optional.of(book));
        given(repository
                .save(any(Book.class)))
                .willReturn(updatedBook);


        Mono<BookDTO> updatedBookMono = service.update(book.getId().toString(), request);

        assertNotNull(updatedBookMono);

        StepVerifier.create(updatedBookMono)
                .expectNextMatches(savedBook -> savedBook.getAuthor().equals(request.getAuthor().get()))
                .expectComplete()
                .verify();

        verify(repository).save(any(Book.class));
        verify(repository).findById(any(UUID.class));
    }

    @Test
    void whenFindBook_thenReturnCorrectBook() {
        Book book = prepareBook();
        given(repository
                .findById(any(UUID.class)))
                .willReturn(Optional.of(book));

        Mono<BookDTO> foundBookMono = service.findById(book.getId().toString());

        assertNotNull(foundBookMono);

        StepVerifier.create(foundBookMono)
                .expectNextMatches(savedBook -> savedBook.getTitle().equals(book.getTitle()))
                .expectComplete()
                .verify();

        verify(repository).findById(any(UUID.class));
    }

    @Test
    void whenFindAllBooks_thenReturnCorrectBookList() {
        Book book = prepareBook();
        List<Book> books = Arrays.asList(book, book, book);
        given(repository
                .findAll())
                .willReturn(books);

        Flux<BookDTO> bookDTOFlux = service.findAll();

        assertNotNull(bookDTOFlux);

        StepVerifier.create(bookDTOFlux)
                .expectNextCount(books.size())
                .expectComplete()
                .verify();

        verify(repository).findAll();
    }

    @Test
    void whenDeleteBook_thenExecuteCorrectly() {
        Book book = prepareBook();
        given(repository
                .findById(any(UUID.class)))
                .willReturn(Optional.of(book));
        doNothing()
                .when(repository)
                .deleteById(any(UUID.class));

        Mono<Void> deleteResultMono = service.delete(book.getId().toString());

        assertNotNull(deleteResultMono);

        StepVerifier.create(deleteResultMono)
                .expectComplete()
                .verify();

        verify(repository).deleteById(any(UUID.class));
        verify(repository).findById(any(UUID.class));
    }

    private CreateBookRequestDTO prepareCreateRequest() {
        return CreateBookRequestDTO.builder()
                .author("John Doe")
                .title("Title")
                .isbn("ISBN10000100")
                .quantity(10)
                .build();
    }

    private UpdateBookRequestDTO prepareUpdateRequest() {
        return UpdateBookRequestDTO.builder()
                .author(Optional.of("New Author"))
                .title(Optional.empty())
                .isbn(Optional.empty())
                .quantity(Optional.empty())
                .build();
    }

    private Book prepareBook() {
        Book book = new Book();
        book.setId(UUID.randomUUID());
        book.setAuthor("John Doe");
        book.setIsbn("ISBN10000100");
        book.setTitle("Title");
        book.setQuantity(10);
        return book;
    }

}
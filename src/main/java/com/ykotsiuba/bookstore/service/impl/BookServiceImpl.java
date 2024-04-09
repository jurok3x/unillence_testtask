package com.ykotsiuba.bookstore.service.impl;

import com.ykotsiuba.bookstore.dto.BookDTO;
import com.ykotsiuba.bookstore.dto.CreateBookRequestDTO;
import com.ykotsiuba.bookstore.dto.UpdateBookRequestDTO;
import com.ykotsiuba.bookstore.entity.Book;
import com.ykotsiuba.bookstore.mapper.BookMapper;
import com.ykotsiuba.bookstore.repository.BookRepository;
import com.ykotsiuba.bookstore.service.BookService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Service implementation for CRUD operations on books.
 */
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private static final String BOOK_NOT_FOUND = "Book not found";
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    /**
     * Finds a book by ID.
     *
     * @param id The ID of the book to find
     * @return A Mono emitting the found book, or an error if not found
     */
    @Override
    public Mono<BookDTO> findById(String id) {
        return Mono.fromCallable(() -> bookRepository.findById(UUID.fromString(id)))
                .flatMap(Mono::justOrEmpty)
                .map(bookMapper::toDTO)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(BOOK_NOT_FOUND)));
    }

    /**
     * Finds all books.
     *
     * @return A Flux emitting all books
     */
    @Override
    public Flux<BookDTO> findAll() {
        return Flux.defer(() -> Flux.fromIterable(bookRepository.findAll())
                .map(bookMapper::toDTO));
    }

    /**
     * Saves a new book.
     *
     * @param requestDTO The request containing details of the book to save
     * @return A Mono emitting the saved book
     */
    @Override
    public Mono<BookDTO> save(CreateBookRequestDTO requestDTO) {
        Book book = new Book();
        book.setAuthor(requestDTO.getAuthor());
        book.setTitle(requestDTO.getTitle());
        book.setIsbn(requestDTO.getIsbn());
        book.setQuantity(requestDTO.getQuantity());
        return Mono.fromCallable(() -> bookRepository.save(book))
                .map(bookMapper::toDTO);
    }

    /**
     * Updates an existing book.
     *
     * @param id         The ID of the book to update
     * @param requestDTO The request containing updated details of the book
     * @return A Mono emitting the updated book
     */
    @Override
    public Mono<BookDTO> update(String id, UpdateBookRequestDTO requestDTO) {
        return findById(id)
                .flatMap(existingBook -> {
                    existingBook.setAuthor(requestDTO.getAuthor()
                            .orElse(existingBook.getAuthor()));
                    existingBook.setTitle(requestDTO.getTitle()
                            .orElse(existingBook.getTitle()));
                    existingBook.setIsbn(requestDTO.getIsbn()
                            .orElse(existingBook.getIsbn()));
                    existingBook.setQuantity(requestDTO.getQuantity()
                            .orElse(existingBook.getQuantity()));
                    return Mono.fromCallable(() -> bookRepository
                            .save(bookMapper.toEntity(existingBook))
                    );
                }).map(bookMapper::toDTO);
    }

    /**
     * Deletes a book by ID.
     *
     * @param id The ID of the book to delete
     * @return A Mono indicating completion of the delete operation
     */
    @Override
    public Mono<Void> delete(String id) {
        return findById(id)
                .flatMap(existingBook -> Mono.fromRunnable(
                        () -> bookRepository
                                .deleteById(UUID.fromString(existingBook.getId()))));
    }
}

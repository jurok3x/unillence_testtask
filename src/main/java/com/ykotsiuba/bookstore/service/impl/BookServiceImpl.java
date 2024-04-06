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

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private static final String BOOK_NOT_FOUND = "Book not found";
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public Mono<BookDTO> findById(String id) {
        return Mono.fromCallable(() -> bookRepository.findById(UUID.fromString(id)))
                .flatMap(Mono::justOrEmpty)
                .map(bookMapper::toDTO)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(BOOK_NOT_FOUND)));
    }

    @Override
    public Flux<BookDTO> findAll() {
        return Flux.defer(() -> Flux.fromIterable(bookRepository.findAll())
                .map(bookMapper::toDTO));
    }

    @Override
    public Mono<BookDTO> save(CreateBookRequestDTO requestDTO) {
        Book book = new Book();
        book.setAuthor(requestDTO.getAuthor());
        book.setTitle(requestDTO.getTitle());
        book.setIsbn(requestDTO.getIsbn());
        book.setQuantity(requestDTO.getQuantity());
        Book newBook = bookRepository.save(book);
        return Mono.fromCallable(() -> bookRepository.save(newBook))
                .map(bookMapper::toDTO);
    }

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

    @Override
    public Mono<Void> delete(String id) {
        return findById(id)
                .flatMap(existingBook -> Mono.fromRunnable(
                        () -> bookRepository
                                .deleteById(UUID.fromString(existingBook.getId()))));
    }
}

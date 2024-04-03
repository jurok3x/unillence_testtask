package com.ykotsiuba.bookstore.service;

import com.ykotsiuba.bookstore.dto.BookDTO;
import com.ykotsiuba.bookstore.dto.CreateBookRequestDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookService {

    Mono<BookDTO> findById(String id);

    Flux<BookDTO> findAll();

    Mono<BookDTO> save(CreateBookRequestDTO requestDTO);

    Mono<BookDTO> update(String id, CreateBookRequestDTO requestDTO);

    Mono<Void> delete(String id);
}

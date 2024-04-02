package com.ykotsiuba.bookstore.service;

import com.ykotsiuba.bookstore.dto.BookDTO;
import com.ykotsiuba.bookstore.dto.CreateBookRequestDTO;

import java.util.List;

public interface BookService {

    BookDTO findById(String id);

    List<BookDTO> findAll();

    BookDTO save(CreateBookRequestDTO requestDTO);

    BookDTO update(String id, CreateBookRequestDTO requestDTO);

    void delete(String id);
}

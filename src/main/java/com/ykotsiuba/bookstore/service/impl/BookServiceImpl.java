package com.ykotsiuba.bookstore.service.impl;

import com.ykotsiuba.bookstore.dto.BookDTO;
import com.ykotsiuba.bookstore.dto.CreateBookRequestDTO;
import com.ykotsiuba.bookstore.entity.Book;
import com.ykotsiuba.bookstore.mapper.BookMapper;
import com.ykotsiuba.bookstore.repository.BookRepository;
import com.ykotsiuba.bookstore.service.BookService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDTO findById(String id) {
        Book book = bookRepository.findById(UUID.fromString(id)).orElseThrow(
                () -> new EntityNotFoundException("Book not found with id: " + id)
        );
        BookDTO bookDTO = bookMapper.toDTO(book);
        return bookDTO;
    }

    @Override
    public List<BookDTO> findAll() {
        List<Book> books = bookRepository.findAll();
        return books.stream().map(bookMapper::toDTO).toList();
    }

    @Override
    public BookDTO save(CreateBookRequestDTO requestDTO) {
        Book book = new Book();
        book.setAuthor(requestDTO.getAuthor());
        book.setTitle(requestDTO.getTitle());
        book.setIsbn(requestDTO.getIsbn());
        book.setQuantity(requestDTO.getQuantity());
        Book newBook = bookRepository.save(book);
        return bookMapper.toDTO(newBook);
    }

    @Override
    public BookDTO update(String id, CreateBookRequestDTO requestDTO) {
        BookDTO bookDTO = findById(id);
        bookDTO.setAuthor(requestDTO.getAuthor());
        bookDTO.setTitle(requestDTO.getTitle());
        bookDTO.setIsbn(requestDTO.getIsbn());
        bookDTO.setQuantity(requestDTO.getQuantity());
        Book updatedBook = bookRepository.save(bookMapper.toEntity(bookDTO));
        return bookMapper.toDTO(updatedBook);
    }

    @Override
    public void delete(String id) {
        BookDTO bookDTO = findById(id);
        UUID bookId = UUID.fromString(bookDTO.getId());
        bookRepository.deleteById(bookId);
    }
}

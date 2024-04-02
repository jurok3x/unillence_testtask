package com.ykotsiuba.bookstore.service.impl;

import com.google.protobuf.Any;
import com.google.rpc.Code;
import com.google.rpc.ErrorInfo;
import com.google.rpc.Status;
import com.ykotsiuba.bookstore.BookOuterClass;
import com.ykotsiuba.bookstore.BookServiceGrpc;
import com.ykotsiuba.bookstore.dto.BookDTO;
import com.ykotsiuba.bookstore.dto.CreateBookRequestDTO;
import com.ykotsiuba.bookstore.mapper.BookProtoMapper;
import com.ykotsiuba.bookstore.service.BookService;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class BookProtoService extends BookServiceGrpc.BookServiceImplBase {

    private static final String DELETED_MESSAGE = "Book deleted.";
    private static final String DOMAIN = "com.ykotsiuba";
    private static final String ERROR_REASON = "Invalid parameter.";

    private final BookService bookService;

    private final BookProtoMapper bookMapper;

    @Override
    public void readBook(BookOuterClass.ReadBookRequest request, StreamObserver<BookOuterClass.Book> responseObserver) {
        try {
            String id = request.getId();
            BookDTO bookDTO = bookService.findById(id);
            BookOuterClass.Book response = bookMapper.toProto(bookDTO);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (EntityNotFoundException ex) {
            Status status = prepareErrorStatus(ex.getMessage(), Code.NOT_FOUND);
            responseObserver.onError(StatusProto.toStatusRuntimeException(status));
        }
    }

    @Override
    public void readALLBooks(BookOuterClass.Empty request, StreamObserver<BookOuterClass.BookList> responseObserver) {
        List<BookDTO> books = bookService.findAll();
        List<BookOuterClass.Book> responseList = books.stream().map(bookMapper::toProto).toList();
        BookOuterClass.BookList response = BookOuterClass.BookList.newBuilder()
                .addAllBooks(responseList).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void createBook(BookOuterClass.CreateBookRequest request, StreamObserver<BookOuterClass.Book> responseObserver) {
        try {
            CreateBookRequestDTO requestDTO = bookMapper.toCreateRequestDTO(request);
            BookDTO savedBook = bookService.save(requestDTO);
            BookOuterClass.Book response = bookMapper.toProto(savedBook);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (ConstraintViolationException ex) {
            Status status = prepareErrorStatus(ex.getMessage(), Code.INVALID_ARGUMENT);
            responseObserver.onError(StatusProto.toStatusRuntimeException(status));
        }
    }

    @Override
    public void deleteBook(BookOuterClass.DeleteBookRequest request, StreamObserver<BookOuterClass.DeleteBookResponse> responseObserver) {
        try {
            String id = request.getId();
            bookService.delete(id);
            BookOuterClass.DeleteBookResponse response = BookOuterClass.DeleteBookResponse.newBuilder()
                    .setMessage(DELETED_MESSAGE)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (EntityNotFoundException ex) {
            Status status = prepareErrorStatus(ex.getMessage(), Code.NOT_FOUND);
            responseObserver.onError(StatusProto.toStatusRuntimeException(status));
        }
    }

    @Override
    public void updateBook(BookOuterClass.UpdateBookRequest request, StreamObserver<BookOuterClass.Book> responseObserver) {
        try {
            String id = request.getId();
            CreateBookRequestDTO requestDTO = bookMapper.toCreateRequestDTO(request.getBook());
            BookDTO updatedBook = bookService.update(id, requestDTO);
            BookOuterClass.Book response = bookMapper.toProto(updatedBook);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (EntityNotFoundException ex) {
            Status status = prepareErrorStatus(ex.getMessage(), Code.NOT_FOUND);
            responseObserver.onError(StatusProto.toStatusRuntimeException(status));
        } catch (ConstraintViolationException ex) {
            Status status = prepareErrorStatus(ex.getMessage(), Code.INVALID_ARGUMENT);
            responseObserver.onError(StatusProto.toStatusRuntimeException(status));
        }
    }

    private Status prepareErrorStatus(String message, Code code) {
        return Status.newBuilder()
                .setCode(code.getNumber())
                .setMessage(message)
                .addDetails(Any.pack(ErrorInfo.newBuilder()
                        .setDomain(DOMAIN)
                        .setReason(ERROR_REASON)
                        .build()
                )).build();
    }
}

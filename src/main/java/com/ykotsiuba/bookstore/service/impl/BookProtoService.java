package com.ykotsiuba.bookstore.service.impl;

import com.google.protobuf.Any;
import com.google.rpc.Code;
import com.google.rpc.ErrorInfo;
import com.google.rpc.Status;
import com.ykotsiuba.bookstore.BookOuterClass;
import com.ykotsiuba.bookstore.BookServiceGrpc;
import com.ykotsiuba.bookstore.dto.CreateBookRequestDTO;
import com.ykotsiuba.bookstore.mapper.BookProtoMapper;
import com.ykotsiuba.bookstore.service.BookService;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

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
        String id = request.getId();
        bookService.findById(id)
                .map(bookMapper::toProto)
                .subscribe(responseObserver::onNext, error -> {
                    Status status = prepareErrorStatus(error.getMessage(), Code.NOT_FOUND);
                    responseObserver.onError(StatusProto.toStatusRuntimeException(status));
                }, responseObserver::onCompleted);
    }

    @Override
    public void readALLBooks(BookOuterClass.Empty request, StreamObserver<BookOuterClass.BookList> responseObserver) {
        bookService.findAll()
                .map(bookMapper::toProto)
                .collectList()
                .subscribe( bookList -> {
                    BookOuterClass.BookList bookListResponse = BookOuterClass.BookList.newBuilder()
                            .addAllBooks(bookList)
                            .build();
                    responseObserver.onNext(bookListResponse);
                }, error -> {
                    Status status = prepareErrorStatus(error.getMessage(), Code.INTERNAL);
                    responseObserver.onError(StatusProto.toStatusRuntimeException(status));
                }, responseObserver::onCompleted);
    }

    @Override
    public void createBook(BookOuterClass.CreateBookRequest request, StreamObserver<BookOuterClass.Book> responseObserver) {
        bookService.save(bookMapper.toCreateRequestDTO(request))
                .map(bookMapper::toProto)
                .subscribe(responseObserver::onNext, error -> {
                    Status status = prepareErrorStatus(error.getMessage(), Code.INVALID_ARGUMENT);
                    responseObserver.onError(StatusProto.toStatusRuntimeException(status));
                }, responseObserver::onCompleted);
    }

    @Override
    public void deleteBook(BookOuterClass.DeleteBookRequest request, StreamObserver<BookOuterClass.DeleteBookResponse> responseObserver) {
        String id = request.getId();
        bookService.delete(id)
                .doOnSuccess(action -> {
                    BookOuterClass.DeleteBookResponse response = BookOuterClass.DeleteBookResponse.newBuilder()
                            .setMessage(DELETED_MESSAGE)
                            .build();
                    responseObserver.onNext(response);
                })
                .subscribe(
                        action -> {},
                        error -> {
                            Status status = prepareErrorStatus(error.getMessage(), Code.NOT_FOUND);
                            responseObserver.onError(StatusProto.toStatusRuntimeException(status));
                        }, responseObserver::onCompleted
                );
    }

    @Override
    public void updateBook(BookOuterClass.UpdateBookRequest request, StreamObserver<BookOuterClass.Book> responseObserver) {
        String id = request.getId();
        CreateBookRequestDTO requestDTO = bookMapper.toCreateRequestDTO(request.getBook());
        bookService.update(id, requestDTO)
                .map(bookMapper::toProto)
                .subscribe(responseObserver::onNext,
                        error -> {
                                Code code = error.getClass() == EntityNotFoundException.class ? Code.NOT_FOUND : Code.INVALID_ARGUMENT;
                                Status status = prepareErrorStatus(error.getMessage(), code);
                                responseObserver.onError(StatusProto.toStatusRuntimeException(status));
                            },
                        responseObserver::onCompleted);
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

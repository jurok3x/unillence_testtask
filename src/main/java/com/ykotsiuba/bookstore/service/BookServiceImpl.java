package com.ykotsiuba.bookstore.service;

import com.google.protobuf.Any;
import com.google.rpc.Code;
import com.google.rpc.ErrorInfo;
import com.google.rpc.Status;
import com.ykotsiuba.bookstore.BookOuterClass;
import com.ykotsiuba.bookstore.BookServiceGrpc;
import com.ykotsiuba.bookstore.dto.CreateBookRequestDTO;
import com.ykotsiuba.bookstore.entity.Book;
import com.ykotsiuba.bookstore.mapper.BookMapper;
import com.ykotsiuba.bookstore.repository.BookRepository;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Optional;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class BookServiceImpl extends BookServiceGrpc.BookServiceImplBase {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public void readBook(BookOuterClass.ReadBookRequest request, StreamObserver<BookOuterClass.Book> responseObserver) {
        String id = request.getId();
        Optional<Book> optionalBook = bookRepository.findById(UUID.fromString(id));
        Optional<BookOuterClass.Book> bookResponseOptional = optionalBook.map(bookMapper::toDTO).map(bookMapper::toProto);
        bookResponseOptional.ifPresentOrElse(
                response -> {
                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                },
                () -> {
                    Status status = Status.newBuilder()
                            .setCode(Code.NOT_FOUND.getNumber())
                            .setMessage("Book not found")
                            .addDetails(Any.pack(ErrorInfo.newBuilder()
                                    .setDomain("com.ykotsiuba")
                                    .setReason("Invalid id")
                                    .build()
                            )).build();
                    responseObserver.onError(StatusProto.toStatusRuntimeException(status));
                }
        );
    }

    @Override
    public void createBook(BookOuterClass.CreateBookRequest request, StreamObserver<BookOuterClass.Book> responseObserver) {
        CreateBookRequestDTO requestDTO = bookMapper.toCreateRequestDTO(request);
        Book book = bookMapper.toRequestEntity(requestDTO);
        Book savedBook = bookRepository.save(book);
        responseObserver.onNext(bookMapper.toProto(bookMapper.toDTO(savedBook)));
        responseObserver.onCompleted();
    }

    @Override
    public void deleteBook(BookOuterClass.DeleteBookRequest request, StreamObserver<BookOuterClass.DeleteBookResponse> responseObserver) {

    }

    @Override
    public void updateBook(BookOuterClass.UpdateBookRequest request, StreamObserver<BookOuterClass.Book> responseObserver) {

    }
}

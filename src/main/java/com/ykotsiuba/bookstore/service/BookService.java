package com.ykotsiuba.bookstore.service;

import com.ykotsiuba.bookstore.BookOuterClass;
import com.ykotsiuba.bookstore.BookServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class BookService extends BookServiceGrpc.BookServiceImplBase {

    @Override
    public void readBook(BookOuterClass.ReadBookRequest request, StreamObserver<BookOuterClass.ReadBookResponse> responseObserver) {

    }

    @Override
    public void createBook(BookOuterClass.CreateBookRequest request, StreamObserver<BookOuterClass.CreateBookResponse> responseObserver) {

    }

    @Override
    public void deleteBook(BookOuterClass.DeleteBookRequest request, StreamObserver<BookOuterClass.DeleteBookResponse> responseObserver) {

    }

    @Override
    public void updateBook(BookOuterClass.UpdateBookRequest request, StreamObserver<BookOuterClass.UpdateBookResponse> responseObserver) {

    }
}

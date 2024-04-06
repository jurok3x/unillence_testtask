package com.ykotsiuba.bookstore.mapper;

import com.ykotsiuba.bookstore.BookOuterClass;
import com.ykotsiuba.bookstore.dto.BookDTO;
import com.ykotsiuba.bookstore.dto.CreateBookRequestDTO;
import com.ykotsiuba.bookstore.dto.UpdateBookRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Optional;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface BookProtoMapper {
    BookOuterClass.Book toProto(BookDTO bookDTO);
    CreateBookRequestDTO toCreateRequestDTO(BookOuterClass.CreateBookRequest request);

    @Mapping(target = "title",  expression = "java(mapTitle(request))")
    @Mapping(target = "author",  expression = "java(mapAuthor(request))")
    @Mapping(target = "isbn",  expression = "java(mapIsbn(request))")
    @Mapping(target = "quantity",  expression = "java(mapQuantity(request))")
    UpdateBookRequestDTO toUpdateRequestDTO(BookOuterClass.UpdateBookRequest request);

    default Optional<String> mapTitle(BookOuterClass.UpdateBookRequest request) {
        if (request.hasTitle()) {
            return Optional.of(request.getTitle());
        } else {
            return Optional.empty();
        }
    }

    default Optional<String> mapAuthor(BookOuterClass.UpdateBookRequest request) {
        if (request.hasAuthor()) {
            return Optional.of(request.getAuthor());
        } else {
            return Optional.empty();
        }
    }

    default Optional<String> mapIsbn(BookOuterClass.UpdateBookRequest request) {
        if (request.hasIsbn()) {
            return Optional.of(request.getIsbn());
        } else {
            return Optional.empty();
        }
    }

    default Optional<Integer> mapQuantity(BookOuterClass.UpdateBookRequest request) {
        if (request.hasQuantity()) {
            return Optional.of(request.getQuantity());
        } else {
            return Optional.empty();
        }
    }
}

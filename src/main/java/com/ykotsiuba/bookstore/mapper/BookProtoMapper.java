package com.ykotsiuba.bookstore.mapper;

import com.ykotsiuba.bookstore.BookOuterClass;
import com.ykotsiuba.bookstore.dto.BookDTO;
import com.ykotsiuba.bookstore.dto.CreateBookRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface BookProtoMapper {
    BookOuterClass.Book toProto(BookDTO bookDTO);
    CreateBookRequestDTO toCreateRequestDTO(BookOuterClass.CreateBookRequest request);
}

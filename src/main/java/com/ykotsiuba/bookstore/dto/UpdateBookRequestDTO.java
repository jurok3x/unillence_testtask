package com.ykotsiuba.bookstore.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Optional;

@Builder
@Data
public class UpdateBookRequestDTO {
    private Optional<String> title;
    private Optional<String> author;
    private Optional<String> isbn;
    private Optional<Integer> quantity;
}

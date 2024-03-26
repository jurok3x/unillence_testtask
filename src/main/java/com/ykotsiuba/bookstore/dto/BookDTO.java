package com.ykotsiuba.bookstore.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BookDTO {
    private String id;
    private String title;
    private String author;
    private String isbn;
    private Integer quantity;
}

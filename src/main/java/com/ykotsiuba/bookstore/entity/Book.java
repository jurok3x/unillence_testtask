package com.ykotsiuba.bookstore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "title", columnDefinition = "VARCHAR(255)")
    @NotBlank(message = "Title should not be blank.")
    private String title;

    @Column(name = "author", columnDefinition = "VARCHAR(255)")
    @NotBlank(message = "Author should not be blank.")
    private String author;

    @Column(name = "isbn", columnDefinition = "VARCHAR(20)")
    @NotBlank(message = "ISBN should not be blank.")
    private String isbn;

    @Column(name = "quantity", columnDefinition = "INT")
    @PositiveOrZero(message = "Invalid quantity.")
    private Integer quantity;
}

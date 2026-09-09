package com.tanco.dto;

//id int auto_increment primary key,
//title varchar(255) not null,
//author varchar(255) not null,
//publisher varchar(255) not null,
//publication_year int,
//isbn  varchar(13),
//available boolean default true

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Book {
    private int id;
    private String title;
    private String author;
    private String publisher;
    private int publicationYear;
    private String isbn;
    private boolean available;

    public Book(String title, String author, String publisher, int publicationYear, String isbn, boolean available) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.isbn = isbn;
        this.available = available;
    }
}

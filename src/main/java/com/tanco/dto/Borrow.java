package com.tanco.dto;

//id int auto_increment primary key,
//book_id int,
//student_id int,
//borrow_date date not null,
//return_date date,

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Borrow {
    private int id;
    private int bookId;
    private int studentId;
    private LocalDate borrowDate;
    private LocalDate returnDate;

    private  String bookTitle;
}

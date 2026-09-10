package com.tanco.dto;

//id int auto_increment primary key,
//book_id int,
//student_id int,
//borrow_date date not null,
//return_date date,

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
// 도서 대출 기록을 담는 DTO
// DTO는 테이블과 꼭 1:1로 맞출 필요가 없습니다
// SQL 실행 결과를 담는 그릇 이므로 JOIN으로 가져와 컬럼 결과도 담을 수 있습니다
public class Borrow {
    private int id;
    private int bookId;
    private int studentId;
    private String bookTitle;
    private String studentName;
    private LocalDate borrowDate;
    private LocalDate returnDate;
}

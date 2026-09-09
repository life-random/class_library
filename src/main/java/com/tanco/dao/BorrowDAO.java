package com.tanco.dao;

import com.tanco.dto.Borrow;
import com.tanco.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 도서 대출/반납 관련 SQL 실행은 DAO
 */
public class BorrowDAO {

    // 1.1 현재 대출 중인 도서 목록 조회
    // 1.2 JOIN 해서 도서 이름까지 출력 받기
    public List<Borrow> getBorrowedBooks(){
        List<Borrow> borrowList = new ArrayList<>();
        // 1. Join 없이 코드 완성
        String sql = """
                select r.*, b.title
                from borrows r left join books b
                on r.book_id = b.id;
                """;

        try (Connection connection = DatabaseUtil.getConnection()) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()){
                    borrowList.add(createBorrow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // 2. Join 결과 출력
        return borrowList;
    }

    // 2. 도서 대출 기능(트랜잭션)
    // 2.1 - 대상 도서 대출 가능 여부 - select
    // 2.2 - 도서 대출 기록 - insert
    // 3. 도서 반납 처리 (트랜잭션)
    // 3.1 대출 기록 확인 = select
    // 3.2 반납 기록 등록 = UPDATE

    private static Borrow createBorrow(ResultSet rs) throws SQLException {
        Borrow borrow = new Borrow();
        borrow.setId(rs.getInt("id"));
        borrow.setBookId(rs.getInt("book_id"));
        borrow.setBookTitle(rs.getString("title"));
        borrow.setStudentId(rs.getInt("student_id"));
        borrow.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
        Date returnDate = rs.getDate("return_date");

        if (returnDate != null) {
            borrow.setReturnDate(returnDate.toLocalDate());
        } else {
            borrow.setReturnDate(null);
        }

        return borrow;
    }
}

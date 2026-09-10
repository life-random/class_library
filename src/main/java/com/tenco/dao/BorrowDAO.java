package com.tenco.dao;

import com.tenco.dto.Borrow;
import com.tenco.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 도서 대출/반납 관련 SQL 실행은 DAO
 */
public class BorrowDAO {

    // 현재 대출 중인 도서 목록 조회
    // 사용자에게 보여주려면 도서 제목과
    public List<Borrow> getBorrowedBooks(){
        List<Borrow> borrowList = new ArrayList<>();
        // 1. Join 없이 코드 완성

        String sql = """
                select b.id, b.book_id, bk.title, b.student_id, s.name, b.borrow_date, b.return_date
                from borrows b
                join books bk on b.book_id = bk.id
                join students s on b.student_id = s.id
                where b.return_date is null
                order by b.borrow_date desc
                """;

        try (Connection connection = DatabaseUtil.getConnection()) {

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()){
                    borrowList.add(Borrow.builder()
                                    .id(rs.getInt("id"))
                                    .bookId(rs.getInt("book_id"))
                                    .bookTitle(rs.getString("title"))
                                    .studentId(rs.getInt("student_id"))
                                    .studentName(rs.getString("name"))
                                    .borrowDate(rs.getDate("borrow_date").toLocalDate())
                            .build());
                }
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // 2. Join 결과 출력
        return borrowList;
    }

    // 2. 도서 대출 기능(트랜잭션)
    // [처리 순서]
    // 1. DB 연결을 얻고 자동 커밋을 끈다 (트랜잭션 시작)
    // 2. 도서가 존재하고 대출 가능 상태를 확인 -- select
    // 3. borrows 테이블에 대출 기록 -- insert
    // 4. books 테이블에 available을 false로 변경 -- update
    // 5. 2 - 4번이 모두 성공하면 commit, 하나라도 실패하면 rollback
    // 6. 자동 커밋을 원래대로 되돌리고 연결을 닫는다
    public void borrowBook(int bookId, int studentId) throws SQLException {
        Connection conn = null;
        // try-with-resources로 선언하지 않은 이유
        // catch 블록에서 rollback을 호출할려면 conn 변수가  catch 안에서도 보여야 합니다
        // 그래서 try 바깥에 선언하고 finally에서 직접 닫습니다
        try {
            // 1. 트랜잭션 시작
            conn =DatabaseUtil.getConnection();
            // 기본값 autoCommit = true 이고, 이 상태에서느 SQL 한줄 한줄 마다 즉시 확정(반영)이 됩니다
            // 이 값 false 로 변경하면 commit()을 호출하기 전까지 보든 변경 상항이 임시 상태가 남습니다
            conn.setAutoCommit(false);
            // 대출 가능 여부 확인
            String checksql = """
                    select available
                    from books
                    where id =?;
                    """;
            try (PreparedStatement checkPstmt = conn.prepareStatement(checksql)) {
                checkPstmt.setInt(1, bookId);
                try (ResultSet rs = checkPstmt.executeQuery()) {
                    if(!rs.next()){
                        throw new SQLException("존재하지 않는 도서입니다 ID : " + bookId);
                    }
                    if (!rs.getBoolean("available")){
                        throw new SQLException("현재 대출 주인 도서입니다. 반납 후 이용가능합니다");
                    }
                }
            }
            // 3. 코드가 여기까지 내려온다면 대출 가능한 bookID 이다 -> 대출 가능이다
            String borrowSql = """
                    insert into borrows (book_id, student_id, borrow_date)
                    values (?, ?, ?)
                    """;
            int rows;
            try (PreparedStatement borrowPstmt = conn.prepareStatement(borrowSql)) {
                borrowPstmt.setInt(1, bookId);
                borrowPstmt.setInt(2, studentId);
                // java.time.localDate를 JDBC가 이해하는 java.sql.Date로 변환 해주어야 한다
                borrowPstmt.setDate(3, Date.valueOf(LocalDate.now()));
                rows = borrowPstmt.executeUpdate();
            }
            if (rows < 0){
                throw new SQLException("적용된 기록이 없습니다");
            }
            // 4. 도서 상태 변경 (대출 불가로 해당도서 처리)
            String updateSql = """
                    update books set available = False
                    where id = ?
                    """;
            try (PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {
                updatePstmt.setInt(1, bookId);
                updatePstmt.executeUpdate();
            }
            // 5. 여기까지 모두 성공했다면 확정
            conn.commit();

        } catch (SQLException e) {
            // 5. 하나라도 실패시 rollback 처리
            if (conn != null){
                conn.rollback();
            }
            throw new SQLException(e);
        } finally {
            if (conn != null){
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }




    // 3. 도서 반납 처리 (트랜잭션)
    // [처리 순서]
    // 1. DB 연결을 얻고 자동 커밋을 끈다 (트랜잭션 시작)
    // 2. 이 학생이 이 도서를 빌린 뒤 아직 반납하지 않은 기록이 있는지 먼저 확인 -- select
    // 3. 찾은 대출 기록을 return_date을 오늘 날짜로 update 한다 -- update
    // 4. books 테이블에 available을 true로 변경 -- update
    // 5. 2 - 4번이 모두 성공하면 commit, 하나라도 실패하면 rollback
    // 6. 자동 커밋을 원래대로 되돌리고 연결을 닫는다
    public void returnBook(int bookId, int studentId) throws SQLException {
        Connection conn = null;

        try {
            conn = DatabaseUtil.getConnection();

            conn.setAutoCommit(false);

            String checkSql = """
                    select *
                    from borrows
                    where book_id = ?
                    and student_id = ?
                    and return_date is null
                    """;


            int borrowId;
            try (PreparedStatement checkPstmt = conn.prepareStatement(checkSql)) {
                checkPstmt.setInt(1, bookId);
                checkPstmt.setInt(2, studentId);
                try (ResultSet rs = checkPstmt.executeQuery()) {
                    if (!rs.next()){
                        throw new SQLException("해당 대출 기록이 없거나 이미 반납되었습니다 book_ID : %d, studnet_ID : %d".formatted(bookId, studentId));
                    }
                    borrowId = rs.getInt("id");
                    boolean isBorrowBook = false;
                }
            }

            String updateBorrowSql = """
                    update borrows 
                    set return_date = ?
                    where id = ?
                    """;

            try (PreparedStatement updatePstmt = conn.prepareStatement(updateBorrowSql)) {
                updatePstmt.setDate(1, Date.valueOf(LocalDate.now()));
                updatePstmt.setInt(2, borrowId);
                updatePstmt.executeUpdate();
            }

            String updateBookSql = """
                    update books 
                    set available = TRUE
                    where id = ?
                    """;

            try (PreparedStatement updateBookPstmt = conn.prepareStatement(updateBookSql)) {
                updateBookPstmt.setInt(1, bookId);
                updateBookPstmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null){
                conn.rollback();
            }
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
}

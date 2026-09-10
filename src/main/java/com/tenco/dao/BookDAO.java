package com.tenco.dao;

import com.tenco.dto.Book;
import com.tenco.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    // 도서 전체 검색 기능
    public List<Book> getAllBooks(){
        List<Book> bookList = new ArrayList<>();
        String sql = """
                select * 
                from books
                order by id
                """;

        try (Connection connection = DatabaseUtil.getConnection()) {

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    // 자료구조에 생성된 Student 객체를 하나씩 추가 함
                    bookList.add(createBook(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return bookList;
    }


    // 제목을 도서 검색 기능
    public List<Book> searchBooksByTitle(String title){
        List<Book> bookList = new ArrayList<>();
        String sql = """
                select *
                from books
                where title like ?;
                """;

        try (Connection connection = DatabaseUtil.getConnection()) {

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, '%'+title+'%');
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    // 자료구조에 생성된 Student 객체를 하나씩 추가 함
                    bookList.add(createBook(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return bookList;
    }

    // 도서 등록 기능
    public int addBook(Book book){
        int rows = 0;
        String sql = """
                INSERT INTO books (title, author, publisher, publication_year, isbn)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseUtil.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, book.getTitle());
                pstmt.setString(2, book.getAuthor());
                pstmt.setString(3, book.getPublisher());
                pstmt.setInt(4, book.getPublicationYear());
                pstmt.setString(5, book.getIsbn());
                // INSERT, UPDATE, DELETE에 사용해야 함
                rows = pstmt.executeUpdate();
                System.out.println(rows + "행이 추가 되었습니다");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rows;
    }

    private static Book createBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setPublisher(rs.getString("publisher"));
        book.setPublicationYear(rs.getInt("publication_year"));
        book.setIsbn(rs.getString("isbn"));
        book.setAvailable(rs.getBoolean("available"));
        return book;
    }
}

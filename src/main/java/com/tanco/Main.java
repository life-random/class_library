package com.tanco;

import com.tanco.dao.BookDAO;
import com.tanco.dao.BorrowDAO;
import com.tanco.dao.StudentDAO;
import com.tanco.dto.Book;
import com.tanco.dto.Borrow;
import com.tanco.dto.Student;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {

        BorrowDAO borrowDAO = new BorrowDAO();

        // getBorrowedBooks
//        List<Borrow> borrowList = borrowDAO.getBorrowedBooks();
//        for (Borrow borrow : borrowList){
//            System.out.println(borrow);
//        }

//        borrowDAO.borrowBook(1, 1);

        borrowDAO.returnBook(1, 1);
    }
}
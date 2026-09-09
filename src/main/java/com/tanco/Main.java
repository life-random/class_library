package com.tanco;

import com.tanco.dao.BookDAO;
import com.tanco.dao.BorrowDAO;
import com.tanco.dao.StudentDAO;
import com.tanco.dto.Book;
import com.tanco.dto.Borrow;
import com.tanco.dto.Student;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        StudentDAO studentDAO = new StudentDAO();
        // addStudent 테스트
//        Student student = new Student("티모", "90230005");
//        studentDAO.addStudent(student);

        // getAllStudent 테스트
//        List<Student> studentList = studentDAO.getAllStudent();
//        for (Student student : studentList){
//            System.out.println(student);
//        }

        // login
//        Student student = studentDAO.getStudentByStudentId("20230002");
//        if (student != null){
//            System.out.println(student);
//        } else {
//            System.out.println("존재하지 않는 학생입니다");
//        }

        BookDAO bookDAO = new BookDAO();
        // getAllBooks
//        List<Book> bookList = bookDAO.getAllBooks();
//        for (Book book : bookList){
//            System.out.println(book);
//        }

        // searchBooksByTitle
//        List<Book> bookList = bookDAO.searchBooksByTitle("입문");
//        for (Book book : bookList){
//            System.out.println(book);
//        }

        //addBook
//        Book book = new Book("테스트책2", "저자", "한빛미디어", 2026, "9788968481239");

//        Book book = Book.builder()
//                .title("테스트책2")
//                .author("저자")
//                .publisher("한빛미디어")
//                .publicationYear(2026)
//                .isbn("9788968481239")
//                .build();

//        bookDAO.addBook(book);

        BorrowDAO borrowDAO = new BorrowDAO();

        // getBorrowedBooks
        List<Borrow> borrowList = borrowDAO.getBorrowedBooks();
        for (Borrow borrow : borrowList){
            System.out.println(borrow);
        }
    }
}
package com.bookstore.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookstore.bookstore.entity.Book;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    Optional<Book> findByNameAndAuthor(String name, String author);
    List<Book> findByNameContainingIgnoreCase(String name);
    List<Book> findByAuthorContainingIgnoreCase(String author);
    List<Book> findByNameContainingIgnoreCaseAndAuthorContainingIgnoreCase(String name, String author);
}

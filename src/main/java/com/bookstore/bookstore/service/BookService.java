package com.bookstore.bookstore.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.bookstore.bookstore.entity.Book;
import com.bookstore.bookstore.repository.BookRepository;

@Service
public class BookService {
	@Autowired
	BookRepository bookRepo;

	@Transactional
	public Book save(Book b) {
		Optional<Book> existingBook = bookRepo.findByNameAndAuthor(b.getName(), b.getAuthor());

		if (existingBook.isPresent()) {
			throw new EntityNotFoundException("A book with the same name and author already exists");
		}

		try {
			return bookRepo.save(b);
		} catch (DataIntegrityViolationException e) {
			throw new RuntimeException("Unable to save the book: A book with the same name and author already exists.");}
	}

	public List<Book> searchBooks(Integer id, String name, String author) {
		if (id != null) {
			Optional<Book> book = bookRepo.findById(id);
			return book.map(Collections::singletonList).orElse(Collections.emptyList());
		} else if (name != null && author != null) {
			return bookRepo.findByNameContainingIgnoreCaseAndAuthorContainingIgnoreCase(name, author);
		} else if (name != null) {
			return bookRepo.findByNameContainingIgnoreCase(name);
		} else if (author != null) {
			return bookRepo.findByAuthorContainingIgnoreCase(author);
		} else {
			return bookRepo.findAll();
		}
	}

	public Book updateBook(Book updatedBook){
		Optional<Book> existingBook=bookRepo.findById(updatedBook.getId());
		if(existingBook.isPresent()){
			Book book = existingBook.get();
			book.setName(updatedBook.getName());
			book.setAuthor(updatedBook.getAuthor());
			book.setPrice(updatedBook.getPrice());

			return bookRepo.save(book);
		}
		return null;
	}

	public boolean deleteBook(int id){
		Optional<Book> book = bookRepo.findById(id);
		if(book.isPresent()){
			bookRepo.deleteById(id);
			return true;
		}
		return false;
	}









	public Book getBookById(int id) {
		return bookRepo.findById(id).get();
	}

	public void deleteBookById(int id) {
		bookRepo.deleteById(id);
	}
}

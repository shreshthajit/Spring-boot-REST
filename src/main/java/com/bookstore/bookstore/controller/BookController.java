package com.bookstore.bookstore.controller;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.bookstore.bookstore.entity.Book;
import com.bookstore.bookstore.service.BookService;

@RestController
public class BookController {
	@Autowired
	BookService bookService;


@PostMapping("/save")
public ResponseEntity<?> addBook(@Valid @RequestBody Book book, BindingResult result) {
	if (result.hasErrors()) {
		List<String> errors = result.getAllErrors()
				.stream()
				.map(error -> error.getDefaultMessage())
				.toList();
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}
	try {
		Book savedBook = bookService.save(book);

		return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
	} catch (EntityNotFoundException e) {
		return new ResponseEntity<>("Unable to save the book: A book with the same name and author already exists.", HttpStatus.BAD_REQUEST);
	} catch (Exception e) {
		return new ResponseEntity<>("Unable to save the book: An internal server error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
	}
}

	@GetMapping("/books")
	public ResponseEntity<?> getBooks(
			@RequestParam(required = false) Integer id,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String author) {

		try {
			List<Book> books = bookService.searchBooks(id, name, author);

			if (books.isEmpty()) {
				String message = "No books found with the provided search criteria.";
				if (id != null && name==null && author==null) {
					message = "No book found with ID: " + id;
				} else if (name != null && author!=null) {
					message = "No book found with the name and the author";
				}
				else if (name != null) {
					message = "No book found with the name: " + name;
				}
				else if (author != null) {
					message = "No book found with the author: " + author;
				}
				return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(books, HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<>("An error occurred while searching for books: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@PutMapping("/update-book")
	public ResponseEntity<?> updateBook(@Valid @RequestBody Book updatedBook, BindingResult result) {
		if (result.hasErrors()) {
			List<String> errors = result.getAllErrors()
					.stream()
					.map(error -> error.getDefaultMessage())
					.toList();
			return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
		}
		if (updatedBook.getId() == 0) {
			return new ResponseEntity<>("Book ID is required for updating.", HttpStatus.BAD_REQUEST);
		}

		try {
			if (updatedBook.getId() == 0) {
				return new ResponseEntity<>("Book ID is required for updating.", HttpStatus.BAD_REQUEST);
			}
			Book book = bookService.updateBook(updatedBook);

			if (book == null) {
				return new ResponseEntity<>("Book with ID " + updatedBook.getId() + " not found.", HttpStatus.NOT_FOUND);
			}

			return new ResponseEntity<>(book, HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<>("An error occurred while updating the book: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/delete-book")
	public ResponseEntity<String> deleteBook(@RequestParam int id) {
		try {
			if (id <= 0) {
				return new ResponseEntity<>("Invalid book ID provided.", HttpStatus.BAD_REQUEST);
			}

			boolean isDeleted = bookService.deleteBook(id);
			return isDeleted
					? new ResponseEntity<>("Book deleted successfully.", HttpStatus.OK)
					: new ResponseEntity<>("Book with ID " + id + " not found.", HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<>("An error occurred while deleting the book: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


























}

package com.example.demo.service;

import com.example.demo.entity.Book;
import com.example.demo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    // Create a new book
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    // Get all books
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Get book by ID
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    // Update book
    public Book updateBook(Long id, Book bookDetails) {
        return bookRepository.findById(id)
            .map(book -> {
                book.setTitle(bookDetails.getTitle());
                book.setAuthor(bookDetails.getAuthor());
                book.setGenre(bookDetails.getGenre());
                book.setPrice(bookDetails.getPrice());
                book.setPages(bookDetails.getPages());
                book.setPublicationDate(bookDetails.getPublicationDate());
                book.setDescription(bookDetails.getDescription());
                book.setIsAvailable(bookDetails.getIsAvailable());
                return bookRepository.save(book);
            })
            .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }

    // Delete book
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    // Get available books
    public List<Book> getAvailableBooks() {
        return bookRepository.findByIsAvailableTrue();
    }

    // Search books by title
    public List<Book> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    // Get books by author
    public List<Book> getBooksByAuthor(String author) {
        return bookRepository.findByAuthor(author);
    }

    // Get books by genre
    public List<Book> getBooksByGenre(String genre) {
        return bookRepository.findByGenre(genre);
    }

    // Get books by author and genre
    public List<Book> getBooksByAuthorAndGenre(String author, String genre) {
        return bookRepository.findByAuthorAndGenre(author, genre);
    }
}

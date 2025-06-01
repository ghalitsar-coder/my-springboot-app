package com.example.demo.repository;

import com.example.demo.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // Find books by author
    List<Book> findByAuthor(String author);
    
    // Find books by genre
    List<Book> findByGenre(String genre);
    
    // Find available books
    List<Book> findByIsAvailableTrue();
    
    // Find books by title containing (case insensitive)
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Book> findByTitleContainingIgnoreCase(@Param("title") String title);
    
    // Find books by author and genre
    List<Book> findByAuthorAndGenre(String author, String genre);
}

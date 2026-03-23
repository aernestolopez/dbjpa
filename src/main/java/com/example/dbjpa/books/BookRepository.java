package com.example.dbjpa.books;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>  {
//Spring analiza el nombre del metodo, entiende qué quieres buscar y genera el SQL

    List<Book> findByAuthor(String author);

    List<Book> findByYear(Integer year);

    List<Book> findByAuthorAndYear(String author, Integer year);

    //buscar por título que contenga una palabra
    List<Book> findByTitleContainingIgnoreCase(String title);
}

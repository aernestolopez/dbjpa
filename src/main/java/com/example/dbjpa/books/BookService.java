package com.example.dbjpa.books;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
public class BookService {
    @Autowired
    BookRepository bookRepository;

    private BookDTO MapperToDTO(Book book){
        return new BookDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getYear()
        );
    }

    private Book MapperToBook(BookDTO bookDTO){
        Book book = new Book();

        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setYear(bookDTO.getYear());
        return book;
    }

    public List<BookDTO> getAllBooks(){
        return bookRepository.findAll()
                .stream()
                .map(this::MapperToDTO)
                .collect(Collectors.toList());
    }

    public BookDTO getBook(Long id){
        Book foundBook = bookRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return MapperToDTO(foundBook);
    }

    public BookDTO save(BookDTO bookDTO){
        Book book = MapperToBook(bookDTO);
        Book savedBook = bookRepository.save(book);
        return MapperToDTO(savedBook);
    }

    public BookDTO updateBook(Long id, BookDTO bookDTO){
        Book existBook = bookRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        existBook.setTitle(bookDTO.getTitle());
        existBook.setYear(bookDTO.getYear());
        existBook.setAuthor(bookDTO.getAuthor());

        Book saveBook = bookRepository.save(existBook);

        return MapperToDTO(saveBook);
    }

    public void delete(Long id){
        if(!bookRepository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No se puede borrar al usuario porque el ID: " + id +" no existe");
        }
        bookRepository.deleteById(id);
    }

    //Existe otra forma de hacerlo, utilizando JpaSpecificationExecutor en BookRepository, este crea de forma dinamica la sentencia SQL
    // a ejecutar dependiendo de los filtros (author, year, title) que se añadan
    public List<BookDTO> searchBooks(String author, String title, Integer year) {
        List<Book> books;
        if(author != null && year != null) {
            books = bookRepository.findByAuthorAndYear(author, year);
        }
       else if (author != null) {
            books = bookRepository.findByAuthor(author);
        } else if (title != null) {
            books = bookRepository.findByTitleContainingIgnoreCase(title);
        } else if (year != null) {
            books = bookRepository.findByYear(year);

        } else {
            books = bookRepository.findAll();
        }
        return books.stream().map(this::MapperToDTO).collect(Collectors.toList());
    }

    public BookDTO patchBook(Long id, Map<String, Object> fields){
        Book existingBook = bookRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));

       fields.forEach((key, value)->{
           Field field = ReflectionUtils.findField(Book.class, key);

           if(field!=null){
               field.setAccessible(true);
               try{
                   ReflectionUtils.setField(field, existingBook, value);
               }catch (Exception e){
                   throw new RuntimeException("Error al asignar el campo: " + key);
               }
           }

       });

       return MapperToDTO(bookRepository.save(existingBook));

    }

}

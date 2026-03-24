package com.example.dbjpa.books;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    BookService bookService;

    @GetMapping
    private ResponseEntity<List<BookDTO>> getAllBooks(){
        List<BookDTO> bookDTOList = bookService.getAllBooks();

        return  ResponseEntity.status(HttpStatus.OK).body(bookDTOList);
    }

    @GetMapping("/{id}")
    private ResponseEntity<BookDTO> getBook(@PathVariable Long id){
        BookDTO bookDTO =  bookService.getBook(id);
        return ResponseEntity.status(HttpStatus.OK).body(bookDTO);
    }

    @PostMapping("/createBook")
    private ResponseEntity<BookDTO> createBook(@RequestBody BookDTO bookDTO){
        BookDTO saveBook = bookService.save(bookDTO);

        return  ResponseEntity.status(HttpStatus.CREATED).body(saveBook);

    }
    @DeleteMapping("/deleteBook/{id}")
    private ResponseEntity<Void> deleteBook(@PathVariable Long id){
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/updateBook/{id}")
    private ResponseEntity<BookDTO> updateBook(@PathVariable Long id, @RequestBody BookDTO newBookDTO){
        BookDTO updatedBook = bookService.updateBook(id, newBookDTO);

        return ResponseEntity.status(HttpStatus.OK).body(updatedBook);
    }

    @GetMapping("/search")
    private ResponseEntity<List<BookDTO>> search(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer year
    ){

        List<BookDTO> foundBooks = bookService.searchBooks(author, title, year);
       return ResponseEntity.status(HttpStatus.OK).body(foundBooks);
    }

    @PatchMapping("/patchBook/{id}")
    private ResponseEntity<BookDTO> patchBook(@PathVariable Long id, @RequestBody Map<String, Object> fields){
        BookDTO patchBook = bookService.patchBook(id, fields);

        return ResponseEntity.status(HttpStatus.OK).body(patchBook);

    }
}

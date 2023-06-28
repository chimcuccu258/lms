package com.example.be.service.impl;

import com.example.be.model.Author;
import com.example.be.model.Book;
import com.example.be.model.Category;
import com.example.be.payload.request.BookRequest;
import com.example.be.payload.response.BookResponse;
import com.example.be.payload.response.ListDataResponse;
import com.example.be.repository.AuthorRepository;
import com.example.be.repository.BookRepository;
import com.example.be.repository.CategoryRepository;
import com.example.be.service.BookService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {
  @Autowired
  BookRepository bookRepository;

  @Autowired
  CategoryRepository categoryRepository;

  @Autowired
  AuthorRepository authorRepository;

  @Override
  public ResponseEntity<Object> getAll() {
    try {
      List<Book> books = bookRepository.findAll();
      ListDataResponse<Object> listDataResponse = ListDataResponse.builder().message("OK").data(books).build();
      return ResponseEntity.ok(listDataResponse);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("An exception occurred from the server with exception = " + e);
    }
  }

  @Override
  public ResponseEntity<Object> addBook(@NonNull BookRequest bookRequest) {
    try {

      if (bookRequest.getBookName().isBlank()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Book name is required!");
      }
      if (bookRepository.existsByBookName(bookRequest.getBookName())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Book already exists!");
      }
      if (bookRequest.getInventory() < 1) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid!");
      }
      if (bookRequest.getPrice() < 0) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid!");
      }
      if (!validateBookName(bookRequest.getBookName())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid book name!");
      }
      if (!categoryRepository.existsById(bookRequest.getCtgId())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Category Id is not exist!");
      }
      if (!authorRepository.existsById(bookRequest.getAuthorId())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Author Id is not exist!");
      }

      Book book = new Book();
      Author author = authorRepository.getById(bookRequest.getAuthorId());

      book.setBookName(bookRequest.getBookName());
      book.setInventory(bookRequest.getInventory());
      book.setPrice(bookRequest.getPrice());
      book.setDescription(bookRequest.getDescription());

      book.setAuthor(author);
      author.getBooks().add(book);

      Category category = categoryRepository.getById(bookRequest.getCtgId());

      book.setCategory(category);
      category.getBooks().add(book);

      bookRepository.save(book);
      authorRepository.save(author);
      categoryRepository.save(category);

      BookResponse bookResponse = new BookResponse();
      bookResponse.setBookName(book.getBookName());
      bookResponse.setInventory(book.getInventory());
      bookResponse.setPrice(book.getPrice());
      bookResponse.setDescription(book.getDescription());
      bookResponse.setAuthorId(author.getId());
      bookResponse.setCtgId(category.getId());

      ListDataResponse<Object> listDataResponse = ListDataResponse.builder().message("OK").data(bookResponse).build();
      return ResponseEntity.ok(listDataResponse);

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("An exception occurred from the server with exception = " + e);
    }
  }

  public boolean validateBookName(String bookName) {
    String regex = "^[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỂẾưạảấầẩẫậắằẳẵặẹẻẽềểếỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ\\s\\W|_]+$";

    if (bookName.matches(regex)) {
      System.out.println(("Book name is valid"));
      return true;
    } else {
      System.out.println("Book name is invalid");
      return false;
    }
  }


}
package com.formation.book.mapper;
import com.formation.book.dto.*; import com.formation.book.model.Book;
public final class BookMapper {
    private BookMapper() { }
    public static BookResponse toResponse(Book b){return new BookResponse(b.getId(),b.getTitle(),b.getAuthor(),b.getIsbn(),b.getTotalCopies(),b.getAvailableCopies());}
}

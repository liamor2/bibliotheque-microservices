package com.formation.book.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Book {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @NotBlank @Column(nullable = false) private String title;
    @NotBlank @Column(nullable = false) private String author;
    @NotBlank @Column(unique = true, nullable = false) private String isbn;
    @NotNull @Min(1) private Integer totalCopies;
    @NotNull @Min(0) private Integer availableCopies;
    @Version private Long version;
    protected Book() { }
    public Book(String title, String author, String isbn, Integer totalCopies, Integer availableCopies) { this.title=title; this.author=author; this.isbn=isbn; this.totalCopies=totalCopies; this.availableCopies=availableCopies; }
    public Long getId(){return id;} public String getTitle(){return title;} public String getAuthor(){return author;} public String getIsbn(){return isbn;} public Integer getTotalCopies(){return totalCopies;} public Integer getAvailableCopies(){return availableCopies;} public Long getVersion(){return version;}
    public void setTitle(String v){title=v;} public void setAuthor(String v){author=v;} public void setIsbn(String v){isbn=v;} public void setTotalCopies(Integer v){totalCopies=v;} public void setAvailableCopies(Integer v){availableCopies=v;}
}

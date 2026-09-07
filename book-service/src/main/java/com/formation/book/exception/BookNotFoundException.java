package com.formation.book.exception; public class BookNotFoundException extends RuntimeException { public BookNotFoundException(Long id){super("Le livre "+id+" n'existe pas");} }

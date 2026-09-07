package com.formation.book.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public record BookRequest(@NotBlank String title, @NotBlank String author, @NotBlank String isbn, @NotNull @Min(1) Integer totalCopies, Integer availableCopies) { }

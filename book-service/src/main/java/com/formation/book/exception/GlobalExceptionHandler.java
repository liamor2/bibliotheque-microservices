package com.formation.book.exception;
import org.springframework.dao.DataIntegrityViolationException; import org.springframework.http.*; import org.springframework.orm.ObjectOptimisticLockingFailureException; import org.springframework.web.bind.MethodArgumentNotValidException; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestControllerAdvice public class GlobalExceptionHandler {
 @ExceptionHandler(BookNotFoundException.class) ResponseEntity<ApiError> notFound(BookNotFoundException e){return response(HttpStatus.NOT_FOUND,e.getMessage());}
 @ExceptionHandler(StockConflictException.class) ResponseEntity<ApiError> conflict(StockConflictException e){return response(HttpStatus.CONFLICT,e.getMessage());}
 @ExceptionHandler({ObjectOptimisticLockingFailureException.class,DataIntegrityViolationException.class}) ResponseEntity<ApiError> db(Exception e){return response(HttpStatus.CONFLICT,"Conflit de modification ou ISBN déjà utilisé");}
 @ExceptionHandler(IllegalArgumentException.class) ResponseEntity<ApiError> bad(IllegalArgumentException e){return response(HttpStatus.BAD_REQUEST,e.getMessage());}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ApiError> validation(MethodArgumentNotValidException e){Map<String,String> f=new LinkedHashMap<>();e.getBindingResult().getFieldErrors().forEach(x->f.putIfAbsent(x.getField(),x.getDefaultMessage()));return ResponseEntity.badRequest().body(new ApiError(400,"Bad Request","La requête contient des données invalides",f));}
 private ResponseEntity<ApiError> response(HttpStatus s,String m){return ResponseEntity.status(s).body(new ApiError(s.value(),s.getReasonPhrase(),m));}
}

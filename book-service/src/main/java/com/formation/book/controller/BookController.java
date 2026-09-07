package com.formation.book.controller;
import com.formation.book.dto.*; import com.formation.book.service.BookService; import jakarta.validation.Valid; import org.springframework.data.domain.*; import org.springframework.data.web.PageableDefault; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.support.ServletUriComponentsBuilder; import java.net.URI;
@RestController @RequestMapping("/api/books") public class BookController {
 private final BookService service; public BookController(BookService service){this.service=service;}
 @GetMapping public Page<BookResponse> findAll(@RequestParam(required=false) String title,@RequestParam(required=false) String author,@PageableDefault(size=20,sort="title") Pageable pageable){return service.findAll(title,author,pageable);}
 @GetMapping("/{id}") public BookResponse findById(@PathVariable Long id){return service.findById(id);}
 @PostMapping public ResponseEntity<BookResponse> create(@Valid @RequestBody BookRequest r){BookResponse b=service.create(r);URI u=ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(b.id()).toUri();return ResponseEntity.created(u).body(b);}
 @PutMapping("/{id}") public BookResponse update(@PathVariable Long id,@Valid @RequestBody BookRequest r){return service.update(id,r);}
 @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id){service.delete(id);return ResponseEntity.noContent().build();}
 @PatchMapping("/{id}/decrement-stock") public BookResponse decrement(@PathVariable Long id){return service.decrement(id);}
 @PatchMapping("/{id}/increment-stock") public BookResponse increment(@PathVariable Long id){return service.increment(id);}
}

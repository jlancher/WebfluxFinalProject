package com.bancolombia.training.customers.web;

import com.bancolombia.training.customers.catalog.CustomerCatalog;
import com.bancolombia.training.customers.model.CreateOrUpdateCustomerRequest;
import com.bancolombia.training.customers.model.Customer;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

  private final CustomerCatalog catalog;

  public CustomerController(CustomerCatalog catalog) {
    this.catalog = catalog;
  }

  @GetMapping
  public Flux<Customer> list() {
    return catalog.findAll();
  }

  @GetMapping("/{id}")
  public Mono<Customer> get(@PathVariable UUID id) {
    return catalog.findById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Customer> create(@Valid @RequestBody Mono<CreateOrUpdateCustomerRequest> request) {
    return request.flatMap(catalog::create);
  }

  @PutMapping("/{id}")
  public Mono<Customer> update(
      @PathVariable UUID id, @Valid @RequestBody Mono<CreateOrUpdateCustomerRequest> request) {
    return request.flatMap(body -> catalog.update(id, body));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> delete(@PathVariable UUID id) {
    return catalog.delete(id);
  }
}
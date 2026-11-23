package com.bancolombia.training.customers.catalog;

import com.bancolombia.training.customers.model.CreateOrUpdateCustomerRequest;
import com.bancolombia.training.customers.model.Customer;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CustomerCatalog {

  private final Map<UUID, Customer> customers = new ConcurrentHashMap<>();

  public Flux<Customer> findAll() {
    return Flux.defer(() -> Flux.fromIterable(customers.values()));
  }

  public Mono<Customer> findById(UUID id) {
    return Mono.defer(
        () ->
            Mono.justOrEmpty(customers.get(id))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))));
  }

  public Mono<Customer> create(CreateOrUpdateCustomerRequest request) {
    return Mono.fromSupplier(
        () -> {
          UUID id = UUID.randomUUID();
          Customer created =
              new Customer(id, request.name().trim(), request.email().trim());
          customers.put(id, created);
          return created;
        });
  }

  public Mono<Customer> update(UUID id, CreateOrUpdateCustomerRequest request) {
    return Mono.defer(
        () -> {
          Customer existing = customers.get(id);
          if (existing == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND));
          }
          Customer updated =
              new Customer(id, request.name().trim(), request.email().trim());
          customers.put(id, updated);
          return Mono.just(updated);
        });
  }

  public Mono<Void> delete(UUID id) {
    return Mono.defer(
        () -> {
          if (customers.remove(id) == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND));
          }
          return Mono.empty();
        });
  }
}
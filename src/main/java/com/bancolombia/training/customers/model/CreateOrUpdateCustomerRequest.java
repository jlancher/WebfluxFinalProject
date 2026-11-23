package com.bancolombia.training.customers.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateOrUpdateCustomerRequest(
    @NotBlank String name,
    @NotBlank @Email String email) {}
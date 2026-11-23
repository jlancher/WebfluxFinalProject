package com.bancolombia.training.customers.model;

import java.util.UUID;

public record Customer(UUID id, String name, String email) {}
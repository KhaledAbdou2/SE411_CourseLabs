package edu.psu.se411.lab15.model;

import org.springframework.data.annotation.Id;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record Product(@Id @Positive Integer id, @NotBlank @Size(max=100) String name, @Size(max=100) String description, @NotNull @DecimalMin("0.00") @Digits(integer=6,fraction=2) BigDecimal price) {}

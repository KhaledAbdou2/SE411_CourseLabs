package edu.psu.se411.lab16.model;

import org.springframework.data.annotation.Id;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record Course(@Id @Positive Integer id, @NotBlank @Size(max=100) String code, @NotBlank @Size(min=2,max=100) String name, @Size(max=100) String description, @NotNull @Min(1) @Max(10) Integer credits) {}

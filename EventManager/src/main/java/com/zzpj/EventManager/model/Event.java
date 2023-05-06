package com.zzpj.EventManager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class Event {

    @Id
    private Long id;
    @Size(min = 4, max = 50) private String name;
    @NotBlank(message = "description cannot be empty") private String description;
    @Positive private Double entranceFee;
    private LocalDateTime startDate;
}

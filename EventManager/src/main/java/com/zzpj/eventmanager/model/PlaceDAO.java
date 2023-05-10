package com.zzpj.eventmanager.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class PlaceDAO {

    @Id
    private String id;

    private String name;

    private Double capacity;

    private String placeType;
}

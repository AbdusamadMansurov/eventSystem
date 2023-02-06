package com.example.eventsystem.model;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private District district;
    private String streetHome;

    private Double latitude, longitude;

    public Address(District district, String streetHome) {
        this.district = district;
        this.streetHome = streetHome;
    }
}

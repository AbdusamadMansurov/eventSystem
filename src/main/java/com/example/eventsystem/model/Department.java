package com.example.eventsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    private Bot bot;

    @ManyToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    private Site site;
    @OneToMany(mappedBy = "department")
    @ToString.Exclude
    private List<User> clientList;

    //    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @ManyToOne
    private Company company;
    @OneToMany(mappedBy = "department")
    @ToString.Exclude
    private List<Category> categories;

    @OneToMany(mappedBy = "department")
    @ToString.Exclude
    private List<Vacancy> vacancies;
    @Column(nullable = true)
    private boolean active = true;
}

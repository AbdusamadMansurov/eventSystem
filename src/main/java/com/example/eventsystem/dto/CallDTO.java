package com.example.eventsystem.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Mansurov Abdusamad  *  01.12.2022  *  14:47   *  tedaSystem
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CallDTO {

    private Long clientId;

    private LocalDate nextConnectionDate;

    private String description;

    private List<Long> ReviewCategoryIds;

    boolean success = false;

}

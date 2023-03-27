package com.example.eventsystem.dto;

import lombok.*;

import java.util.List;

/**
 * @author Mansurov Abdusamad  *  12.12.2022  *  10:47   *  tedaSystem
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class EmployeeDTO {
    private String username, fullName;
    private String password;
    private String phoneFirst;
    private String phoneSecond;
    private AddressDTO addressDTO;
//    private Long companyId;
    private List<String> roleList;
}

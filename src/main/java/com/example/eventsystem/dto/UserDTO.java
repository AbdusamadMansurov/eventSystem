package com.example.eventsystem.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDTO {

    private String username, fullName, email, passportNumber;

    private String genderType;
    private Long departmentId;
    private List<String> userRoles;

    private AddressDTO addressDTO;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate brithDate;

//    private AddressDTO addressDTO;

}

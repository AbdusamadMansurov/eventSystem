package com.example.eventsystem.dto;

import com.example.eventsystem.model.enums.ActiveTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Malikov Azizjon    ourSystem    26.12.2022    15:39
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompanyDTO {

    private Long memberOrganizationId;
    private String name, stirNumber;
    private ActiveTypes activeType;
    private List<BankInfoDTO> bankInfoDTO;
    private MultipartFile attachment;
    private EmployeeDTO director;
    private Long directorId;
    private AddressDTO addressDTO;

}

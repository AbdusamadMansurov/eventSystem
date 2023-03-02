package com.example.eventsystem;

import com.example.eventsystem.model.*;
import com.example.eventsystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReadXLSX {

    private final WorkTypeRepository workTypeRepository;
    private final WorkCategoryRepository workCategoryRepository;
    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;
    private final DistrictRepository districtRepository;
    private final PasswordEncoder passwordEncoder;

    public void main() {
        String filePath = "E:/downloads/Telegram Desktop/28_минг_туманга_ажратилгани_йўналиши_билан.xlsx";
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(filePath));
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
//            HSSFWorkbook wb = new HSSFWorkbook(fileInputStream);
            XSSFSheet sheet = workbook.getSheetAt(0); // get first sheet
//            HSSFSheet sheetWB = wb.getSheetAt(0);
            for (Row row : sheet) {
                String companyName = null, inn = null, directorName = null, directorPhone = null, districtName = null, workCategory = null, workType = null, abortion = null;
                String companyNameCell = "Корхона номи", innCell = "ИНН рақами", directorNameCell = "Раҳбарининг Ф.И.О", directorPhoneCell = "Телефон рақами",
                        districtNameCell = "шаҳар, туман номи", workCategoryCell = "ФАОЛИЯТ ТУРИ", workTypeCell = "Тармоқ йўналиши",
                        abortionCell = "2022 йил январ-август (8 ойлик) якуни бўйича Бюджетга тўлаган солиқ тушуми";
                for (Cell cell : row) {

                    if (cell.getStringCellValue().contains(companyNameCell)) {
                        companyName = cell.getStringCellValue();
                    }
                    if (cell.getStringCellValue().contains(innCell)) {
                        inn = cell.getStringCellValue();
                    }
                    if (cell.getStringCellValue().contains(directorPhoneCell)) {
                        directorPhone = cell.getStringCellValue();
                    }
                    if (cell.getStringCellValue().contains(districtNameCell)) {
                        districtName = cell.getStringCellValue();
                    }
                    if (cell.getStringCellValue().contains(workCategoryCell)) {
                        workCategory = cell.getStringCellValue();
                    }
                    if (cell.getStringCellValue().contains(workTypeCell)) {
                        workType = cell.getStringCellValue();
                    }
                    if (cell.getStringCellValue().contains(abortionCell)) {
                        abortion = cell.getStringCellValue();
                    }
                    if (cell.getStringCellValue().contains(districtNameCell)) {
                        directorName = cell.getStringCellValue();
                    }
                    System.out.print(cell.getStringCellValue() + "\t");
                }
                Optional<District> districtOptional = districtRepository.findByName(districtName);
                if (districtOptional.isEmpty()) {
                    continue;
                }
                Optional<Company> companyOptional = companyRepository.findByINN(inn);
                if (companyOptional.isPresent()) {
                    continue;
                }
                District district = districtOptional.get();
                Company company = new Company();
                company.setName(companyName);
                company.setAddress(new Address(district, district.getRegion().getCountry(), district.getRegion()));
                company.setAbortion(Double.valueOf(Objects.requireNonNull(abortion)));
                company.setINN(inn);
                Company saveCompany = companyRepository.save(company);
                Employee employee = new Employee();
                employee.setCompany(saveCompany);
                employee.setFullName(directorName);
                employee.setPhoneFirst(directorPhone);
                employee.setUsername(directorPhone);
                employee.setPassword(passwordEncoder.encode(directorPhone));
                Employee saveDirector = employeeRepository.save(employee);
                saveCompany.setDirector(saveDirector);
                companyRepository.save(company);
                if (workType != null) {
                    Optional<WorkType> workTypeOptional = workTypeRepository.findByName(workType);
                    if (workTypeOptional.isPresent()) {
                        company.setWorkTypeList(List.of(workTypeOptional.get()));
                    } else {
                        WorkType workType1 = new WorkType();
                        workType1.setName(workType);
                        company.setWorkTypeList(List.of(workTypeRepository.save(workType1)));
                    }
                }
                if (workCategory != null) {
                    Optional<WorkCategory> workCategoryOptional = workCategoryRepository.findByName(workCategory);
                    if (workCategoryOptional.isPresent()) {
                        company.setWorkCategoryList(List.of(workCategoryOptional.get()));
                    } else {
                        WorkCategory workCategory1 = new WorkCategory();
                        workCategory1.setName(workCategory);
                        company.setWorkCategoryList(List.of(workCategoryRepository.save(workCategory1)));
                    }
                }
                companyRepository.save(company);
            }
            System.out.println();

            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

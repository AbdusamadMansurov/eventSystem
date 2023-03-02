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

import static java.awt.image.ImageObserver.ERROR;
import static java.sql.JDBCType.NUMERIC;
import static javax.management.openmbean.SimpleType.BOOLEAN;
import static javax.management.openmbean.SimpleType.STRING;
import static org.openxmlformats.schemas.spreadsheetml.x2006.main.STCfvoType.FORMULA;

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
        String filePath = "C:/Users/User/Downloads/Telegram Desktop/28_минг_туманга_ажратилгани_йўналиши_билан.xlsx";
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
//                for (Cell cell : row) {
//
////                    Cell celldata=(Cell) cell;
////                    cell = (HSSFCell) cell;
//                    companyName =
//                    if (asd(cell).contains(companyNameCell)) {
//                            companyName = asd(cell);
//
////                        companyName = cell.getStringCellValue();
//                    }
//                    if (asd(cell).contains(innCell)) {
//                        inn = asd(cell);
////                        inn = cell.getStringCellValue();
//                    }
//                    if (asd(cell).contains(directorPhoneCell)) {
//                        directorPhone = asd(cell);
////                        directorPhone = cell.getStringCellValue();
//                    }
//                    if (asd(cell).contains(districtNameCell)) {
//                        districtName = asd(cell);
////                        districtName = cell.getStringCellValue();
//                    }
//                    if (asd(cell).contains(workCategoryCell)) {
//                        workCategory = asd(cell);
////                        workCategory = cell.getStringCellValue();
//                    }
//                    if (asd(cell).contains(workTypeCell)) {
//
//                        workType = asd(cell);
////                        workType = cell.getStringCellValue();
//
//                    }
//                    if (asd(cell).contains(abortionCell)) {
//                        abortion = asd(cell);
////                        abortion = cell.getStringCellValue();
//                    }
//                    if (asd(cell).contains(districtNameCell)) {
//                        directorName = asd(cell);
////                        directorName = cell.getStringCellValue();
//                    }
//                    System.out.print(cell.getStringCellValue() + "\t");
//                }

                districtName = asd(row.getCell(1));
                companyName = asd(row.getCell(2));
                inn = asd(row.getCell(4));
                workType = asd(row.getCell(5));
                workCategory = asd(row.getCell(6));
                abortion = asd(row.getCell(14));
                directorName = asd(row.getCell(16));
                directorPhone = asd(row.getCell(17));



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

    private String asd(Cell cell) {
        if(cell == null)
            return null;
        if (cell.equals(NUMERIC)) {
            return String.valueOf(cell.getNumericCellValue());
        } else if (cell.equals(STRING)) {
            return cell.getStringCellValue();
        } else if (cell.equals(ERROR)) {
            return String.valueOf(cell.getErrorCellValue());
        } else if (cell.equals(FORMULA)) {
            return cell.getCellFormula();
        }else if (cell.equals(BOOLEAN)) {
            return cell.getCellFormula();
        } else  {
            return cell.toString();
        }
    }
}

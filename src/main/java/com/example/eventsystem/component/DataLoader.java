package com.example.eventsystem.component;

import com.example.eventsystem.model.BankInfo;
import com.example.eventsystem.model.Company;
import com.example.eventsystem.model.Employee;
import com.example.eventsystem.model.enums.RoleType;
import com.example.eventsystem.repository.BankInfoRepository;
import com.example.eventsystem.repository.CompanyRepository;
import com.example.eventsystem.repository.EmployeeRepository;
import com.example.eventsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    @Value("${spring.sql.init.mode}")
    private String runMode;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final BankInfoRepository bankInfoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        System.err.print(runMode);
        if (runMode.equals("always")) {
//            Country country = countryRepository.save(new Country("Uzbekistan", "UZB"));
//            Region region = regionRepository.save(new Region("Tashkent", "Poytaxt", country));
//            District district = districtRepository.save(new District("Yakkasaroy", region));
            Employee employee = new Employee();
            employee.setUsername("Abdusamad");
            employee.setPassword(passwordEncoder.encode("123123"));
            employee.setPhoneFirst("+998977515747");
//            employee.setSelectedRole(RoleType.DIRECTOR);
            Set<RoleType> roles = new LinkedHashSet<>();
            roles.add(RoleType.DIRECTOR);
            employee.setRoles(roles);
            Employee employeeSave = employeeRepository.save(employee);
            Company company = companyRepository.findById(1L).get();
            company.setDirector(employeeSave);
//            company.setAddress(new Address(district, "22A"));
            Company save = companyRepository.save(company);
            List<BankInfo> bankInfos = new ArrayList<>();
            BankInfo bankInfo = new BankInfo();
            bankInfo.setCompany(save);
            bankInfo.setMfo(54567);
            bankInfo.setBranch("Oybek");
            bankInfo.setCurrency("DOLLAR");
            bankInfo.setAccountNumber(123);
            bankInfos.add(bankInfo);
            BankInfo bankInfo1 = new BankInfo();
            bankInfo1.setCompany(save);
            bankInfo1.setMfo(14787);
            bankInfo1.setBranch("Do'stlik");
            bankInfo1.setCurrency("EURO");
            bankInfo1.setAccountNumber(321);
            bankInfos.add(bankInfo1);
            bankInfoRepository.saveAll(bankInfos);
        }
//        if (runMode.equals("never")) {
//            List<Company> companies = companyRepository.findAll();
//            for (Company company : companies) {
//                Employee director = company.getDirector();
//                if (director != null && company.getAddress() != null) {
//                    director.setAddress(company.getAddress());
//                    Optional<User> userOptional = userRepository.findByPhone(director.getPhoneFirst());
//                    if (userOptional.isEmpty())
//                        continue;
//                    User user = userOptional.get();
//                    user.setAddress(company.getAddress());
//                    employeeRepository.save(director);
//                    userRepository.save(user);

//            Optional<Employee> employeeOptional = employeeRepository.findById(3415L);
//            Employee employee = employeeOptional.get();
//            employee.setPassword(passwordEncoder.encode("123123"));
//            employee.setPhoneFirst("+998977515747");
//            employeeRepository.save(employee);
//        }
        }
    }




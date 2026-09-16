package com.project.billingManagementSystem.controller;


import com.project.billingManagementSystem.apiResponse.APIResponse;
import com.project.billingManagementSystem.entity.BusinessDetails.Company;
import com.project.billingManagementSystem.entity.dto.createDTO.CompanyRequest;
import com.project.billingManagementSystem.entity.dto.updateDTO.CompanyUpdateRequest;
import com.project.billingManagementSystem.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/business")
public class CompanyController {

    @Autowired
    private CompanyService service;

    @PostMapping("/add-company")
    public ResponseEntity<APIResponse<Company>> addCompany( @Valid @RequestBody CompanyRequest company) {
        Company savedCompany = service.createCompany(company);

        if (savedCompany == null) {
            APIResponse<Company> response = APIResponse.<Company>builder().success(false).code(HttpStatus.CONFLICT.value()).message("Company already exists.").data(null).build();
            return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON).body(response);
        }
        APIResponse<Company> response = APIResponse.<Company>builder().success(true).code(HttpStatus.CREATED.value()).message("Company added successfully.").data(savedCompany).build();
        return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(response);
    }



    @GetMapping("/get-company")
    public ResponseEntity<APIResponse<Company>> getCompany() {
        Company company = service.getCompany();
        if (company == null) {
            APIResponse<Company> response = APIResponse.<Company>builder().success(false).code(HttpStatus.NOT_FOUND.value()).message("Company not found.").data(null).build();

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        APIResponse<Company> response = APIResponse.<Company>builder().success(true).code(HttpStatus.OK.value()).message("Company details fetched successfully.").data(company).build();

        return ResponseEntity.ok(response);
    }




    @PutMapping("/update-company")
    public ResponseEntity<APIResponse<Company>> updateCompany( @Valid @RequestBody CompanyUpdateRequest company) {

        Company updatedCompany = service.updateCompany(company.getId(), company);

        if (updatedCompany == null) {
            APIResponse<Company> response = APIResponse.<Company>builder().success(false).code(HttpStatus.NOT_FOUND.value()).message("Company not found.").data(null).build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        APIResponse<Company> response = APIResponse.<Company>builder().success(true).code(HttpStatus.OK.value()).message("Company updated successfully.").data(updatedCompany).build();
        return ResponseEntity.ok(response);
    }


}

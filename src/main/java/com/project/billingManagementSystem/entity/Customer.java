package com.project.billingManagementSystem.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.billingManagementSystem.entity.invoice.Invoice;
import com.project.billingManagementSystem.enums.CustomerStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Customer {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long customerId;
	
	private String customerName;
	
	private String customerLastName;
	
	private String phoneNo;
	
	private String emailId;

	@Enumerated(EnumType.STRING)
	private CustomerStatus status;

	@OneToMany(
			mappedBy = "customer",
			cascade = CascadeType.ALL,
			fetch = FetchType.LAZY
	)
	@JsonManagedReference
	private List<Invoice> invoices;

	
	
	
	
	
	
	

}

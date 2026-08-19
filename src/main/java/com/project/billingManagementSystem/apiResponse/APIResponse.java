package com.project.billingManagementSystem.apiResponse;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class APIResponse<T> {

	
	private Boolean success;
	private int code;
	private String message;
	private T data;
	
	
	

}

package com.myfiles.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UploadResultDto {

	private String fileName;
	private String status; 
	private String code; 
	private String message;

}

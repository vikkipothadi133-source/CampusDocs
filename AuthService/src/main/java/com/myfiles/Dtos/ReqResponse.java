package com.myfiles.Dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@AllArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class ReqResponse {


    private String email;
    private String message;
}

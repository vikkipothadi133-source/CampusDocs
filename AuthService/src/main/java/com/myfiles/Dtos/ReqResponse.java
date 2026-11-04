package com.myfiles.Dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@AllArgsConstructor
@NoArgsConstructor
public class ReqResponse {

    public ReqResponse(String accessToken, Long userId,
                        String fullName, String email) {

        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
    }

    private String accessToken;
    private String tokenType;
    private Long userId;
    private String fullName;
    private String email;
}

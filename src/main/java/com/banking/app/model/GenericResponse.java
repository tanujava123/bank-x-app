package com.banking.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponse {

    private String status;

    private int statusCode;

    private String message;

    private Object object;
}

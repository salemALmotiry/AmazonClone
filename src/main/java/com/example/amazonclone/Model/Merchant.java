package com.example.amazonclone.Model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class Merchant {


    @NotEmpty(message = "Merchant id cannot be null")
    @Size(min = 10, max = 50, message = "Merchant id must be between 10 and 50 characters")
    private String id;

    @NotEmpty(message = "Merchant name cannot be null")
    @Size(min = 3, max = 49, message = "Merchant name must be between 3 and 49 characters")
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9 _-]{2,48}$", message = "RegEx rules:" +
            "- Length: between 3 to 49 characters.\n" +
            "- No leading or trailing spaces.\n" +
            "- Must start with a letter.")
    private String name;


    

}

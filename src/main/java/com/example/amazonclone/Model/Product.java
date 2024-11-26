package com.example.amazonclone.Model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Product {


    @NotEmpty(message = "Product id cannot be null")
    @Size(min = 10,max = 50, message = "Product id must be between 10 and 50")
    private String id;


    @NotEmpty(message = "Product name cannot be null")
    @Size(min = 5 , max = 49 , message = "Product name must be between 5 and 49")
    @Pattern(regexp = "^[A-Za-z0-9][A-Za-z0-9 _-]{5,50}$", message = "RegEx rules:" +
            "- Length: between 5 to 50 characters.\n" +
            "- Allowed Characters: Letters (both uppercase and lowercase), numbers, spaces, hyphens (`-`), underscores (`_`), and possibly special characters (like `&`, `@`, etc.).\n" +
            "- Starting Character: It can start with a letter or a number.\n" +
            "- No leading or trailing spaces.")
    private String name;

    @NotNull(message = "Product price cannot be null ")
    @Positive(message = "Product price accept only positive")
    private double price;

    @NotEmpty(message = "Category id cannot be null")
    @Size(min = 10,max = 50, message = "category id must be between 10 and 50")
    private String categoryId;

    @JsonIgnore
    private String couponCode;
    @JsonIgnore
    private int discount;
    @JsonIgnore
    private LocalDateTime until;








}

package com.example.amazonclone.Model;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Category {

    @NotEmpty(message = "Category id cannot be null")
    @Size(min = 10, max = 50, message = "Category id must be between 10 and 50 characters")
    private String id;

    @NotEmpty(message = "Category name cannot be null")
    @Size(min = 3, max = 49, message = "Category name must be between 3 and 49 characters")
    @Pattern(regexp = "^[A-Za-z0-9 _-]{3,50}$", message = "RegEx rules:" +
            "- Length: between 3 to 50 characters.\n" +
            "- No leading or trailing spaces.")
    private String name;

}


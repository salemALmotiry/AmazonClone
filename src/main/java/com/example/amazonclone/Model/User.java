package com.example.amazonclone.Model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@Data
@AllArgsConstructor
public class User {



    @NotEmpty(message = "User id cannot be null")
    @Size(min = 10,max = 50, message = "User id must be between 10 and 50")
    private String id;

    @NotEmpty(message = "User name cannot be null")
    @Size(min = 3 , max = 30 , message = "User name must be between 3 and 30")
    @Pattern(regexp = "^(?=[A-Za-z_])([A-Za-z0-9_.]{3,30})$", message = "User name RegEx Rules:\n" +
                                                                        "- Length: 3 to 30 characters.\n" +
                                                                        "- Allowed Characters: Letters (both uppercase and lowercase), numbers, underscores (`_`), and periods (`.`).\n" +
                                                                        "- Must start with a letter or underscore.\n" +
                                                                        "- No spaces or special characters allowed.")
    private String userName;


    @NotEmpty(message = "User password cannot be null")
    @Size(min = 3 , max = 30 , message = "User password must be between 3 and 30")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,20}$", message = "password RegEx rules: \n" +
                                                                                                                "- Length: between 8 to 20 characters.\n" +
                                                                                                                "- Must contain at least:\n" +
                                                                                                                "   - One uppercase letter (A-Z)\n" +
                                                                                                                "   - One lowercase letter (a-z)\n" +
                                                                                                                "   - One number (0-9)\n" +
                                                                                                                "   - One special character (e.g., `!@#$%^&*`)\n" +
                                                                                                                "- No spaces allowed.")
    private String password;


    @NotEmpty(message = "User password cannot be null")
    @Size(min = 3 , max = 30 , message = "User email must be between 3 and 30")
    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Email RegEx rules:"+
                                                                        "- Format: Must follow the pattern `local-part@domain`.\n" +
                                                                        "- Local part can contain:\n" +
                                                                        "  - Letters (A-Z, a-z)\n" +
                                                                        "  - Numbers (0-9)\n" +
                                                                        "  - Special characters (`!#$%&'*+/=?^_`{|}~-`)\n" +
                                                                        "  - Periods (.) but not at the start or end, and not consecutively.\n" +
                                                                        "- Domain part must contain:\n" +
                                                                        "  - Letters (A-Z, a-z)\n" +
                                                                        "  - Numbers (0-9)\n" +
                                                                        "  - Periods (.) to separate subdomains.\n" +
                                                                        "  - Must end with a valid top-level domain (e.g., `.com`, `.org`, `.net`).")
    private String email;

    @NotNull(message = "User balance cannot be null")
    @Positive(message = "User balance accept only positive")
    private double balance;

    @NotEmpty(message = "User role cannot be null")
    @Pattern(regexp = "^(Admin|Customer)" , message = "User role can be Admin or Customer only")
    private String role;


    @JsonIgnore
    private ArrayList<Double> buyNowAndPayLetter;

    @JsonIgnore
    private ArrayList<Product> purchaseHistory;



}

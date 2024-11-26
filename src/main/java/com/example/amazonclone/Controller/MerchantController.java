package com.example.amazonclone.Controller;

import com.example.amazonclone.ApiResponse.ApiResponse;
import com.example.amazonclone.Model.Merchant;
import com.example.amazonclone.Service.MerchantService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/amazon-clone/merchant")
public class MerchantController {


    private final MerchantService merchantService;

    @GetMapping("/get")
    public ResponseEntity getMerchants(){

        if (merchantService.getMerchants().isEmpty()){
            return ResponseEntity.status(200).body(new ApiResponse("There are no merchants in the system "));
        }
        return ResponseEntity.status(200).body(merchantService.getMerchants());
    }


    @PostMapping("/add")
    public ResponseEntity addMerchant(@RequestBody @Valid Merchant merchant, Errors errors){

        if (errors.hasErrors()){
            return ResponseEntity.status(400).body(errors.getFieldError().getDefaultMessage());

        }

        boolean isAdded = merchantService.addMerchant(merchant);
        if (isAdded){
            return ResponseEntity.status(200).body(new ApiResponse("New merchant successfully added"));

        }
        return ResponseEntity.status(400).body(new ApiResponse("Merchant already in the system"));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity updateMerchant(@PathVariable String id, @RequestBody @Valid Merchant merchant, Errors errors){

        if (errors.hasErrors()){
            return ResponseEntity.status(400).body(errors.getFieldError().getDefaultMessage());

        }

        boolean isUpdated = merchantService.updateMerchant(id,merchant);

        if (isUpdated){
            return ResponseEntity.status(200).body(new ApiResponse("Merchant successfully updated"));

        }

        return ResponseEntity.status(400).body(new ApiResponse("Merchant not found in the system"));
    }



    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteMerchant(@PathVariable String id){

        boolean isDeleted = merchantService.deleteMerchant(id);

        if (isDeleted){
            return ResponseEntity.status(200).body(new ApiResponse("Merchant successfully deleted"));

        }

        return ResponseEntity.status(400).body(new ApiResponse("Merchant not found in the system"));
    }


}

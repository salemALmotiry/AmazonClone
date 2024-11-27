package com.example.amazonclone.Controller;

import com.example.amazonclone.ApiResponse.ApiResponse;
import com.example.amazonclone.Model.Product;
import com.example.amazonclone.Service.CategoryService;
import com.example.amazonclone.Service.ProductService;

import com.example.amazonclone.Service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/amazon-clone/product")
public class ProductController {


    private final ProductService productService;
    private final UserService userService;


    @GetMapping("/get")
    public ResponseEntity getProducts(){

        if (productService.getProducts().isEmpty()){
            return ResponseEntity.status(200).body(new ApiResponse("No products available."));
        }
        return ResponseEntity.status(200).body(productService.getProducts());
    }


    @PostMapping("/add")
    public ResponseEntity addProduct(@RequestBody @Valid Product product, Errors errors){

        if (errors.hasErrors()){
            return ResponseEntity.status(400).body(errors.getFieldError().getDefaultMessage());

        }

        String isAddable = productService.checkRequirement(product.getCategoryId());
        if (isAddable != null){
            return ResponseEntity.status(400).body(new ApiResponse(isAddable));
        }

        boolean isAdded = productService.addProduct(product);
        if (isAdded){

            return ResponseEntity.status(200).body(new ApiResponse("New product successfully added."));

        }
        return ResponseEntity.status(400).body(new ApiResponse("Product already exists in the system."));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity updateProduct(@PathVariable String id, @RequestBody @Valid Product product, Errors errors){

        if (errors.hasErrors()){
            return ResponseEntity.status(400).body(errors.getFieldError().getDefaultMessage());

        }

        boolean isUpdated = productService.updateProduct(id,product);

        if (isUpdated){
            return ResponseEntity.status(200).body(new ApiResponse("Product successfully updated."));

        }

        return ResponseEntity.status(400).body(new ApiResponse("Product not found in the system.."));
    }



    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteProduct(@PathVariable String id){

        boolean isDeleted = productService.deleteProduct(id);

        if (isDeleted){
            return ResponseEntity.status(200).body(new ApiResponse("Product successfully deleted."));

        }

        return ResponseEntity.status(400).body(new ApiResponse("Product not found in the system."));
    }

    @PutMapping("/set-discount/{userId}/{productId}/{categoryId}/{until}/{percent}/{couponCode}")
    public ResponseEntity setDiscount(@PathVariable String userId,@PathVariable String productId , @PathVariable String categoryId,@PathVariable int until,@PathVariable int percent,@PathVariable String couponCode){


        String response = userService.setDiscount(userId,productId,categoryId,percent,until,couponCode);

        if (response == null){
            return ResponseEntity.status(200).body(new ApiResponse("Product discount successfully activated for "+until+ " days."));

        }

        return ResponseEntity.status(400).body(new ApiResponse(response));
    }



    //_______________________________Extra____________________________________
    @GetMapping("/merchant-products/{merchantId}")
    public ResponseEntity getMerchantProducts(@PathVariable String merchantId) {

        ArrayList products = productService.getMerchantProducts(merchantId);
        if (products == null){
            return  ResponseEntity.status(400).body(new ApiResponse("Merchant not found in the system"));

        }
        return  ResponseEntity.status(200).body(products);

    }

}

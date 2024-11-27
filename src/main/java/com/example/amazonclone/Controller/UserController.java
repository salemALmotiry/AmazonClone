package com.example.amazonclone.Controller;

import com.example.amazonclone.ApiResponse.ApiResponse;
import com.example.amazonclone.Model.Product;
import com.example.amazonclone.Model.User;
import com.example.amazonclone.Service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/amazon-clone/user")
public class UserController {

    private final UserService userService;


    @GetMapping("/get")
    public ResponseEntity getUsers(){

        if (userService.getUsers().isEmpty()){
            return ResponseEntity.status(200).body(new ApiResponse("No users found."));
        }
        return ResponseEntity.status(200).body(userService.getUsers());
    }


    @PostMapping("/add")
    public ResponseEntity addUser(@RequestBody @Valid User user, Errors errors){

        if (errors.hasErrors()){
            return ResponseEntity.status(400).body(errors.getFieldError().getDefaultMessage());

        }

        boolean isAdded = userService.addUser(user);
        if (isAdded){
            return ResponseEntity.status(200).body(new ApiResponse("New user successfully added."));

        }
        return ResponseEntity.status(400).body(new ApiResponse("User already exists in the system."));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity updateUser(@PathVariable String id, @RequestBody @Valid User user, Errors errors){

        if (errors.hasErrors()){
            return ResponseEntity.status(400).body(errors.getFieldError().getDefaultMessage());

        }

        boolean isUpdated = userService.updateUser(id,user);

        if (isUpdated){
            return ResponseEntity.status(200).body(new ApiResponse("User details successfully updated."));

        }

        return ResponseEntity.status(400).body(new ApiResponse("User not found in the system."));
    }



    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteUser(@PathVariable String id){

        boolean isDeleted = userService.deleteUser(id);

        if (isDeleted){
            return ResponseEntity.status(200).body(new ApiResponse("User successfully deleted."));

        }

        return ResponseEntity.status(400).body(new ApiResponse("User not found in the system."));
    }


    @PutMapping("/direct-paying/{userId}/{merchantId}/{productId}")
    public ResponseEntity directPay(@PathVariable String userId, @PathVariable String merchantId, @PathVariable String productId){


        String response = userService.directPaying(productId,userId,merchantId);

        if (response == null){
            return ResponseEntity.status(200).body(new ApiResponse("Item has been purchased."));

        }
        return ResponseEntity.status(400).body(new ApiResponse(response));
    }

    //________________________________

    @PutMapping("/refund/{userId}/{merchantId}/{productId}")
    public ResponseEntity refund(@PathVariable String userId, @PathVariable String merchantId, @PathVariable String productId){

        String response = userService.refund(productId,userId,merchantId);

        if (response == null){
            return ResponseEntity.status(200).body(new ApiResponse("Item has been successfully refund"));

        }
        return ResponseEntity.status(400).body(new ApiResponse(response));

    }



    @PutMapping("/gif-gift/{userId}/{giftedEmail}/{merchantId}/{productId}")
    public ResponseEntity gifGift(@PathVariable String userId, @PathVariable String giftedEmail, @PathVariable String merchantId, @PathVariable String productId){


        String response = userService.gifGift(userId,giftedEmail,productId,merchantId);

        if (response == null){
            return ResponseEntity.status(200).body(new ApiResponse("Gifted user has been successfully gifted"));

        }
        return ResponseEntity.status(400).body(new ApiResponse(response));


    }



    @GetMapping("/recommendation/{userId}")
    public ResponseEntity recommendation(@PathVariable String userId) {

        if (!userService.checkUser(userId)){
            return ResponseEntity.status(400).body(new ApiResponse("User not found in the system."));
        }

        ArrayList<Product> products = userService.recommendationProducts(userId);

        if (products != null){
            return ResponseEntity.status(200).body(products);

        }
        return ResponseEntity.status(400).body(new ApiResponse("User do not make ant perches"));
    }


    @GetMapping("/best/{userId}/{limit}")
    public ResponseEntity bestSelling(@PathVariable String userId,@PathVariable int limit) {
        if (!userService.checkUser(userId)){
            return ResponseEntity.status(400).body(new ApiResponse("User not found in the system."));
        }
        ArrayList<Product> products = userService.bestSelling(limit);

        if (!products.isEmpty()){
            return ResponseEntity.status(200).body(products);

        }
        return ResponseEntity.status(400).body(new ApiResponse("There is no best product available at this time"));
    }


    //____________________________Extra__________________________________--
    @PutMapping("/pay-with-coupon/{userId}/{merchantId}/{productId}/{coupon}")
    public ResponseEntity payWithCoupon(@PathVariable String userId, @PathVariable String merchantId, @PathVariable String productId,@PathVariable String coupon) {


        String response = userService.payWithCoupon(productId,userId,merchantId,coupon);

        if (response == null){
            return ResponseEntity.status(200).body(new ApiResponse("Item has been purchased."));

        }
        return ResponseEntity.status(400).body(new ApiResponse(response));
    }

}

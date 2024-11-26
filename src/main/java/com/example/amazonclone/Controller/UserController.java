package com.example.amazonclone.Controller;

import com.example.amazonclone.ApiResponse.ApiResponse;
import com.example.amazonclone.Model.User;
import com.example.amazonclone.Service.MerchantService;
import com.example.amazonclone.Service.MerchantStockService;
import com.example.amazonclone.Service.ProductService;
import com.example.amazonclone.Service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;


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



    @PutMapping("/gif-gift/{userId}/{giftedId}/{merchantId}/{productId}")
    public ResponseEntity gifGift(@PathVariable String userId, @PathVariable String giftedId, @PathVariable String merchantId, @PathVariable String productId){

        if (giftedId.equals(userId)){
            return ResponseEntity.status(400).body(new ApiResponse("You cannot gift to yourself."));

        }
        String response = userService.gifGift(userId,giftedId,productId,merchantId);

        if (response == null){
            return ResponseEntity.status(200).body(new ApiResponse("Gifted user has been successfully gifted"));

        }
        return ResponseEntity.status(400).body(new ApiResponse(response));


    }


    @PutMapping("/puy-now-pay-letter/{userId}/{merchantId}/{productId}")
    public ResponseEntity installment(@PathVariable String userId, @PathVariable String merchantId, @PathVariable String productId){


        String response = userService.installment(productId,userId,merchantId);

        if (response == null){
            return ResponseEntity.status(200).body(new ApiResponse("Item has been purchased. Your next payment is due in 30 days."));

        }
        return ResponseEntity.status(400).body(new ApiResponse(response));
    }



    //____________________________Extra__________________________________--
    @PutMapping("/pay-with-coupon/{userId}/{merchantId}/{productId}/{coupon}")
    public ResponseEntity payWithCoupon(@PathVariable String userId, @PathVariable String merchantId, @PathVariable String productId,@PathVariable String coupon)
    {


        String response = userService.payWithCoupon(productId,userId,merchantId,coupon);

        if (response == null){
            return ResponseEntity.status(200).body(new ApiResponse("Item has been purchased."));

        }
        return ResponseEntity.status(400).body(new ApiResponse(response));
    }

}

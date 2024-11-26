package com.example.amazonclone.Controller;


import com.example.amazonclone.ApiResponse.ApiResponse;
import com.example.amazonclone.Model.MerchantStock;
import com.example.amazonclone.Service.MerchantService;
import com.example.amazonclone.Service.MerchantStockService;
import com.example.amazonclone.Service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/amazon-clone/merchant-stock")
public class MerchantStockController {

    private final MerchantStockService merchantStockService;
    private final ProductService productService;
    private final MerchantService merchantService;

    @GetMapping("/get")
    public ResponseEntity getMerchantStocks(){

        if (merchantStockService.getMerchantStocks().isEmpty()){
            return ResponseEntity.status(200).body(new ApiResponse("No merchant stocks in the system."));
        }
        return ResponseEntity.status(200).body(merchantStockService.getMerchantStocks());
    }


    @PostMapping("/add")
    public ResponseEntity addMerchantStock(@RequestBody @Valid MerchantStock merchantStock, Errors errors){

        if (errors.hasErrors()){
            return ResponseEntity.status(400).body(errors.getFieldError().getDefaultMessage());

        }
        
        String isAddable = productService.checkRequirement(merchantStock.getMerchantId(),merchantStock.getProductId());
       
        if (!isAddable.isEmpty()){

            return ResponseEntity.status(400).body(new ApiResponse(isAddable));

        }
        
        boolean isAdded = merchantStockService.addMerchantStock(merchantStock);
        if (isAdded){
            return ResponseEntity.status(200).body(new ApiResponse("New merchant stock successfully added."));

        }
        return ResponseEntity.status(400).body(new ApiResponse("Merchant stock already in the system."));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity updateMerchantStock(@PathVariable String id, @RequestBody @Valid MerchantStock merchantStock, Errors errors){

        if (errors.hasErrors()){
            return ResponseEntity.status(400).body(errors.getFieldError().getDefaultMessage());

        }

        boolean isUpdated = merchantStockService.updateMerchantStock(id,merchantStock);

        if (isUpdated){
            return ResponseEntity.status(200).body(new ApiResponse("Merchant stock successfully updated"));

        }

        return ResponseEntity.status(400).body(new ApiResponse("MerchantStock not found in the system."));
    }



    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteMerchantStock(@PathVariable String id){

        boolean isDeleted = merchantStockService.deleteMerchantStock(id);

        if (isDeleted){
            return ResponseEntity.status(200).body(new ApiResponse("Merchant stock successfully deleted"));

        }

        return ResponseEntity.status(400).body(new ApiResponse("MerchantStock not found in the system."));

    }


    @PutMapping("/restock/{merchantId}/{productId}/{amount}")
    public ResponseEntity reStock(@PathVariable String merchantId, @PathVariable String productId, @PathVariable int amount){

        boolean isProductExits = productService.checkProduct(productId);
        if (!isProductExits){
            return ResponseEntity.status(400).body(new ApiResponse("Product not found in the system."));
        }

        boolean isMerchantId = merchantService.checkMerchant(merchantId);
        if (!isMerchantId){
            return ResponseEntity.status(400).body(new ApiResponse("Merchant not found in the system."));
        }

        boolean isReStock = merchantStockService.reStock(merchantId,amount);
        if (isReStock){
            return ResponseEntity.status(200).body(new ApiResponse("Merchant stock successfully restocked"));
        }
        return ResponseEntity.status(400).body(new ApiResponse("Something went wrong."));

    }


    //_________________________Extra_________________
    //Combine stocks of one product
    @GetMapping("/get-stocks/{merchantId}")
    public ResponseEntity getStocks(@PathVariable String merchantId){

        String response = productService.getStocks(merchantId);
        if (response.startsWith("Product")) {
            return ResponseEntity.status(400).body(new ApiResponse(response));
        }
        return ResponseEntity.status(200).body(new ApiResponse("This product stocks: "+response));

    }





}

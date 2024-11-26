package com.example.amazonclone.Service;


import com.example.amazonclone.ApiResponse.ApiResponse;
import com.example.amazonclone.Model.Merchant;
import com.example.amazonclone.Model.MerchantStock;
import com.example.amazonclone.Model.Product;
import com.example.amazonclone.Model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

@Service
@AllArgsConstructor
public class ProductService {

    private final CategoryService categoryService;
    private final MerchantService merchantService;
    private final MerchantStockService merchantStockService;

    ArrayList<Product> products = new ArrayList<>();

    public ArrayList<Product> getProducts() {
        return this.products;
    }

    public boolean addProduct(Product product){

        for (Product tem:this.products){
            if (tem.getId().equals(product.getId()) ){
                return false;
            }
        }


        this.products.add(product);
        return true;
    }


    public boolean updateProduct(String id , Product product){

        for (int i = 0; i < this.products.size(); i++) {

            if (this.products.get(i).getId().equals(id)){
                this.products.set(i,product);
                return true;
            }

        }
        return false;
    }


    public boolean deleteProduct(String id){

        for (Product Product:this.products){
            if (Product.getId().equals(id)){
                this.products.remove(Product);
                return true;
            }
        }

        return false;
    }


    public boolean checkProduct(String id){
        for (Product Product:this.products){
            if (Product.getId().equals(id)){
                return true;
            }
        }
        return false;
    }

    public String checkRequirement(String categoryId){
        if (categoryService.checkCategory(categoryId) == null){
            return "Category not in the system. please contact to admin to add new category";


        }

        return null;
    }


    public String checkRequirement(String merchantId,String productId){
        if (!checkProduct(productId)){

            return "Product not found in the system.";

        }
        if (!merchantService.checkMerchant(merchantId)){

            return "Merchant not found in the system. Please register as merchant";

        }

        return "";
    }





    public ArrayList<Product> filter(String category, String price) {


        ArrayList<Product> sortedProducts = new ArrayList<>(this.products);


        if (category.equalsIgnoreCase("all")) {
            sortedProducts = new ArrayList<>();
            for (Product product : products) {

                if (product.getCouponCode() != null
                        && product.getDiscount() != 0
                        && product.getUntil() != null
                        && product.getUntil().isAfter(LocalDateTime.now())) {
                    sortedProducts.add(new Product(
                            product.getId(),
                            product.getName(),
                            (product.getPrice() * product.getDiscount() / 100.0),
                            product.getCategoryId(),
                            product.getCouponCode(),
                            product.getDiscount(),
                            product.getUntil()
                    ));



                }else {
                    sortedProducts.add(product);
                }
            }

        }else {
            sortedProducts = new ArrayList<>();

            for (Product product : products) {
                if (categoryService.checkCategory(product.getCategoryId()).equals(category) &&
                        product.getCouponCode() != null
                        && product.getDiscount() > 0
                        && product.getUntil() != null
                        && product.getUntil().isAfter(LocalDateTime.now())) {

                    sortedProducts.add(new Product(
                            product.getId(),
                            product.getName(),
                            (product.getPrice() * product.getDiscount() / 100.0),
                            product.getCategoryId(),
                            product.getCouponCode(),
                            product.getDiscount(),
                            product.getUntil()
                    ));

                }else if (categoryService.checkCategory(product.getCategoryId()).equals(category)){
                    sortedProducts.add(product);
                }
            }

        }
        if (price.equalsIgnoreCase("DESC") ) {
            sortedProducts.sort(Comparator.comparingDouble(Product::getPrice).reversed());
        } else if (price.equalsIgnoreCase("ASC")) {
            sortedProducts.sort(Comparator.comparingDouble(Product::getPrice));
        }


        return sortedProducts;
    }


    //__________________________________Extra method____________________________
    // get merchant products
    public ArrayList<Product> getMerchantProducts(String merchantId) {
        if (!merchantService.checkMerchant(merchantId)) {
            return null;
        }

        ArrayList<Product> merchantProducts = new ArrayList<Product>();
        for (MerchantStock merchantStock : merchantStockService.getMerchantStocks()) {

            if (merchantStock.getMerchantId().equals(merchantId)){

                for (Product product : products){
                    if (product.getId().equals(merchantStock.getProductId())){
                        merchantProducts.add(product);
                    }
                }
            }

        }


        return merchantProducts;
    }

    //_________________________Extra_________________
    //Combine stocks of one product
    public String getStocks(String productId){

        String response = checkRequirement("",productId);
        if (response.startsWith("Product"))
            return response;

        int stock=0;
        for (MerchantStock merchantStock: merchantStockService.getMerchantStocks()){

            if (merchantStock.getProductId().equals(productId)){
                stock+= merchantStock.getStock();
            }
        }



        return String.valueOf(stock);
    }


}

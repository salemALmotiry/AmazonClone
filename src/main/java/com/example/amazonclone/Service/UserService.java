package com.example.amazonclone.Service;


import com.example.amazonclone.Model.MerchantStock;
import com.example.amazonclone.Model.Product;
import com.example.amazonclone.Model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final MerchantStockService merchantStockService;
    private final ProductService productService;
    private final MerchantService merchantService;
    ArrayList<User> users = new ArrayList<>();




    public ArrayList<User> getUsers(){
        return this.users;
    }

    private String hash(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] hashBytes = messageDigest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unsupported algorithm: " + "MD5", e);
        }
    }


    public boolean addUser(User user){

        for (User tem:this.users){
            if (tem.getId().equals(user.getId())){
                return false;
            }
        }


        user.setPurchaseHistory(new ArrayList<Product>());
        user.setShippingCart(new ArrayList<Product>());
        user.setBuyNowAndPayLetter(new ArrayList<Double>());
        user.setPassword(this.hash(user.getPassword()));

        this.users.add(user);
        return true;
    }


    public boolean updateUser(String id , User user){

        for (int i = 0; i < this.users.size(); i++) {

            if (this.users.get(i).getId().equals(id)){
                this.users.set(i,user);
                return true;
            }

        }
        return false;
    }


    public boolean deleteUser(String id){

        for (User user:this.users){
            if (user.getId().equals(id)){
                this.users.remove(user);
                return true;
            }
        }

        return false;
    }

    public boolean checkUser(String id){
        for (User user:this.users){
            if (user.getId().equals(id)){
                return true;
            }
        }
        return false;
    }


    public String directPaying(String productId,String userId,String merchantId){
        ArrayList verifyStatus =  verifyTransaction(productId, userId, merchantId);

        User user = (User) verifyStatus.get(0);
        Product product = (Product) verifyStatus.get(1);
        String response = (String) verifyStatus.get(2);

        if (product == null || user == null || response != null){
            return response;
        }

        if (product.getPrice()>user.getBalance()){
            response = "Product price exceeds the user's balance.";
        }else {
            if(!merchantStockService.reStock(merchantId,-1)){
                response = "Item is out of stock";
            }else {
                user.setBalance(user.getBalance()-product.getPrice());
                user.getPurchaseHistory().add(product);
                response  = null;

            }
        }


        return response;
    }

    public String refund(String productId,String userId,String merchantId){

        ArrayList verifyStatus =  verifyTransaction(productId, userId, merchantId);

        User user = (User) verifyStatus.get(0);
        Product product = (Product) verifyStatus.get(1);
        String response = (String) verifyStatus.get(2);

        if (product == null || user == null || response != null){
            return response;
        }

        for(Product product1 :user.getPurchaseHistory()){
            if (product.getId().equals(product1.getId())){

                user.setBalance((user.getBalance() + product1.getPrice()));
                user.getPurchaseHistory().remove(product);
                return null;

            }
        }
        response = "User has not purchased this product and cannot request a refund";





        return response;
    }

    public String gifGift(String gifterid,String giftedId,String productId ,String merchantId){

        ArrayList verifyStatus =  verifyTransaction(productId, gifterid, merchantId);

        User user = (User) verifyStatus.get(0);
        Product product = (Product) verifyStatus.get(1);
        String response = (String) verifyStatus.get(2);

        if (product == null || user == null || response != null){
            return response;
        }



        if (product.getPrice()>user.getBalance()){
            response = "Product price exceeds the user's balance.";
        }else {
            if(!merchantStockService.reStock(merchantId,-1)){
                response = "Item is out of stock";
            }else {

                for(User temp : users){
                    if (temp.getId().equals(giftedId)){
                        user.setBalance(user.getBalance()-product.getPrice());
                        temp.getPurchaseHistory().add(product);
                        return null;
                    }
                }

                response = "gifted user not in the system.";
            }
        }






        return response;
    }


    public String installment(String productId,String userId,String merchantId){
        ArrayList verifyStatus =  verifyTransaction(productId, userId, merchantId);

        User user = (User) verifyStatus.get(0);
        Product product = (Product) verifyStatus.get(1);
        String response = (String) verifyStatus.get(2);

        if (product == null || user == null || response != null){
            return response;
        }


        if(user.getBuyNowAndPayLetter().isEmpty()){
            double singlePayment = product.getPrice()/4;

            if (singlePayment>user.getBalance()){
            response = "The first payment of"+singlePayment+" SAR exceeds the user's balance.";
        }else {
            if(!merchantStockService.reStock(merchantId,-1)){
                response = "Item is out of stock";
            }else {

                user.setBalance(user.getBalance()-singlePayment);
                user.getPurchaseHistory().add(product);
                user.setBuyNowAndPayLetter( new ArrayList<Double>(List.of(singlePayment,singlePayment,singlePayment)));
                response  = null;

                }
            }
        }else
            response = "The user already has "+user.getBuyNowAndPayLetter().size()+"-month payment plan of "+user.getBuyNowAndPayLetter().get(0)+" SAR";



        return response;
    }


    public String setDiscount(String userId,String productId,String categoryId,int percent,int until,String couponCode){


        if (percent>70){
            return "Discount is too high";
        }
        if (productService.checkRequirement(categoryId)!=null){
            return productService.checkRequirement(categoryId,productId);
        }

        for (User user : users){

          if (user.getId().equals(userId)){
              if (user.getRole().equals("Admin")){

                  for (Product product : productService.getProducts()){
                      if (product.getId().equals(productId)){
                          product.setDiscount(percent);
                          product.setCouponCode(couponCode);
                          product.setUntil(LocalDateTime.now().plusDays(until));
                      }
                          return null;
                      }
                  }
              }else {
                  return "User is not an admin and cannot apply the request.";
              }
          }





        return "User not found in the system";
    }

    //___________________________________Extra__________________________________
    //applying the discount by coupon
    public String payWithCoupon(String productId,String userId,String merchantId,String couponCode){
        ArrayList verifyStatus =  verifyTransaction(productId, userId, merchantId);

        User user = (User) verifyStatus.get(0);
        Product product = (Product) verifyStatus.get(1);
        String response = (String) verifyStatus.get(2);

        if (product == null || user == null || response != null){
            return response;
        }
        if (product.getCouponCode()!=null && !product.getCouponCode().equals(couponCode)){
            return "Coupon is invalid";
        }else if (product.getCouponCode()==null ){
            return "No discount on this product";
        }else if (product.getUntil().isBefore(LocalDateTime.now())){
            return "Coupon is expired";
        }

        if ((product.getPrice() * product.getDiscount() / 100.0)>user.getBalance()){
            response = "Product price exceeds the user's balance.";
        }else {
            if(!merchantStockService.reStock(merchantId,-1)){
                response = "Item is out of stock";
            }else {
                user.setBalance(user.getBalance()-(product.getPrice() * product.getDiscount() / 100.0));
                user.getPurchaseHistory().add(new Product(
                        product.getId(),
                        product.getName(),
                        (product.getPrice() * product.getDiscount() / 100.0),
                        product.getCategoryId(),
                        product.getCouponCode(),
                        product.getDiscount(),
                        product.getUntil()
                ));
                response  = null;

            }
        }


        return response;
    }

    private ArrayList verifyTransaction(String productId, String userId, String merchantId) {
        User user = null;
        String response = null;
        Product product = null;
        ArrayList temp = new ArrayList();

        for (User u : users){
            if(u.getId().equals(userId)){
                user = u;
            }
        }
        for (Product p : productService.getProducts()){

            if (p.getId().equals(productId)){
                product = p;
            }
        }

        if (!merchantService.checkMerchant(merchantId)){
            response =  "Merchant not found in the system.";
        }
        if (user == null)
            response = "User not in the system.";

        if (product == null)
            response = "Product not in the system.";

        temp.add(user);
        temp.add(product);
        temp.add(response);
        return temp;
    }

}

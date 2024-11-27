package com.example.amazonclone.Service;


import com.example.amazonclone.Model.Product;
import com.example.amazonclone.Model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        user.setPassword(this.hash(user.getPassword()));

        this.users.add(user);
        return true;
    }


    public boolean updateUser(String id , User user){

        for (int i = 0; i < this.users.size(); i++) {

            if (this.users.get(i).getId().equals(id)){
                if (user.getPurchaseHistory() != null) {
                    user.setPurchaseHistory(user.getPurchaseHistory());
                }else
                    user.setPurchaseHistory(new ArrayList<Product>());

                if (user.getPassword() != null) {
                    user.setPassword(this.hash(user.getPassword()));
                }
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

    public String gifGift(String gifterid,String giftedEmail,String productId ,String merchantId){

        String giftedId = null;
        for (User user:this.users){
            if (user.getEmail().equals(giftedEmail) && !user.getId().equals(gifterid)){
                giftedId = user.getId();
            }
        }
        if (giftedId == null){
            return "You cannot gift to yourself.";
        }


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

/**
 * Generates a list of recommended products for a user based on their purchase history.
 *
 * This method analyzes the purchase history of the user with the specified user ID,
 * identifies the most frequently purchased category, and recommends products from that
 * category that the user has not yet bought. The recommended products are returned
 * as a list of `Product` objects.
 **/

 public ArrayList<Product> recommendationProducts(String userId){
        ArrayList<Product> recommendationProducts = new ArrayList<>();
        User user = null;
        for (User u : users){
            if(u.getId().equals(userId)){
                user = u;
            }
        }


        assert user != null;
        if (user.getPurchaseHistory() == null || user.getPurchaseHistory().isEmpty()) {
            return null;
        }

        String mostRepeated = getMostRepeated();


        // Recommend products the user has not purchased.
        for (Product product : productService.getProducts()){
            if (product.getCategoryId().equals(mostRepeated) && !user.getPurchaseHistory().contains(product)){
                recommendationProducts.add(product);
            }

        }

        return recommendationProducts;
    }



/**
 * Generates a list of the best-selling products based on user purchase history.
 *
 * This method calculates the most frequently purchased products by all users and
 * returns a list of the top `limit` best-selling products. The products are ranked
 * by the number of times they have been purchased, in descending order.
 */
 public ArrayList<Product> bestSelling(int limit){
        ArrayList<Product> recommendationProducts = new ArrayList<>();

        // Product as the key and the number of times it has been purchased as the value
        Map<Product, Integer> productCountMap = new HashMap<>();

        // Loop through all users and their purchase history
        for (User user : users) {
            for (Product purchasedProduct : user.getPurchaseHistory()) {
                productCountMap.put(purchasedProduct, productCountMap.getOrDefault(purchasedProduct, 0) + 1);
            }
        }


        // Convert the map entries to a list and sort by count

        List<Map.Entry<Product, Integer>> sortedList = sort(productCountMap);


        int count = 0;
        for (Map.Entry<Product, Integer> entry : sortedList) {
            if (count >= limit && entry.getKey() !=null)
                break;
            Product product = entry.getKey();
            int productCount = entry.getValue();
            count++;
            recommendationProducts.add(product);
        }

        return recommendationProducts;
    }



//___________________________________Extra__________________________________
/**
 * Processes a payment for a product using a coupon code.
 *
 * This method performs a transaction check to verify that the product, user, and
 * coupon code are valid. If valid, it applies the coupon discount and deducts the
 * discounted price from the user's balance. It also checks if the product is in stock.
 */

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



    //___________________________________________________________________________________-

    private String getMostRepeated() {
        String mostRepeated = "";
        int maxCount = 0;

        for (Product currentProduct : productService.getProducts()) {
            int count = 0;

            for (Product product : productService.getProducts()) {
                if (currentProduct.getCategoryId().equals(product.getCategoryId())) {
                    count++;
                }
            }

            if (count > maxCount) {
                mostRepeated = currentProduct.getCategoryId();
                maxCount = count;
            }
        }
        return mostRepeated;
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

    private List<Map.Entry<Product, Integer>> sort(Map<Product, Integer> productCountMap) {
        List<Map.Entry<Product, Integer>> sortedList = new ArrayList<>(productCountMap.entrySet());

        for (int i = 0; i < sortedList.size(); i++) {
            int maxIndex = i;
            for (int j = i + 1; j < sortedList.size(); j++) {
                if (sortedList.get(j).getValue() > sortedList.get(maxIndex).getValue()) {
                    maxIndex = j;
                }
            }

            if (maxIndex != i) {
                Map.Entry<Product, Integer> temp = sortedList.get(i);
                sortedList.set(i, sortedList.get(maxIndex));
                sortedList.set(maxIndex, temp);
            }
        }
        return sortedList;
    }




}

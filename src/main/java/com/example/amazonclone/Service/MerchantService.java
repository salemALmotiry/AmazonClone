package com.example.amazonclone.Service;


import com.example.amazonclone.Model.Merchant;
import com.example.amazonclone.Model.Product;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@AllArgsConstructor
public class MerchantService {


    ArrayList<Merchant> merchants = new ArrayList<>();




    public ArrayList<Merchant> getMerchants(){
        return this.merchants;
    }

    public boolean addMerchant(Merchant merchant){

        for (Merchant tem:this.merchants){
            if (tem.getId().equals(merchant.getId())){
                return false;
            }
        }

        this.merchants.add(merchant);
        return true;
    }


    public boolean updateMerchant(String id , Merchant merchant){

        for (int i = 0; i < this.merchants.size(); i++) {

            if (this.merchants.get(i).getId().equals(id)){
                this.merchants.set(i,merchant);
                return true;
            }

        }
        return false;
    }


    public boolean deleteMerchant(String id){

        for (Merchant Merchant:this.merchants){
            if (Merchant.getId().equals(id)){
                this.merchants.remove(Merchant);
                return true;
            }
        }

        return false;
    }

    public boolean checkMerchant(String id){
        for (Merchant Merchant:this.merchants){
            if (Merchant.getId().equals(id)){
                return true;
            }
        }
        return false;
    }




}

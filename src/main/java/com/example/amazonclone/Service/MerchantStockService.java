package com.example.amazonclone.Service;

import com.example.amazonclone.Model.MerchantStock;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@AllArgsConstructor
public class MerchantStockService {



    ArrayList<MerchantStock> merchantStocks = new ArrayList<>();


    public ArrayList<MerchantStock> getMerchantStocks(){
        return this.merchantStocks;
    }

    public boolean addMerchantStock(MerchantStock merchantStock){

        for (MerchantStock tem:this.merchantStocks){
            if (tem.getId().equals(merchantStock.getId())){
                return false;
            }
        }

        this.merchantStocks.add(merchantStock);
        return true;
    }


    public boolean updateMerchantStock(String id , MerchantStock merchantStock){

        for (int i = 0; i < this.merchantStocks.size(); i++) {

            if (this.merchantStocks.get(i).getId().equals(id)){
                this.merchantStocks.set(i,merchantStock);
                return true;
            }

        }
        return false;
    }


    public boolean deleteMerchantStock(String id){

        for (MerchantStock MerchantStock:this.merchantStocks){
            if (MerchantStock.getId().equals(id)){
                this.merchantStocks.remove(MerchantStock);
                return true;
            }
        }

        return false;
    }

    public boolean reStock(String merchantId,int amount){

        for (MerchantStock merchantStock:this.merchantStocks){

            if (merchantStock.getMerchantId().equals(merchantId) && (merchantStock.getStock() + (amount) >=0)){

                    merchantStock.setStock(merchantStock.getStock() + (amount));
                    return true;

            }
        }
        return false;
    }










}

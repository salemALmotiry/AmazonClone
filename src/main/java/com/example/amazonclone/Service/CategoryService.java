package com.example.amazonclone.Service;

import com.example.amazonclone.Model.Category;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CategoryService {


    ArrayList<Category> categories = new ArrayList<>();


    public ArrayList<Category> getCategories(){
        return this.categories;
    }

    public boolean addCategory(Category category){

        for (Category tem:this.categories){
            if (tem.getId().equals(category.getId())){
                return false;
            }
        }

        this.categories.add(category);
        return true;
    }


    public boolean updateCategory(String id , Category category){

        for (int i = 0; i < this.categories.size(); i++) {

            if (this.categories.get(i).getId().equals(id)){
                this.categories.set(i,category);
                return true;
            }

        }
        return false;
    }


    public boolean deleteCategory(String id){

        for (Category Category:this.categories){
            if (Category.getId().equals(id)){
                this.categories.remove(Category);
                return true;
            }
        }

        return false;
    }

    public String checkCategory(String categoryId){

        for (Category category: categories){
            if (category.getId().equals(categoryId)){
                return category.getName();
            }
        }
        return null;
    }


}

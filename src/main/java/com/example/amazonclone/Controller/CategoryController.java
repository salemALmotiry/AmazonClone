package com.example.amazonclone.Controller;

import com.example.amazonclone.ApiResponse.ApiResponse;
import com.example.amazonclone.Model.Category;
import com.example.amazonclone.Service.CategoryService;
import com.example.amazonclone.Service.CategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/amazon-clone/category")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/get")
    public ResponseEntity getCategories(){

        if (categoryService.getCategories().isEmpty()){
            return ResponseEntity.status(200).body(new ApiResponse("There are no categories in the system."));
        }
        return ResponseEntity.status(200).body(categoryService.getCategories());
    }


    @PostMapping("/add")
    public ResponseEntity addCategory(@RequestBody @Valid Category category, Errors errors){

        if (errors.hasErrors()){
            return ResponseEntity.status(400).body(errors.getFieldError().getDefaultMessage());

        }

        boolean isAdded = categoryService.addCategory(category);
        if (isAdded){
            return ResponseEntity.status(200).body(new ApiResponse("New Category successfully added"));

        }
        return ResponseEntity.status(400).body(new ApiResponse("Category already in the system"));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity updateCategory(@PathVariable String id, @RequestBody @Valid Category category, Errors errors){

        if (errors.hasErrors()){
            return ResponseEntity.status(400).body(errors.getFieldError().getDefaultMessage());

        }

        boolean isUpdated = categoryService.updateCategory(id,category);

        if (isUpdated){
            return ResponseEntity.status(200).body(new ApiResponse("Category successfully updated"));

        }

        return ResponseEntity.status(400).body(new ApiResponse("Category not found in the system"));
    }



    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteCategory(@PathVariable String id){

        boolean isDeleted = categoryService.deleteCategory(id);

        if (isDeleted){
            return ResponseEntity.status(200).body(new ApiResponse("Category successfully deleted"));

        }

        return ResponseEntity.status(400).body(new ApiResponse("Category not found in the system"));
    }

}

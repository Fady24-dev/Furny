package com.example.furniture.models;

public class Products {
    private String name,price,category,image,pid,model;

    public Products() {
    }

    public Products(String name, String price, String category, String image, String pid,String model) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.image = image;
        this.pid = pid;
        this.model=model;
    }



    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}

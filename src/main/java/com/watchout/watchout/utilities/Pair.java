package com.watchout.watchout.utilities;


import com.watchout.watchout.entities.Product;


public class Pair  {

    private Product product;
    private int qty;
    
    public Product getProduct(){
        return product;
    }
    public void setProduct(Product product){
        this.product = product;
    }
    public int getQuantity(){
        return qty;
    }
    public void setQuantity(int qty){
        this.qty = qty;
    }
}

package com.watchout.watchout.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "customer")
public class Customer extends User{
	@OneToMany(fetch =FetchType.LAZY)
	List<Product> favorite;
	@OneToMany(cascade = CascadeType.ALL, fetch =FetchType.LAZY)
	List<Order> orders;
	public List<Product> getFavorite(){
		return this.favorite;
	}
	public void setFavorite(List<Product> favorite){
		this.favorite = favorite;
	}
	public List<Order> getOrders(){
		return this.orders;
	}
	public void setOrders(List<Order> orders){
		this.orders = orders;
	}
	public void addFavorite(Product product){
		if(favorite==null){
			favorite = new ArrayList<Product>();
		}
		favorite.add(product);
	}
}

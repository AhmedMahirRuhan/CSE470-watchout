package com.watchout.watchout.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "shop")
public class Shop {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private int id;
	private String name;
	private String description;
	@ManyToMany(cascade = CascadeType.ALL,fetch =FetchType.LAZY)
	private List<ShopWiseOrder> orders;
	@OneToMany(cascade = CascadeType.ALL,fetch =FetchType.LAZY)
	private List<Product> products;
	private boolean approved_status;
	@OneToMany(cascade = CascadeType.ALL, fetch =FetchType.LAZY)
	private List<Coupon> coupons;
	public List<Coupon> getCoupons() {
		return coupons;
	}
	public void setCoupons(List<Coupon> coupons) {
		this.coupons = coupons;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<Product> getProducts() {
		return products;
	}
	public void setProducts(List<Product> products) {
		this.products = products;
	}
	public List<ShopWiseOrder> getOrders() {
		return orders;
	}
	public void setOrders(List<ShopWiseOrder> orders) {
		this.orders = orders;
	}
	public boolean getApproved_status(){
		return approved_status;
	}
	public void setApproved_status(boolean approved_status){
		this.approved_status = approved_status;
	}
}

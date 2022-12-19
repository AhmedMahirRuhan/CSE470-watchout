package com.watchout.watchout.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "shopowner")
public class ShopOwner extends User{
	@OneToOne(cascade = CascadeType.ALL, fetch =FetchType.LAZY)
	private Shop shop;
	public Shop getShop() {
		return shop;
	}
	public void setShop(Shop shop) {
		this.shop = shop;
	}

}

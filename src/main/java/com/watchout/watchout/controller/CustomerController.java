package com.watchout.watchout.controller;


import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import com.watchout.watchout.dao.CustomerRepository;
import com.watchout.watchout.dao.OrderRepository;
import com.watchout.watchout.dao.ProductsRepository;
import com.watchout.watchout.dao.ShopRepository;
import com.watchout.watchout.entities.Coupon;
import com.watchout.watchout.entities.Customer;
import com.watchout.watchout.entities.Order;
import com.watchout.watchout.entities.Product;
import com.watchout.watchout.entities.Shop;
import com.watchout.watchout.entities.ShopWiseOrder;
import com.watchout.watchout.utilities.Cart;
import com.watchout.watchout.utilities.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
	private CustomerRepository customerRepository;
	@Autowired 
	private ProductsRepository productsRepository;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private ShopRepository shopRepository;
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal, HttpSession session){
        String email = principal.getName();
        Customer customer = customerRepository.getUserByEmail(email);
		Cart cart = (Cart)session.getAttribute("cart");
		customerRepository.save(customer);
		List<Order> orders = customer.getOrders();
		model.addAttribute("cart", cart);
		// System.out.println(orders.size());
		model.addAttribute("orders", orders);
        model.addAttribute("title", "dashboard");
        model.addAttribute("user", customer);
		return "dashboard";
    }
	@GetMapping(value=("/add-favorite/{id}"))
	public RedirectView add_to_favorite(@PathVariable int id, Model model, Principal principal, HttpSession session){
		String email = principal.getName();
		Customer customer =  customerRepository.getUserByEmail(email);
		Product product = productsRepository.getReferenceById(id);
		customer.addFavorite(product);
		Cart cart = (Cart)session.getAttribute("cart");
		customerRepository.save(customer);
		model.addAttribute("cart", cart);
		model.addAttribute("user", customer);
		return new RedirectView("/customer/dashboard/");
	}
	@GetMapping("/delete-favorite/{id}")
	public RedirectView view_favorite(@PathVariable int id, Model model, Principal principal,HttpServletRequest request){
		String email = principal.getName();
		Customer customer = customerRepository.getUserByEmail(email);
		List<Product> favorite = customer.getFavorite();
		// System.out.println(request.getRequestURI());
		for(Product product: favorite){
			if(product.getId()==id){
				favorite.remove(product);
				break;
			}
		}
		customerRepository.save(customer);
		model.addAttribute("user", customer);
		model.addAttribute("title", "favorite");
		return new RedirectView("/customer/dashboard");
	}
	@GetMapping("/checkout")
	public String checkout(Model model, Principal principal, HttpSession session){
		Customer customer = customerRepository.findByEmail(principal.getName());
		Cart cart = (Cart) session.getAttribute("cart");
		List<Pair> pairs = cart.getProducts();
		cart.getTotal_after_charges();
		model.addAttribute("cart", cart);
		model.addAttribute("pairs", pairs);
		model.addAttribute("title", "checkout");
		model.addAttribute("user",customer);
		return "checkout";
	}
	

	@PostMapping("/confirm-checkout")
	public String confirm_checkout(@RequestParam String name, @RequestParam String address, @RequestParam String number,  Model model, Principal principal, HttpSession session){
		Customer customer = customerRepository.findByEmail(principal.getName());
		Cart cart = (Cart) session.getAttribute("cart");
		Coupon coupon = cart.getCoupon();
		List<Pair> pairs = cart.getProducts();
		Order order = new Order();
		
		List<Product> products = new ArrayList<>();
		int quantity = 0;
		double total = cart.getTotal_after_charges();
		HashMap<Integer, List<Product>> shop_product = new HashMap<>();
		HashMap<Integer, Integer> shop_qty = new HashMap<>();
		for (Pair pair : pairs) {
			Product product = pair.getProduct();
			product.setQuantity(product.getQuantity() - pair.getQuantity());
			this.productsRepository.save(product);
			int shopID = product.getShop().getId();
			int qty = pair.getQuantity();
			// for total order
			products.add(product);
			quantity += qty;

			// creating hashmap value for each key
			List<Product> shop_product_list = shop_product.get(shopID);
			;
			// if key doesnt exist set new list
			if (shop_product_list == null) {
				shop_product_list = new ArrayList<>();
			}
			// add product to value in hashmap
			shop_product_list.add(product);
			shop_product.put(shopID, shop_product_list);
			if (shop_qty.get(shopID) == null)
				shop_qty.put(shopID, qty);
			else
				shop_qty.put(shopID, shop_qty.get(shopID) + qty);
		}
		for (int shopID : shop_product.keySet()) {
			ShopWiseOrder shop_wise_order = new ShopWiseOrder();
			Shop shop = shopRepository.getReferenceById(shopID);
			shop_wise_order.setProducts(shop_product.get(shopID));
			shop_wise_order.setQuantity(shop_qty.get(shopID));
			List<ShopWiseOrder> current_orders = shop.getOrders();
			if (current_orders == null) {
				current_orders = new ArrayList<>();
			}
			int total_shop_wise = 0;
			for (Product product : shop_product.get(shopID)) {
				total_shop_wise += product.getPrice();
			}
			if (coupon != null) {
				List<Coupon> shop_coupons = shop.getCoupons();
				for (Coupon coup : shop_coupons) {
					if (coup.getName().equals(coupon.getName())) {
						total_shop_wise -= total_shop_wise * coupon.getPercentage();
						break;
					}
				}
			}
			shop_wise_order.setTotal(total_shop_wise);
			shop_wise_order.setAddress(address);
			shop_wise_order.setName(name);
			shop_wise_order.setNumber(number);
			current_orders.add(shop_wise_order);
		}
		List<Order> customer_orders = customer.getOrders();
		if (customer_orders == null) {
			customer_orders = new ArrayList<>();
		}
		customer_orders.add(order);
		order.setProducts(products);
		order.setQuantity(quantity);
		order.setTotal(total);
		order.setAddress(address);
		order.setName(name);
		order.setNumber(number);
		orderRepository.save(order);
		session.removeAttribute("cart");
		List<Order> orders = customer.getOrders();
		model.addAttribute("orders", orders);
		model.addAttribute("title", "confirmed");
		model.addAttribute("user", customer);
		return "dashboard";
	}
	@GetMapping("/order/{id}")
    public String single_order(Model model, Principal principal, @PathVariable int id){
        String email = principal.getName();
		Customer customer = customerRepository.getUserByEmail(email);
        Order order = orderRepository.getReferenceById(id);
        List<Product> products = order.getProducts();
        model.addAttribute("user", customer);
        model.addAttribute("products", products);
        model.addAttribute("order", order);
        model.addAttribute("title", "Invoice");
        return "customer_single_order";
    }
}

package com.watchout.watchout.controller;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import com.watchout.watchout.dao.AdminRepository;
import com.watchout.watchout.dao.CategoryRepository;
import com.watchout.watchout.dao.CustomerRepository;
import com.watchout.watchout.dao.ProductsRepository;
import com.watchout.watchout.dao.ShopOwnerRepository;
import com.watchout.watchout.dao.ShopRepository;
import com.watchout.watchout.dao.UserRepository;
import com.watchout.watchout.entities.Admin;
import com.watchout.watchout.entities.Category;
import com.watchout.watchout.entities.Customer;
import com.watchout.watchout.entities.Product;
import com.watchout.watchout.entities.Shop;
import com.watchout.watchout.entities.ShopOwner;
import com.watchout.watchout.entities.User;
import com.watchout.watchout.utilities.Cart;
import com.watchout.watchout.utilities.Message;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Controller
public class MainController {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private ProductsRepository productsRepository;
	@Autowired
	private ShopOwnerRepository shopownerRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired 
	private ShopRepository shopRepository;
	@Autowired
	private AdminRepository adminRepository;
	@GetMapping("/")
	public String home(Model model, Principal principal, HttpSession session) {
		model.addAttribute("title", "Watch Out");
		try{
			String email = principal.getName();
			User user = userRepository.getUserByEmail(email);
			model.addAttribute("user", user);
		}
		catch(Exception e){

		}
		Object user = isLogged(principal);
		List<Category> categories = categoryRepository.findAll();
		model.addAttribute("categories", categories);
		model.addAttribute("user", user);
		Cart cart =(Cart) session.getAttribute("cart");
		List<Product> products;
		products = productsRepository.findAll();
		model.addAttribute("cart", cart);
		model.addAttribute("products", products);
		List<Product> featured = new ArrayList<>();
		for(Product product: products){
			if(product.getId()==15 ){
				featured.add(product);
			}
			if(product.getId()==17 ){
				featured.add(product);
			}
			if(product.getId()==19 ){
				featured.add(product);
			}
			if(product.getId()==22 ){
				featured.add(product);
			}
		}
		model.addAttribute("featured", featured);




		model.addAttribute("title", "Watch Out");
		return "home";
	}
	public Object isLogged(Principal principal){
		try{
			String email = principal.getName();
			User user = userRepository.getUserByEmail(email);
			return user;
		}
		catch(Exception e){
			// System.out.println(e);
			return null;
		}
	}
	@GetMapping("/login")
	public String login(Model model, Principal principal){
		if (principal!=null) {
			return "redirect:/";
		}
		model.addAttribute("title","login");
		return "login";
		// if(isLogged(principal)==null){
		// 	model.addAttribute("title", "login");
		// 	return new RedirectView("login");
		// }
		// else{
		// 	String email = principal.getName();
        // 	Customer customer = customerRepository.getUserByEmail(email);
        // 	System.out.println(customer.getName());
        // 	model.addAttribute("title", "dashboard");
        // 	model.addAttribute("customer", customer);
		// 	return new RedirectView("dashboard");
		// }
	}
	
	@GetMapping("/login-error")
	public String login_fail(Model model,HttpSession session){
		model.addAttribute("title","login");
		session.setAttribute("message",new Message("email/password incorrect","notification is-danger"));
		return "login";
	}
	@GetMapping(value = ("/signup/{type}"))
	public String signup(@PathVariable String type, Model model){
		model.addAttribute("title", "signup");
		model.addAttribute("type", type);
		return "signup";
	}
	@RequestMapping(value = ("/signup/{type}"), method=RequestMethod.POST)
	private RedirectView ProcessSignup(@RequestParam("fullname") String fullname, @RequestParam("email") String email,
			@RequestParam("password") String password,@PathVariable String type, Model model,
			HttpSession session) {
		User user;
		if(type.equals("ROLE_SHOP")){
			user = new ShopOwner();
		}
		else if(type.equals("ROLE_CUSTOMER")){
			user = new Customer();
		}
		else{
			user = new Admin();
		}
		user.setName(fullname);
		user.setEmail(email);
		try {
			if(customerRepository.getUserByEmail(email)!=null) {
				throw new Exception("user email already exists");
			}
			if(password.length()<6) {
				throw new Exception("password length must be at least 6");
			}
			user.setPassword(passwordEncoder.encode(password));
			user.setRole(type);
			if(type.equals("ROLE_SHOP")){
				Shop shop = new Shop();
				ShopOwner shopowner = (ShopOwner)user;
				shopowner.setShop(shop);
				this.shopownerRepository.save(shopowner);
			}
			else if(type.equals("ROLE_CUSTOMER")){
				this.customerRepository.save((Customer)user);
			} 
			else{
				this.adminRepository.save((Admin)user);
			}
			model.addAttribute("user",user);
			session.setAttribute("message",new Message("Successfully registered! ","notification is-success"));
			return new RedirectView("/login");
		}
		catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message",new Message(e.getMessage(),"notification is-danger"));
			return new RedirectView("/signup/"+type);
		}
	}
	
	@GetMapping("/selectsignup")
	public String selectSignup(Model model){
		model.addAttribute("title", "Select Option");
		return "selectsignup";
	}
	@GetMapping("/products")
	public String products(Model model, Principal principal, HttpSession session){
		List<Product> allProduct = productsRepository.findAll();
		Object user = isLogged(principal);
		List<Category> categories = categoryRepository.findAll();
		model.addAttribute("categories", categories);
		model.addAttribute("user", user);
		model.addAttribute("title", "products");
		model.addAttribute("products", allProduct);
		// System.out.println(allProduct);
		Cart cart =(Cart) session.getAttribute("cart");
		model.addAttribute("cart",cart);
		return "products";
	}
	@PostMapping(value=("/products"))
	public String search_products(Model model, Principal principal, @RequestParam(required = false) String name,
	@RequestParam(required = false) String sort, @RequestParam(required = false) String search_type, @RequestParam(required = false) String category){
		List<Product> allProduct = productsRepository.findAll();
		Object user = isLogged(principal);
		if(search_type.equals("Search Product")){
			List<Product> query_product = new ArrayList<Product>();
			if(name.equals("") && category==null){
				query_product = productsRepository.findAll();
			}
			else if(!name.equals("")){
				// System.out.println("this");
				for (Product product : allProduct) {
					if(product.getName().toLowerCase().contains(name.toLowerCase())){
						query_product.add(product);
					}
				}
			}
			else if(category!=null){
				for (Product product : allProduct) {
					if(product.getCategory().getName().equals(category)) {
						// System.out.println(product.getName());
						query_product.add(product);
					}
				}
			}
			// System.out.println(name+" "+category+" "+sort);
			if(sort != null){
				if(sort.equals("Low"))
					query_product = sort_by_price_asc(query_product);
				else query_product = sort_by_price_desc(query_product);
			}
			model.addAttribute("products", query_product);
		}
		else{
			List<Shop> allshops =shopRepository.findAll();
			List<Shop> query_shop = new ArrayList<Shop>();
			for(Shop shop:allshops){
				if(shop.getName()!=null && shop.getName().toLowerCase().contains(name.toLowerCase())){
					query_shop.add(shop);
				}
			}
			model.addAttribute("shops", query_shop);
		}
		List<Category> categories = categoryRepository.findAll();
		model.addAttribute("categories", categories);
		model.addAttribute("user", user);
		model.addAttribute("title", "search");
		// System.out.println(allProduct);
		return "products";
	}
	public List<Product> sort_by_price_asc(List<Product> products){
		products.sort((p1,p2)->p1.getPrice().compareTo(p2.getPrice()));
		return products;
	}
	public List<Product> sort_by_price_desc(List<Product> products){
		products.sort((p1,p2)->p2.getPrice().compareTo(p1.getPrice()));
		return products;
	}
	@GetMapping(value = ("/product/{id}"))
	public String singleProduct(@PathVariable int id, Model model, Principal principal, HttpSession session){
		Product product = productsRepository.getReferenceById(id);
		Object customer = isLogged(principal);
		Cart cart = (Cart) session.getAttribute("cart");
		model.addAttribute("cart", cart);
		model.addAttribute("customer", customer);
		model.addAttribute("product",product);
		return "singleproduct";
	}
	@GetMapping("/shops")
	public String shops(Model model, Principal principal, HttpSession session){
		List<Shop> allShop = shopRepository.findAll();
		Object user = isLogged(principal);
		model.addAttribute("user", user);
		model.addAttribute("title", "Shops");
		model.addAttribute("shops", allShop);
		Cart cart =(Cart) session.getAttribute("cart");
		model.addAttribute("cart",cart);
		return "shops";
	}
	@GetMapping(value = ("/shops/{id}"))
	public String singleShop(@PathVariable int id, Model model, Principal principal, HttpSession session){
		Shop shop = shopRepository.getReferenceById(id);
		Object user = isLogged(principal);
		Cart cart = (Cart) session.getAttribute("cart");
		model.addAttribute("cart", cart);
		model.addAttribute("title", "Phones");
		model.addAttribute("user", user);
		List<Product> products;
		products = productsRepository.findAll();
		model.addAttribute("cart", cart);
		model.addAttribute("products", products);
		List<Product> featured = new ArrayList<>();
		for(Product product: products){
			if(product.getShop().getId()==id){
				featured.add(product);
			}
		}
		model.addAttribute("featured",featured);
		model.addAttribute("shop",shop);
		return "singleshop";
	}
	@GetMapping(value = ("/analog"))
	public String analog(Model model, Principal principal, HttpSession session){
		Object user = isLogged(principal);
		Cart cart = (Cart) session.getAttribute("cart");
		model.addAttribute("cart", cart);
		model.addAttribute("title", "Analog");
		model.addAttribute("user", user);
		List<Product> products;
		products = productsRepository.findAll();
		model.addAttribute("cart", cart);
		model.addAttribute("products", products);
		List<Product> featured = new ArrayList<>();
		for(Product product: products){
			if(product.getCategory().getId()==3){
				featured.add(product);
			}
		}
		model.addAttribute("featured", featured);
		return "analog";
	}
	@GetMapping(value = ("/digital"))
	public String digital(Model model, Principal principal, HttpSession session){

		Object user = isLogged(principal);
		Cart cart = (Cart) session.getAttribute("cart");
		model.addAttribute("cart", cart);
		model.addAttribute("title", "Digital");
		model.addAttribute("user", user);
		List<Product> products;
		products = productsRepository.findAll();
		model.addAttribute("cart", cart);
		model.addAttribute("products", products);
		List<Product> featured = new ArrayList<>();
		for(Product product: products){
			if(product.getCategory().getId()==4){
				featured.add(product);
			}
		}
		model.addAttribute("featured", featured);
		return "digital";
	}
	@GetMapping(value = ("/smart"))
	public String smart(Model model, Principal principal, HttpSession session){
		Object user = isLogged(principal);
		Cart cart = (Cart) session.getAttribute("cart");
		model.addAttribute("cart", cart);
		model.addAttribute("title", "Smart");
		model.addAttribute("user", user);
		List<Product> products;
		products = productsRepository.findAll();
		model.addAttribute("cart", cart);
		model.addAttribute("products", products);
		List<Product> featured = new ArrayList<>();
		for(Product product: products){
			if(product.getCategory().getId()==5){
				featured.add(product);
			}
		}
		model.addAttribute("featured", featured);
		return "smart";
	}
	@GetMapping(value = ("/wall"))
	public String wall(Model model, Principal principal, HttpSession session){
		Object user = isLogged(principal);
		Cart cart = (Cart) session.getAttribute("cart");
		model.addAttribute("cart", cart);
		model.addAttribute("title", "Wall Clock");
		model.addAttribute("user", user);
		List<Product> products;
		products = productsRepository.findAll();
		model.addAttribute("cart", cart);
		model.addAttribute("products", products);
		List<Product> featured = new ArrayList<>();
		for(Product product: products){
			if(product.getCategory().getId()==6){
				featured.add(product);
			}
		}
		model.addAttribute("featured", featured);
		return "wall";
	}
}


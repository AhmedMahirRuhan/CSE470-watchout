package com.watchout.watchout.controller;


import java.security.Principal;
import java.util.List;

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

import com.watchout.watchout.dao.AdminRepository;
import com.watchout.watchout.dao.CategoryRepository;
import com.watchout.watchout.dao.OrderRepository;
import com.watchout.watchout.dao.ShopRepository;
import com.watchout.watchout.entities.Admin;
import com.watchout.watchout.entities.Category;
import com.watchout.watchout.entities.Order;
import com.watchout.watchout.entities.Product;
import com.watchout.watchout.entities.Shop;
import com.watchout.watchout.utilities.Message;


@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private OrderRepository orderRepository;
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal){
        String email = principal.getName();
        Admin user = adminRepository.getUserByEmail(email); 
        List<Shop> shops = shopRepository.findAll();
        List<Order> orders = orderRepository.findAll();
        model.addAttribute("title", "orders");
        model.addAttribute("user",user);
        model.addAttribute("orders", orders);
        model.addAttribute("type", "admin");
        model.addAttribute("shops", shops);
        model.addAttribute("title", "admin panel");
        model.addAttribute("user",user);
        return "view_orders";
    }
    @GetMapping("/add-category")
    public String addCategory(Model model,Principal principal){
        String email = principal.getName();
        Admin user = adminRepository.getUserByEmail(email);
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("title", "add category");
        model.addAttribute("categories", categories);
        model.addAttribute("user",user);
        return "addcategory";
    }
    @PostMapping("/add-category")
    public String saveCategory(@RequestParam("name") String name, Model model,Principal principal, HttpSession session){
        String email = principal.getName();
        Admin user = adminRepository.getUserByEmail(email);
        Category category = new Category();
        category.setName(name);
        this.categoryRepository.save(category);
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("title", "add category");
        model.addAttribute("user",user);
        model.addAttribute("categories", categories);
        session.setAttribute("message",new Message("category added: "+name,"notification is-success"));
        return "addcategory";
    }
    @GetMapping("/all-shops")
    public String allShops(Model model, Principal principal){
        String email = principal.getName();
        Admin user = adminRepository.getUserByEmail(email); 
        List<Shop> shops = shopRepository.findAll();
        model.addAttribute("title", "add category");
        model.addAttribute("user",user);
        model.addAttribute("shops", shops);
        return "allshops";
    }
    @GetMapping("/pending-shops")
    public String pendingshops(Model model, Principal principal){
        String email = principal.getName();
        Admin user = adminRepository.getUserByEmail(email); 
        List<Shop> shops = shopRepository.findAll();
        model.addAttribute("title", "shop status");
        model.addAttribute("user",user);
        model.addAttribute("shops", shops);
        return "pendingshops";
    }
    @PostMapping(value=("/update_status/{id}"))
    public RedirectView update_status(Model model, Principal principal, @PathVariable int id,@RequestParam("approved_status") String approved_status){
        String email = principal.getName();
        Admin user = adminRepository.getUserByEmail(email); 
        Shop shop = shopRepository.getReferenceById(id);
        shop.setApproved_status(approved_status.equals("True")?true:false);
        shopRepository.save(shop);
        List<Shop> shops = shopRepository.findAll();
        model.addAttribute("title", "shop status");
        model.addAttribute("user",user);
        model.addAttribute("shops", shops);
        return new RedirectView("/admin/pending-shops");
    }
    @GetMapping("/view-order")
    public String view_orders(Model model, Principal principal){
        String email = principal.getName();
        Admin user = adminRepository.getUserByEmail(email); 
        List<Shop> shops = shopRepository.findAll();
        List<Order> orders = orderRepository.findAll();
        model.addAttribute("title", "orders");
        model.addAttribute("user",user);
        model.addAttribute("orders", orders);
        model.addAttribute("type", "admin");
        model.addAttribute("shops", shops);
        return "view_orders";
    }
    @GetMapping("signup")
    public String admin_signup(Model model){
        model.addAttribute("title", "signup");
		return "signup";
    }
    @GetMapping("/order/{id}")
    public String single_order(Model model, Principal principal, @PathVariable int id){
        String email = principal.getName();
        Admin user = adminRepository.getUserByEmail(email);
        Order order = orderRepository.getReferenceById(id);
        List<Product> products = order.getProducts();
        model.addAttribute("user", user);
        model.addAttribute("products", products);
        model.addAttribute("order", order);
        model.addAttribute("title", "order items");
        return "admin_single_order";
    }
    @PostMapping("/order/{id}")
    public String set_single_order(Model model, Principal principal, @PathVariable int id,
    @RequestParam("status") String status, @RequestParam("delivery_date") String delivery_date, HttpSession session){
        String email = principal.getName();
        Admin user = adminRepository.getUserByEmail(email);
        Order order = orderRepository.getReferenceById(id);
        // System.out.println(delivery_date);
        order.setDelivery_date(delivery_date);
        order.setStatus(status);
        this.orderRepository.save(order);
        List<Product> products = order.getProducts();
        model.addAttribute("user", user);
        model.addAttribute("products", products);
        model.addAttribute("order", order);
        session.setAttribute("message",new Message("order updated: ","notification is-success"));
        model.addAttribute("title", "order items");
        return "admin_single_order";
    }
}



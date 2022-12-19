package com.watchout.watchout.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.watchout.watchout.dao.UserRepository;
import com.watchout.watchout.entities.User;

import org.springframework.security.core.userdetails.UserDetails;

@Configuration
@EnableWebSecurity
public class CustomerConfig extends WebSecurityConfigurerAdapter{
	@Autowired
	private UserRepository userRepository;
	@Bean
	public UserDetailsService getUserDetailService() {
		return new UserDetailsServiceImpl();
	}
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	@Bean
	public DaoAuthenticationProvider authenticationProvide() {
		DaoAuthenticationProvider daoAuthenticationProvider =  new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(this.getUserDetailService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return daoAuthenticationProvider;
	}
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvide());
	}
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// setting up role based permission
		http.csrf().disable().authorizeRequests().antMatchers("/admin/**").hasRole("ADMIN")
		.antMatchers("/customer/**").hasRole("CUSTOMER")
		.antMatchers("/shop/**").hasRole("SHOP")
		.antMatchers("/**").permitAll().and().formLogin().loginPage("/login").failureUrl("/login-error")
		.successHandler((request, response, authentication) -> {
			// run custom logics upon successful login
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String email = userDetails.getUsername();
			User user = userRepository.getUserByEmail(email);
			String role = user.getRole();
			String redirectURL = request.getContextPath();

			if (role.equals("ROLE_CUSTOMER")) {
				redirectURL = "customer/dashboard";
			} else if (role.equals("ROLE_SHOP")) {
				redirectURL = "shop/dashboard";
			}
			else if(role.equals("ROLE_ADMIN")){
				redirectURL = "admin/dashboard";
			}
			response.sendRedirect(redirectURL);
		}).and().exceptionHandling().accessDeniedPage("/login");
	}
}



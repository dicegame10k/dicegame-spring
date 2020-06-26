package com.cb.dicegame.config;

import com.cb.dicegame.util.DiceGameUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class DiceGameWebSecurityConfig extends WebSecurityConfigurerAdapter {

	private DiceGameUserDetailsService userDetailsService;
	private DiceGameLogoutSuccessHandler diceGameLogoutSuccessHandler;

	@Autowired
	public DiceGameWebSecurityConfig(DiceGameUserDetailsService userDetailsService,
			DiceGameLogoutSuccessHandler diceGameLogoutSuccessHandler) {
		this.userDetailsService = userDetailsService;
		this.diceGameLogoutSuccessHandler = diceGameLogoutSuccessHandler;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.userDetailsService(this.userDetailsService)
			.passwordEncoder(DiceGameUtil.getPasswordEncoder());
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/built/**", "/styles/**", "/images/**").permitAll()
				.antMatchers(HttpMethod.POST, "/signUp").permitAll()
				.antMatchers(HttpMethod.POST, "/resetPassword").permitAll()
				.anyRequest().authenticated()
				.and()
			.formLogin()
				.loginPage("/login")
				.defaultSuccessUrl("/", true)
				.permitAll()
				.and()
			.csrf().disable() //TODO: remove this eventually
			.headers().frameOptions().disable().and()
			.logout()
				.logoutSuccessHandler(diceGameLogoutSuccessHandler);
	}

}

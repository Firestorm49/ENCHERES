package fr.eni.encheres.configuration;

import javax.sql.DataSource;

import fr.eni.encheres.Tools.CategorieConverter;
import fr.eni.encheres.Tools.DateTimeConverter;
import fr.eni.encheres.bll.CategorieService;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
@EnableWebSecurity
public class WebConfiguration implements WebMvcConfigurer {

	private final CategorieService categorieService;

	public WebConfiguration(CategorieService categorieService) {
		this.categorieService = categorieService;
	}

	@Bean
	UserDetailsManager userDetailsManager(DataSource datasource) {
		var jdbcUserDetailsManager = new JdbcUserDetailsManager(datasource);
		jdbcUserDetailsManager.setUsersByUsernameQuery("SELECT email, mot_de_passe, 1 FROM UTILISATEURS WHERE email=?");
		jdbcUserDetailsManager.setAuthoritiesByUsernameQuery(
				"select Email, role from ROLES inner join UTILISATEURS ON ROLES.IS_ADMIN = UTILISATEURS.administrateur WHERE Email=?");
		return jdbcUserDetailsManager;
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> {
			auth.requestMatchers(HttpMethod.GET,"/").permitAll();
			auth.requestMatchers(HttpMethod.GET,"/bid").permitAll();
			auth.requestMatchers(HttpMethod.GET,"/bid/detail").permitAll();
			auth.requestMatchers(HttpMethod.GET,"/bid/Encherisseurs").permitAll();
			auth.requestMatchers(HttpMethod.GET,"/bid/Purpose").permitAll();
			auth.requestMatchers(HttpMethod.GET,"/sale/create").permitAll();
			auth.requestMatchers(HttpMethod.GET,"/sale/upload").permitAll();
			auth.requestMatchers(HttpMethod.GET,"/sale/modify").permitAll();
			auth.requestMatchers(HttpMethod.GET,"/sale/cancel").permitAll();
			auth.requestMatchers(HttpMethod.GET,"/users").permitAll();
			auth.requestMatchers(HttpMethod.GET,"/users/create").permitAll();
			auth.requestMatchers(HttpMethod.POST,"/users/create").permitAll();
			auth.requestMatchers(HttpMethod.GET,"/users/modify").permitAll();
			auth.requestMatchers(HttpMethod.POST,"/users/modify").permitAll();
			auth.requestMatchers(HttpMethod.GET,"/users/modify/password").permitAll();
			auth.requestMatchers(HttpMethod.POST,"/users/modify/password").permitAll();
			auth.requestMatchers(HttpMethod.GET,"/users/delete").permitAll();
			auth.requestMatchers(HttpMethod.POST,"/users/buycredit").permitAll();
			auth.requestMatchers(HttpMethod.GET,"/users/deleteMultiUsers").hasAnyRole("ADMIN","SUPER_ADMIN");
			auth.requestMatchers(HttpMethod.GET,"/users/administrateur").hasRole("SUPER_ADMIN");
			auth.requestMatchers(HttpMethod.GET,"/users/deactivation").hasAnyRole("ADMIN","SUPER_ADMIN");
			auth.requestMatchers(HttpMethod.GET,"/users/activation").hasAnyRole("ADMIN","SUPER_ADMIN");
			auth.requestMatchers(HttpMethod.GET,"/category").hasAnyRole("ADMIN","SUPER_ADMIN");
			auth.requestMatchers(HttpMethod.GET,"/category/detail").hasAnyRole("ADMIN","SUPER_ADMIN");
			auth.requestMatchers(HttpMethod.GET,"/category/create").hasAnyRole("ADMIN","SUPER_ADMIN");
			auth.requestMatchers(HttpMethod.GET,"/category/modify").hasAnyRole("ADMIN","SUPER_ADMIN");
			auth.requestMatchers(HttpMethod.GET,"/category/delete").hasAnyRole("ADMIN","SUPER_ADMIN");
			auth.requestMatchers("/css/*").permitAll();
			auth.requestMatchers("/images/*").permitAll();
			auth.requestMatchers("/errors/**").permitAll();
			auth.anyRequest().authenticated();
		}).csrf(AbstractHttpConfigurer::disable);

		http.sessionManagement((session) -> session
				.invalidSessionUrl("/login?timeout")
				.maximumSessions(1)
				.maxSessionsPreventsLogin(false)
				.expiredUrl("/login?timeout"));

		http.formLogin(form -> form
				.loginPage("/login").failureUrl("/login?error=true").permitAll().defaultSuccessUrl("/bid"));

		http.rememberMe((remember) -> remember
				.key("remember-me") // Clé secrète pour signer le cookie
				.rememberMeParameter("rememberMe") // Nom du champ de case à cocher
				.tokenValiditySeconds(86400));
		http.logout(logout -> logout.logoutSuccessUrl("/").deleteCookies("JSESSIONID"));
		return http.build();
	}

	@Bean
	LocaleResolver localeResolver() {
		SessionLocaleResolver slr = new SessionLocaleResolver();
		slr.setDefaultLocale(new Locale("fr"));
		return slr;
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName("language");
		localeChangeInterceptor.setIgnoreInvalidLocale(true);
		return localeChangeInterceptor;
	}


	@Bean
	MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("messages");
		messageSource.setFallbackToSystemLocale(false);
		return messageSource;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor())
				.addPathPatterns("/**")
				.excludePathPatterns("/css/**", "/images/**");
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(getDateTimeConverter());
		registry.addConverter(getCategorieConverter());
	}

	@Bean
	CategorieConverter getCategorieConverter() {
		return new CategorieConverter(categorieService);
	}

	@Bean
	DateTimeConverter getDateTimeConverter() {
		return new DateTimeConverter();
	}

}
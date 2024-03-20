package fr.eni.encheres.configuration;

import javax.sql.DataSource;

import fr.eni.encheres.Tools.CategorieConverter;
import fr.eni.encheres.Tools.DateTimeConverter;
import fr.eni.encheres.bll.CategorieService;
import fr.eni.encheres.bll.EnchereService;
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
    UserDetailsManager userDetailsManager (DataSource datasource) {
        var jdbcUserDetailsManager = new JdbcUserDetailsManager(datasource);
        jdbcUserDetailsManager.setUsersByUsernameQuery("SELECT email, mot_de_passe, 1 FROM UTILISATEURS WHERE email=?");
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery("select Email, role from ROLES inner join UTILISATEURS ON ROLES.IS_ADMIN = UTILISATEURS.administrateur WHERE Email=?");
        return jdbcUserDetailsManager;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers(HttpMethod.GET,"/").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/bid").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/bid/detail").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/bid/Purpose").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/sale/create").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/sale/upload").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/sale/modify").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/sale/cancel").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/users").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/users/create").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/users/modify").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/users/delete").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/users/deactivation").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/users/activation").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/category").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/category/detail").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/category/create").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/category/modify").hasRole("ADMIN");
            auth.requestMatchers(HttpMethod.GET,"/category/delete").permitAll();
            auth.requestMatchers("/css/*").permitAll();
            auth.requestMatchers("/img/*").permitAll();
            auth.requestMatchers("/errors/**").permitAll();
            auth.anyRequest().authenticated();
        }).csrf(AbstractHttpConfigurer::disable);


        http.formLogin(form -> form
                .loginPage("/login").failureUrl("/login?error=true").permitAll().defaultSuccessUrl("/")
        );

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
    LocaleChangeInterceptor localeChangeInterceptor() {
       LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
       localeChangeInterceptor.setParamName("language");
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
       registry.addInterceptor(localeChangeInterceptor());
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(getDateTimeConverter());
        registry.addConverter(getActeurConverter());
    }

    @Bean
    CategorieConverter getActeurConverter() {
        return new CategorieConverter(categorieService);
    }

    @Bean
    DateTimeConverter getDateTimeConverter() {
        return new DateTimeConverter();
    }
}
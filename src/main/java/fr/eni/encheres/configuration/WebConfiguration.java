package fr.eni.encheres.configuration;

import javax.sql.DataSource;

import fr.eni.encheres.bll.EnchereService;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
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

    private final EnchereService enchereService;

    public WebConfiguration(EnchereService unEnchereService) {
        this.enchereService = unEnchereService;
    }

    @Bean
    UserDetailsManager userDetailsManager (DataSource datasource) {
        var jdbcUserDetailsManager = new JdbcUserDetailsManager(datasource);
        jdbcUserDetailsManager.setUsersByUsernameQuery("SELECT email, mot_de_passe, 1 FROM UTILISATEURS WHERE email=?");
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery("SELECT email, administrateur from UTILISATEURS WHERE email=?");
        return jdbcUserDetailsManager;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers(HttpMethod.GET,"/").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/Encheres").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/Encheres/detail").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/Encheres/Propose").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/Vente/Create").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/Vente/Modify").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/Vente/Annule").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/Users").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/Users/Create").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/Users/Modify").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/Users/Delete").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/Users/Desactivation").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/Users/Activation").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/Category").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/Category/detail").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/Category/Create").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/Category/Modify").permitAll();
            auth.requestMatchers(HttpMethod.GET,"/Category/Delete").permitAll();
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
}
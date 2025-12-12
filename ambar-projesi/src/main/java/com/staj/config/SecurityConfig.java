package com.staj.config;

import com.staj.security.JwtFilter;
import com.staj.security.MyUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration // Bu sınıf Spring Security konfigürasyon sınıfı olarak tanımlanır
@EnableMethodSecurity //  @PreAuthorize kullanabilmek için aktif edilir
public class SecurityConfig {

    private final JwtFilter jwtFilter; //  Request'in başındaki JWT token'ı doğrulayan filtre
    private final MyUserDetailsService userDetailsService; // Kullanıcı bilgilerini veritabanından çeken servis

    public SecurityConfig(JwtFilter jwtFilter, MyUserDetailsService userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            // React frontend çalıştığı için CSRF kapatılır (aksi sayfa POST atamaz)

            .cors(cors -> cors.configurationSource(request -> {
                var corsConfig = new CorsConfiguration();
                corsConfig.setAllowedOrigins(List.of("http://localhost:3000")); 
                //  Sadece frontend'den gelen istekler kabul edilir
                corsConfig.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS")); 
                // Backend'in izin verdiği HTTP metodları
                corsConfig.setAllowedHeaders(List.of("*")); 
                // Tüm header'lara izin veriyoruz (Authorization dahil)
                corsConfig.setAllowCredentials(true);
                return corsConfig;
            }))

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                //  Login ve register endpoint'lerine herkes erişebilir

                .requestMatchers("/api/auth/forgot-password").permitAll()
                // Şifremi unuttum işlemi token istemez

                .requestMatchers("/api/products/**").hasAnyRole("USER", "ADMIN")
                // Ürün işlemleri hem admin hem user için serbest

                .requestMatchers("/api/transactions/**").hasAnyRole("USER", "ADMIN")
                // Stok hareketlerini herkes görebilir

                .requestMatchers("/api/users/**").hasRole("ADMIN")
                // User yönetimi sadece admin'e açık

                .anyRequest().authenticated()
                // Diğer tüm istekler token gerektirir
            )

            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Session kullanılmaz, tamamen JWT token üzerinden doğrulama yapılır

            .authenticationProvider(authenticationProvider())
            //Kullanıcı doğrulama sağlayıcısını ekliyoruz

            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
            // Her request'ten önce JWT filtresi çalışsın

        return http.build(); // Güvenlik zincirini oluştur ve sistemi ayarla
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); 
        // Kullanıcı bilgilerini veritabanından al
        authProvider.setPasswordEncoder(passwordEncoder()); 
        //  Şifreleri BCrypt ile karşılaştır
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); 
        // Şifreler BCrypt ile hashlenir
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager(); 
        // Spring Security'nin kullandığı authentication yöneticisi
    }
}

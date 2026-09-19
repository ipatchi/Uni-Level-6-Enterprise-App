package staffs.leaverequestapp.identity.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enables @PreAuthorize in facade
@AllArgsConstructor
public class SecurityConfig { // define HTTP security policies
    private final Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
                .csrf(AbstractHttpConfigurer::disable) // JWTs are stateless
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll() // end points not requiring authentication
                        .anyRequest().authenticated() // non specified end points require authentication
                )
                .oauth2ResourceServer(oauth2 -> oauth2 // intercept request
                        // apply custom convertor
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                )
                .build();
    }
}

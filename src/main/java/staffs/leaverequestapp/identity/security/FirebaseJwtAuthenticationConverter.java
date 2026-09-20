package staffs.leaverequestapp.identity.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class FirebaseJwtAuthenticationConverter
        implements Converter<Jwt, AbstractAuthenticationToken> {
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String roleClaim = jwt.getClaimAsString("role");
        if (roleClaim == null && Boolean.TRUE.equals(jwt.getClaimAsBoolean("admin"))) {
            roleClaim = "ADMIN";
        }
        roleClaim = Objects.requireNonNull(roleClaim, "JWT role claim is required")
                .trim()
                .toUpperCase();
        if (roleClaim.startsWith("ROLE_")) {
            roleClaim = roleClaim.substring("ROLE_".length());
        }

        Collection<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + roleClaim));

        return new JwtAuthenticationToken(jwt,
                authorities,
                Objects.requireNonNull(jwt.getSubject()));
    }
}
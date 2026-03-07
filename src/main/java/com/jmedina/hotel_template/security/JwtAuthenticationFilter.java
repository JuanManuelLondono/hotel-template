package com.jmedina.hotel_template.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    // OncePerRequestFilter garantiza que el filtro se ejecuta UNA sola vez por petición

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Buscar el token en el header "Authorization"
        final String authHeader = request.getHeader("Authorization");

        // Si no hay header o no empieza con "Bearer ", dejar pasar sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extraer el token (quitar el prefijo "Bearer ")
        final String jwt = authHeader.substring(7);

        // 3. Extraer el email del token
        final String userEmail = jwtService.extractUsername(jwt);

        // 4. Si hay email y el usuario no está autenticado aún
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 5. Cargar el usuario desde la BD
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            // 6. Validar el token
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 7. Crear el objeto de autenticación y guardarlo en el contexto
                // Esto le dice a Spring Security: "este usuario está autenticado"
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null, // credentials null porque ya validamos con JWT
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 8. Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}

package com.dss.practica1.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.dss.practica1.model.LoginResponse;

@RestController
@RequestMapping("/api/auth")
public class LoginRestController {

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private Map<String, String> tokenStore;

    /**
     * Endpoint para realizar login.
     * Recibe credenciales y autentica al usuario.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpServletRequest request) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");

            // Crear token de autenticación
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

            // Autenticar al usuario
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            Authentication authentication = authenticationManager.authenticate(authToken);

            // Establecer el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            String token = "mock-token-" + username;
            
            tokenStore.put(token, username);
            // Respuesta exitosa
            return ResponseEntity.ok(new LoginResponse(token));

        } catch (Exception e) {
            // Manejar errores de autenticación
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
    
    /**
     * Endpoint para cerrar la sesión.
     * Invalida la sesión del usuario actual y limpia el contexto de seguridad.
     * 
     * @param request  HttpServletRequest para manejar la sesión actual.
     * @param response HttpServletResponse para manejar las cookies.
     * @return ResponseEntity con el mensaje de logout exitoso.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Invalidar la sesión actual
            request.getSession().invalidate();

            // Limpiar el contexto de seguridad
            SecurityContextHolder.clearContext();

            // Eliminar la cookie de sesión
            response.setHeader("Set-Cookie", "JSESSIONID=; HttpOnly; Path=/; Max-Age=0");

            return ResponseEntity.ok("Logout successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during logout");
        }
    }
}
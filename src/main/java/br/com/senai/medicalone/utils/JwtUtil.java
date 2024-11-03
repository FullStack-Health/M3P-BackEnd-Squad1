package br.com.senai.medicalone.utils;

import br.com.senai.medicalone.entities.user.PreRegisterUser;
import br.com.senai.medicalone.entities.user.User;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.private.key}")
    private String privateKeyPath;

    @Value("${jwt.public.key}")
    private String publicKeyPath;

    @Value("${jwt.expiration}")
    private Long expiration;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    public void init() throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        try (InputStream privateKeyStream = getClass().getClassLoader().getResourceAsStream(privateKeyPath.replace("classpath:", ""))) {
            if (privateKeyStream == null) {
                throw new IllegalArgumentException("Private key file not found: " + privateKeyPath);
            }
            byte[] privateKeyBytes = privateKeyStream.readAllBytes();
            String privateKeyPEM = new String(privateKeyBytes)
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] decodedPrivateKey = Base64.getDecoder().decode(privateKeyPEM);
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(decodedPrivateKey);
            privateKey = keyFactory.generatePrivate(privateKeySpec);
        }

        try (InputStream publicKeyStream = getClass().getClassLoader().getResourceAsStream(publicKeyPath.replace("classpath:", ""))) {
            if (publicKeyStream == null) {
                throw new IllegalArgumentException("Public key file not found: " + publicKeyPath);
            }
            byte[] publicKeyBytes = publicKeyStream.readAllBytes();
            String publicKeyPEM = new String(publicKeyBytes)
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] decodedPublicKey = Base64.getDecoder().decode(publicKeyPEM);
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(decodedPublicKey);
            publicKey = keyFactory.generatePublic(publicKeySpec);
        }
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().getAuthority());
        claims.put("patientId", user.getPatientId());
        claims.put("name", user.getName());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }

    public String generateToken(PreRegisterUser preRegisterUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", preRegisterUser.getRole().getAuthority());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(preRegisterUser.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }

    public Long getPatientIdFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("patientId", Long.class));
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String email = getEmailFromToken(token);
        final String role = getRoleFromToken(token);
        return (email.equals(userDetails.getUsername()) && role.equals(userDetails.getAuthorities().iterator().next().getAuthority()) && !isTokenExpired(token));
    }

    public String getRoleFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("role", String.class));
    }

    public String getEmailFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }
}
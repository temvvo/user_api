package com.user.api_challenge.security.utils;



import io.jsonwebtoken.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {
    private final Log logger = LogFactory.getLog(this.getClass());
    private static String CLAIM_ROLES = "roles";
    private static String CLAIM_PERMISSIONS = "permissions";
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration:3600}")
    private Long expiration;

    public JwtTokenUtil() {
    }

    public List<String> getRolesFromToken(String token) {
        Claims claims = this.getAllClaimsFromToken(token);
        return (List)claims.get(CLAIM_ROLES, List.class);
    }

    public List<String> getPermissionsFromToken(String token) {
        Claims claims = this.getAllClaimsFromToken(token);
        return (List)claims.get(CLAIM_PERMISSIONS, List.class);
    }

    public String getUsernameFromToken(String token) {
        return (String)this.getClaimFromToken(token, Claims::getSubject);
    }

    public String getIssuer(String token) {
        return (String)this.getClaimFromToken(token, Claims::getIssuer);
    }

    public long getUserIdFromToken(String token) {
        return Long.getLong((String)this.getClaimFromToken(token, Claims::getSubject));
    }

    public Date getIssuedAtDateFromToken(String token) {
        return (Date)this.getClaimFromToken(token, Claims::getIssuedAt);
    }

    public Date getExpirationDateFromToken(String token) {
        return (Date)this.getClaimFromToken(token, Claims::getExpiration);
    }

    public String getAudienceFromToken(String token) {
        return (String)this.getClaimFromToken(token, Claims::getAudience);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = this.getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) throws JwtException {
        return (Claims)Jwts.parser().setSigningKey(this.getSecretBytes()).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        Date expiration = this.getExpirationDateFromToken(token);
        if (expiration == null) {
            List<String> roles = this.getRolesFromToken(token);
            if (roles != null && roles.contains("SYSTEM")) {
                this.logger.debug("System token has no expire date!");
                return false;
            } else {
                this.logger.warn("JWT token has no expire date! It is not valid!");
                return true;
            }
        } else {
            return expiration.before(new Date());
        }
    }

    public Boolean isValid(String token) {
        try {
            this.getAllClaimsFromToken(token);
            return true;
        } catch (MalformedJwtException var3) {
            this.logger.warn("MalformedJwtException:" + var3.getMessage());
        } catch (SignatureException var4) {
            this.logger.warn("JWT SignatureException:" + var4.getMessage());
        } catch (ExpiredJwtException var5) {
            this.logger.warn("JWT ExpiredJwtException:" + var5.getMessage());
        } catch (JwtException var6) {
            this.logger.warn("JWT exception", var6);
        }

        return false;
    }

    public Boolean isSignedAndValid(String token) {
        boolean isValid = false;
        boolean tokenExpired = true;

        try {
            this.getAllClaimsFromToken(token);
            tokenExpired = this.isTokenExpired(token);
            if (tokenExpired) {
                this.logger.warn("Token expired!");
            }

            isValid = Jwts.parser().isSigned(token) && !tokenExpired;
        } catch (MalformedJwtException var5) {
            this.logger.warn("MalformedJwtException: " + var5.getMessage());
        } catch (SignatureException var6) {
            this.logger.warn("JWT SignatureException: " + var6.getMessage());
        } catch (ExpiredJwtException var7) {
            this.logger.warn("JWT ExpiredJwtException: " + var7.getMessage());
        } catch (JwtException var8) {
            this.logger.warn("JWT exception", var8);
        }

        if (tokenExpired) {
            throw new ExpiredJwtException((Header)null, (Claims)null, "token expired");
        } else {
            return isValid;
        }
    }

    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return lastPasswordReset != null && created.before(lastPasswordReset);
    }

    public String generateToken(String username, List<String> roles, Class issuerClass) {
        Map<String, Object> claims = new HashMap();
        claims.put(CLAIM_ROLES, roles);
        claims.put("iss", issuerClass.getCanonicalName());
        return this.doGenerateToken(claims, username, this.expiration);
    }

    public String generateSystemToken(List<String> permissions, Class issuerClass) {
        Map<String, Object> claims = new HashMap();
        claims.put(CLAIM_ROLES, Arrays.asList("SYSTEM"));
        claims.put(CLAIM_PERMISSIONS, permissions);
        claims.put("iss", issuerClass.getCanonicalName());
        return this.doGenerateToken(claims, "-1", 120L);
    }

    private String doGenerateToken(Map<String, Object> claims, String subject, long expirationSeconds) {
        Date createdDate = new Date();
        Date expirationDate = this.calculateExpirationDate(createdDate, expirationSeconds);
        this.logger.debug("Created new token expires: " + expirationDate);
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(createdDate).setExpiration(expirationDate).signWith(SignatureAlgorithm.HS512, this.getSecretBytes()).compact();
    }

    public String refreshToken(String token) {
        Date createdDate = new Date();
        Date expirationDate = this.calculateExpirationDate(createdDate, this.expiration);
        Claims claims = this.getAllClaimsFromToken(token);
        claims.setIssuedAt(createdDate);
        claims.setExpiration(expirationDate);
        return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, this.getSecretBytes()).compact();
    }

    private Date calculateExpirationDate(Date createdDate, Long expiration) {
        return new Date(createdDate.getTime() + expiration * 1000L);
    }

    public byte[] getSecretBytes() {
        return this.secret.getBytes(Charset.forName("UTF-8"));
    }
}

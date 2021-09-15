package ir.ac.ut.ece.ie.loghme;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

public class Jwt {
    private static Jwt curInstance;
    SignatureAlgorithm signatureAlgorithm;
    String secretKey;

    public static Jwt getCurInstance() {
        if(curInstance == null){
            curInstance = new Jwt();
        }
        return curInstance;
    }

    private Jwt(){
        this.signatureAlgorithm = SignatureAlgorithm.HS256;
        this.secretKey = "loghme";

    }
    public String create(String userId, String issuer, String subject, int hours) {
        byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(secretKey);
        Key signingKey = new SecretKeySpec(secretKeyBytes, signatureAlgorithm.getJcaName());

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        long ttlMillis = hours * 3600000;
        long expMillis = nowMillis + ttlMillis;
        Date exp = new Date(expMillis);

        JwtBuilder builder = Jwts.builder().setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(signatureAlgorithm, signingKey)
                .setExpiration(exp)
                .setId(userId);

        return builder.compact();

    }

    public Claims parseJwt(String jwt){
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
                .parseClaimsJws(jwt).getBody();
        return claims;
    }


}

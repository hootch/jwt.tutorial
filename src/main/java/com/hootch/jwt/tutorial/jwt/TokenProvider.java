package com.hootch.jwt.tutorial.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TokenProvider implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
    private static final String AUTHORITIES_KEY = "auth";
    private final String secret;

    private final String refreshSecret;
    private final long tokenValidityInMilliseconds;

    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000* 60 * 60 * 24 * 7;//7일
    private Key key;
    private Key refreshKey;
    private String str;
    
    
    public TokenProvider(  
        @Value("${jwt.secret}") String secret,  //yml 파일의 jwt 설정란에 있는 secret 정보를 가져와서 저장함
        @Value("${jwt.refreshSecret}") String refreshSecret,
        @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds) {   //y-: 1. yml 파일의 token-validty-in-second 정보를 가져와서 저장함
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
        this.refreshSecret = refreshSecret;
    }

    @Override                       //얘를 오버라이드 한 이유는 Bean 생성 후 의존성 주입을 받은 후 secret값을 Base64 Decode해서 key변수에 할당하기 위함
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        keyBytes = Decoders.BASE64.decode(refreshSecret);
        this.refreshKey = Keys.hmacShaKeyFor(keyBytes);

    }


    public String createToken(Authentication authentication,String type) {  //Authentication객체의 권한정보를 이용해서 토큰을 생성하는 메서드
        String authorities = authentication.getAuthorities().stream()
                                            .map(GrantedAuthority::getAuthority)
                                            .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);   //y-: 현시간(now)에 위에서 설정한지금 시간에 위에서 설정한 토큰 생존시간을 더해서 시간을 지정한 후
        Key thisKey = type.equals("access") ? key : refreshKey;


        return Jwts.builder()
                .setSubject(authentication.getName())
                    .claim(AUTHORITIES_KEY, authorities)
                    .signWith(thisKey, SignatureAlgorithm.HS512)
                    .setExpiration(validity)                                //setExpiration을 통해 토큰의 만료시간을 정함.
                    .compact();
    }



    public Authentication getAuthentication(String token) {                                 // 토큰을 파라미터로 받아서
        Claims claims = Jwts                                
                            .parserBuilder()  
                            .setSigningKey(key) 
                            .build()
                            .parseClaimsJws(token)                                          // 토큰으로 클레임으로 만들어주고, 그걸 
                            .getBody();

        Collection<? extends GrantedAuthority> authorities =                                //클레임에서 권한 정보들를 빼내서 
            Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))   
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);            //권한 정보들을 이용해서 유저 객체를 만들어주고

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);        //위의 유저객체와 토큰, 그리고 권한정보를 이용해서 Authentication객체를 리턴해줌
    }
    

    public boolean validateToken(String token) {                                            //토큰을 파라미터로 받아서
        Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);//파싱을 한 후
        System.out.println(claimsJws);
        System.out.println(claimsJws);
        System.out.println(claimsJws);
        System.out.println(claimsJws);
        System.out.println(claimsJws);

        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);//파싱을 한 후
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {    //캐치해서 문제가 있으면 false 아니면 true ^-^
            logger.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            logger.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            logger.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            logger.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}

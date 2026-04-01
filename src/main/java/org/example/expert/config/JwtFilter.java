package org.example.expert.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter implements Filter {

    private final JwtUtil jwtUtil;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String url = httpRequest.getRequestURI();

        // 로그인할때 인증은 필요없다. 통과 시켜라
        if (url.startsWith("/auth")) {
            chain.doFilter(request, response);
            return;
        }

        String bearerJwt = httpRequest.getHeader("Authorization");


        // 토큰이 없을때.
        // 실제 접근 제어는 SecurityConfig의 인증 규칙이 처리
        if (bearerJwt == null) {
            // 토큰이 없는 경우 400을 반환합니다.
//            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "JWT 토큰이 필요합니다.");

            // 어차피 최종판단은 SecurityConfig가 처리한다.
            chain.doFilter(request, response);
            return;
        }

        // 토큰이 유효한지 봐라. 토큰 검증 로직
        String jwt = jwtUtil.substringToken(bearerJwt);

        try {
            // JWT 유효성 검사와 claims 추출
            Claims claims = jwtUtil.extractClaims(jwt);
            if (claims == null) {
                // 잘못된 토큰이 들어 왔을 경우.
                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 JWT 토큰입니다.");
                return;
            }

            AuthUser authUser = jwtUtil.getAuthUser(claims);


            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            authUser,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + authUser.getUserRole().name()))
                    );


            //
//            SecurityContextHolder.getContext().setAuthentication(new usernamePassword);

            // JWT 토큰에서 복호화 한 데이터 저장하기.
//            UserRole userRole = UserRole.valueOf(claims.get("userRole", String.class));
//
//            httpRequest.setAttribute("userId", Long.parseLong(claims.getSubject()));
//            httpRequest.setAttribute("email", claims.get("email"));
//            httpRequest.setAttribute("userRole", claims.get("userRole"));
//            httpRequest.setAttribute("nickname", claims.get("nickname"));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);


//            이제 필요없다. 왜냐면 spring security가 처리한다.
//            if (url.startsWith("/admin")) {
//                // 관리자 권한이 없는 경우 403을 반환합니다.
//                if (!UserRole.ADMIN.equals(userRole)) {
//                    httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "관리자 권한이 없습니다.");
//                    return;
//                }
//                chain.doFilter(request, response);
//                return;
//            }
//
//            chain.doFilter(request, response);


        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.", e);
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않는 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.", e);
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", e);
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "지원되지 않는 JWT 토큰입니다.");
        } catch (Exception e) {
            log.error("Internal server error", e);
            httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}

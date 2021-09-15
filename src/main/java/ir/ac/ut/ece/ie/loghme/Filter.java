package ir.ac.ut.ece.ie.loghme;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.ConnectionPool;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@Component
public class Filter extends GenericFilterBean {
    private ArrayList<String> filteredUrl = new ArrayList<String>(){
        {
            add("home");
            add("partyCart");
            add("foodParty");
            add("restaurant");
            add("restaurants");
            add("search");
            add("searchFood");
            add("searchRestaurant");
            add("credit");
            add("user");
            add("cart");
            add("order");
        }
    };
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        System.out.println("in filter");

        try {
            Connection con = ConnectionPool.getConnection();
            con.close();
        } catch (SQLException e) {
            System.out.println("DB problem");
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.getWriter().println("پایگاه داده موقتا غیرفعال است");
            return;
        }

        List<String> url = Arrays.asList(httpServletRequest.getServletPath().split("/"));
        String authHeader = httpServletRequest.getHeader("authorization");

        if(url.size() >= 2){
            if(this.filteredUrl.contains(url.get(1))){
                if(authHeader == null){
                    httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    httpServletResponse.getWriter().println("Authorizatoin header has wrong format");
                    return;
                }
                else if(!authHeader.startsWith("Bearer ")){
                    httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    httpServletResponse.getWriter().println("Authorizatoin header has wrong format");
                    return;
                }
                String jwtToken = authHeader.substring(7);
                if(!jwtToken.equals("null")){
                    try{
                        Claims claims = Jwt.getCurInstance().parseJwt(jwtToken);
                        httpServletRequest.setAttribute("claims", claims);
                        filterChain.doFilter(httpServletRequest, httpServletResponse);
                    }catch(ExpiredJwtException e){
                        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        httpServletResponse.setCharacterEncoding("UTF-8");
                        httpServletResponse.getWriter().println("از آخرین ورود شما مدت زیادی گذشته است. دوباره وارد شوید");
//                        httpServletResponse.getWriter().println("jwt expired");
                    }
                }else{
                    httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    httpServletResponse.setCharacterEncoding("UTF-8");
                    httpServletResponse.getWriter().println("برای دسترسی به این قسمت لازم است ابتدا وارد شوید");
//                    httpServletResponse.getWriter().println("login first");
                }
            }else{
                httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }else{
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}

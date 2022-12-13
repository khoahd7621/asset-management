package com.nashtech.assignment.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nashtech.assignment.dto.response.ExceptionResponse;
import com.nashtech.assignment.exceptions.ForbiddenException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
            if (ex instanceof ExpiredJwtException) {
                httpStatus = HttpStatus.UNAUTHORIZED;
            } else if (ex instanceof ForbiddenException) {
                httpStatus = HttpStatus.FORBIDDEN;
            }
            setErrorResponse(httpStatus, response, ex);
        }
    }

    public void setErrorResponse(HttpStatus status, HttpServletResponse response, Throwable ex) {
        response.setStatus(status.value());
        response.setContentType("application/json");
        ExceptionResponse exceptionResponse = ExceptionResponse.builder().message(ex.getMessage()).build();
        try {
            String json = convertObjectToJson(exceptionResponse);
            response.getWriter().write(json);
        } catch (IOException e) {
            log.info(e.getMessage());
        }
    }

    public String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}

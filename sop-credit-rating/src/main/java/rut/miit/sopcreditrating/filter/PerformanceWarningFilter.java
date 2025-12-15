package rut.miit.sopcreditrating.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(2)
public class PerformanceWarningFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(PerformanceWarningFilter.class);
    private static final long THRESHOLD_MS = 20L;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            if (duration > THRESHOLD_MS && request.getRequestURI().startsWith("/api/")) {
                log.warn("Slow request detected: {} {} took {}ms",
                        request.getMethod(), request.getRequestURI(), duration);
            }
        }
    }
}

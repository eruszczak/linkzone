package io.eryk.linkzone.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// TODO: this is an ugly hack
// https://dev.to/composite/how-to-run-spa-webapp-with-spring-boot-2-x-5gdo
@Controller
public class SpaErrorController implements ErrorController {

    @RequestMapping("/error")
    public Object error(HttpServletRequest request, HttpServletResponse response) {
        if (request.getMethod().equalsIgnoreCase(HttpMethod.GET.name())) {
            return "forward:/index.html"; // forward to static SPA html resource.
        }
        return ResponseEntity.notFound().build();
    }
}
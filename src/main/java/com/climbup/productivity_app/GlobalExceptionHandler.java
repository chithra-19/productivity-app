package com.climbup.productivity_app;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	
	 private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    	@ExceptionHandler(Exception.class)
    	public String handleException(Exception ex, Model model) {
    	    model.addAttribute("message", ex.getMessage());
    	    return "error"; // Thymeleaf template: error.html
    	}


    }



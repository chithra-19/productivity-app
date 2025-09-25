package com.climbup.controller.productivity;

import com.climbup.service.productivity.MotivationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/motivation")
public class MotivationController {

    private final MotivationService motivationService;

    @Autowired
    public MotivationController(MotivationService motivationService) {
        this.motivationService = motivationService;
    }

    /**
     * Get a random motivational quote.
     */
    @GetMapping("/quote")
    public ResponseEntity<String> getRandomQuote() {
        String quote = motivationService.getRandomQuote();
        return ResponseEntity.ok(quote);
    }
}

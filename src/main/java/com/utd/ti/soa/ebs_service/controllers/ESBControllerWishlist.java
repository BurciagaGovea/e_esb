package com.utd.ti.soa.ebs_service.controllers;

import com.utd.ti.soa.ebs_service.model.WishlistItem;
import com.utd.ti.soa.ebs_service.utils.Auth;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/esb/wishlist")
public class ESBControllerWishlist {

    private final WebClient webClient = WebClient.create("http://orders_service:3000/wishlist");
    private final Auth auth = new Auth();

    @GetMapping("/{user_id}")
    public Mono<ResponseEntity<String>> getWishlist(@PathVariable String user_id,
                                                    @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido"));
        }

        return webClient.get()
                .uri("/" + user_id)
                .retrieve()
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(WebClientResponseException.class,
                        e -> Mono.just(ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString())))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor")));
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<String>> addToWishlist(@RequestBody WishlistItem item,
                                                      @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido"));
        }

        return webClient.post()
                .uri("/add")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(item)
                .retrieve()
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(WebClientResponseException.class,
                        e -> Mono.just(ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString())))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor")));
    }

    @PutMapping("/remove")
    public Mono<ResponseEntity<String>> removeFromWishlist(@RequestBody WishlistItem item,
                                                           @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido"));
        }

        return webClient.put()
                .uri("/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(item)
                .retrieve()
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(WebClientResponseException.class,
                        e -> Mono.just(ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString())))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor")));
    }
}
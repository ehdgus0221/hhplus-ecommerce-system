package kr.hhplus.be.server.product.api.dto.controller;

import kr.hhplus.be.server.product.api.dto.response.ProductDetailResponseDto;
import kr.hhplus.be.server.product.api.dto.response.ProductResponseDto;
import kr.hhplus.be.server.product.application.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        List<ProductResponseDto> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponseDto> getProductDetail(@PathVariable Long productId) {
        ProductDetailResponseDto response = productService.getProductDetail(productId);
        return ResponseEntity.ok(response);
    }
}

package edu.psu.se411.lab13;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.math.BigDecimal;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import edu.psu.se411.lab13.model.Product;
import edu.psu.se411.lab13.repository.ProductRepository;

@SpringBootTest
@AutoConfigureMockMvc
class ProductApiTest {
    @Autowired MockMvc mvc;
    @Autowired ProductRepository products;
    @Autowired Validator validator;

    @Test
    void returnsAllSeventeenSeedProducts() throws Exception {
        mvc.perform(get("/api/v1/products")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(17));
    }

    @Test
    void findsProductWithExactPrice() throws Exception {
        mvc.perform(get("/api/v1/products/10")).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("3d printer"))
                .andExpect(jsonPath("$.price").value(1840.25));
        assertEquals(new BigDecimal("1840.25"), products.findById(10).orElseThrow().price());
    }

    @Test
    void missingProductReturns404() throws Exception {
        mvc.perform(get("/api/v1/products/99999")).andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "0", "abc", "2147483648"})
    void invalidIdsReturn400(String id) throws Exception {
        mvc.perform(get("/api/v1/products/" + id)).andExpect(status().isBadRequest());
    }

    @Test
    void recordValidationRejectsInvalidProductData() {
        Product invalid = new Product(-1, " ", "x".repeat(101), new BigDecimal("-0.001"));
        assertFalse(validator.validate(invalid).isEmpty());
        Product valid = new Product(1, "Keyboard", null, new BigDecimal("0.30"));
        assertTrue(validator.validate(valid).isEmpty());
    }

    @Test
    void decimalArithmeticIsExact() {
        assertEquals(new BigDecimal("0.30"),
                new BigDecimal("0.10").add(new BigDecimal("0.20")));
    }
}

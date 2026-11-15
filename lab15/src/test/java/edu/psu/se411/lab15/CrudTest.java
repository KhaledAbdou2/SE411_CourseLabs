package edu.psu.se411.lab15;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import edu.psu.se411.lab15.repository.ProductRepository;
@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class CrudTest {
 @Autowired MockMvc mvc;
 @Autowired ProductRepository repository;

 @Test void completeLifecycle() throws Exception {
  String response=mvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON).content("""
  {"name":"Keyboard","description":"Test","price":120.25}
  """)).andExpect(status().isCreated()).andReturn().getResponse().getHeader("Location");
  assertNotNull(response);
  mvc.perform(get(response)).andExpect(status().isOk());
  mvc.perform(put(response).contentType(MediaType.APPLICATION_JSON).content("""
  {"name":"Keyboard","description":"Test","price":120.25}
  """)).andExpect(status().isNoContent());
  mvc.perform(delete(response)).andExpect(status().isNoContent());
  mvc.perform(get(response)).andExpect(status().isNotFound());
 }

 @Test void invalidDataDoesNotMutate() throws Exception {
  long count=repository.count();
  mvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest());
  assertEquals(count,repository.count());
 }

 @Test void listAndMissingAndInvalidId() throws Exception {
  mvc.perform(get("/api/v1/products")).andExpect(status().isOk());
  mvc.perform(get("/api/v1/products/999999")).andExpect(status().isNotFound());
  mvc.perform(get("/api/v1/products/-1")).andExpect(status().isBadRequest());
 }
}

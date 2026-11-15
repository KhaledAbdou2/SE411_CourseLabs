package edu.psu.se411.lab16;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import org.springframework.security.test.context.support.WithMockUser;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import edu.psu.se411.lab16.repository.CourseRepository;
@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class CrudTest {
 @Autowired MockMvc mvc;
 @Autowired CourseRepository repository;
 @WithMockUser(username="usera",roles="USER")
 @Test void completeLifecycle() throws Exception {
  String response=mvc.perform(post("/api/v1/courses").with(csrf()).contentType(MediaType.APPLICATION_JSON).content("""
  {"code":"SE411","name":"Software Construction","description":"Test","credits":3}
  """)).andExpect(status().isCreated()).andReturn().getResponse().getHeader("Location");
  assertNotNull(response);
  mvc.perform(get(response)).andExpect(status().isOk());
  mvc.perform(put(response).with(csrf()).contentType(MediaType.APPLICATION_JSON).content("""
  {"code":"SE411","name":"Software Construction","description":"Test","credits":3}
  """)).andExpect(status().isNoContent());
  mvc.perform(delete(response).with(csrf())).andExpect(status().isNoContent());
  mvc.perform(get(response)).andExpect(status().isNotFound());
 }
 @WithMockUser(username="usera",roles="USER")
 @Test void invalidDataDoesNotMutate() throws Exception {
  long count=repository.count();
  mvc.perform(post("/api/v1/courses").with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest());
  assertEquals(count,repository.count());
 }
 @WithMockUser(username="usera",roles="USER")
 @Test void listAndMissingAndInvalidId() throws Exception {
  mvc.perform(get("/api/v1/courses")).andExpect(status().isOk());
  mvc.perform(get("/api/v1/courses/999999")).andExpect(status().isNotFound());
  mvc.perform(get("/api/v1/courses/-1")).andExpect(status().isBadRequest());
 }
}

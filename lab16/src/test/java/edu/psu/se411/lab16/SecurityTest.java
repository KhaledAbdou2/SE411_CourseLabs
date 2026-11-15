package edu.psu.se411.lab16;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class SecurityTest {
 @Autowired MockMvc mvc;
 @Autowired UserDetailsService users;
 @Autowired PasswordEncoder encoder;
 @Test void homeIsPublicAndOtherRoutesRequireLogin() throws Exception {
  mvc.perform(get("/")).andExpect(status().isOk());
  mvc.perform(get("/dashboard")).andExpect(status().is3xxRedirection());
  mvc.perform(get("/api/v1/courses")).andExpect(status().is3xxRedirection());
 }
 @Test void roleBoundaries() throws Exception {
  mvc.perform(get("/dashboard").with(user("usera").roles("USER"))).andExpect(status().isOk());
  mvc.perform(get("/admindashboard").with(user("usera").roles("USER"))).andExpect(status().isForbidden());
  mvc.perform(get("/admindashboard").with(user("admin").roles("ADMIN"))).andExpect(status().isOk());
  mvc.perform(get("/dashboard").with(user("admin").roles("ADMIN"))).andExpect(status().isForbidden());
 }
 @Test void realLoginAcceptsCorrectPasswordAndRejectsWrongPassword() throws Exception {
  mvc.perform(formLogin().user("usera").password("pwd123")).andExpect(authenticated().withUsername("usera"));
  mvc.perform(formLogin().user("admin").password("admin123")).andExpect(authenticated().withUsername("admin"));
  mvc.perform(formLogin().user("usera").password("wrong")).andExpect(unauthenticated());
 }
 @Test void csrfIsRequiredForLoginAndWrites() throws Exception {
  mvc.perform(post("/login").param("username","usera").param("password","pwd123")).andExpect(status().isForbidden());
  mvc.perform(post("/api/v1/courses").with(user("usera").roles("USER"))).andExpect(status().isForbidden());
 }
 @Test void loginPageContainsCsrfAndLogoutEndsSession() throws Exception {
  mvc.perform(get("/login")).andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("_csrf")));
  mvc.perform(post("/logout").with(user("usera")).with(csrf())).andExpect(unauthenticated());
 }
 @Test void passwordsAreBcryptEncoded() {
  String encoded=users.loadUserByUsername("usera").getPassword();
  assertNotEquals("pwd123",encoded); assertTrue(encoder.matches("pwd123",encoded));
 }
}

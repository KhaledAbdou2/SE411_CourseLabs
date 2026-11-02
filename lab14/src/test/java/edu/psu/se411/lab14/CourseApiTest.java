package edu.psu.se411.lab14;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import edu.psu.se411.lab14.repository.CourseRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CourseApiTest {
    @Autowired MockMvc mvc;
    @Autowired CourseRepository courses;

    private String course(long id) {
        return "{\"id\":" + id + ",\"code\":\"SE411\",\"name\":\"Software Construction\","
                + "\"description\":\"Spring CRUD lab\",\"credits\":3}";
    }

    @Test
    void readsSeedCourses() throws Exception {
        mvc.perform(get("/api/v1/courses")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
        mvc.perform(get("/api/v1/courses/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SE201"));
    }

    @Test
    void createsCourseWithLocationAndCanReadItBack() throws Exception {
        mvc.perform(post("/api/v1/courses").contentType(MediaType.APPLICATION_JSON).content(course(100)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/courses/100"))
                .andExpect(jsonPath("$.id").value(100));
        mvc.perform(get("/api/v1/courses/100")).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Software Construction"));
        assertEquals(3, courses.count());
    }

    @Test
    void duplicateIdReturns422AndPreservesOriginal() throws Exception {
        mvc.perform(post("/api/v1/courses").contentType(MediaType.APPLICATION_JSON).content(course(1)))
                .andExpect(status().isUnprocessableEntity());
        assertEquals("SE201", courses.findById(1L).orElseThrow().code());
        assertEquals(2, courses.count());
    }

    @Test
    void updateReturnsEmpty204AndPersistsChanges() throws Exception {
        mvc.perform(put("/api/v1/courses/1").contentType(MediaType.APPLICATION_JSON).content(course(1)))
                .andExpect(status().isNoContent()).andExpect(content().string(""));
        mvc.perform(get("/api/v1/courses/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SE411"));
    }

    @Test
    void deletesAndThenReturns404() throws Exception {
        mvc.perform(delete("/api/v1/courses/1")).andExpect(status().isNoContent())
                .andExpect(content().string(""));
        mvc.perform(get("/api/v1/courses/1")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/v1/courses/1")).andExpect(status().isNotFound());
    }

    @Test
    void missingGetPutDeleteReturn404() throws Exception {
        mvc.perform(get("/api/v1/courses/999")).andExpect(status().isNotFound());
        mvc.perform(put("/api/v1/courses/999").contentType(MediaType.APPLICATION_JSON).content(course(999)))
                .andExpect(status().isNotFound());
        mvc.perform(delete("/api/v1/courses/999")).andExpect(status().isNotFound());
        assertEquals(2, courses.count());
    }

    @Test
    void mismatchedUpdateIdReturns400WithoutMutation() throws Exception {
        mvc.perform(put("/api/v1/courses/1").contentType(MediaType.APPLICATION_JSON).content(course(2)))
                .andExpect(status().isBadRequest());
        assertEquals("SE201", courses.findById(1L).orElseThrow().code());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "{\"id\":-1,\"code\":\"SE411\",\"name\":\"Course\",\"credits\":3}",
        "{\"id\":100,\"code\":\"\",\"name\":\"Course\",\"credits\":3}",
        "{\"id\":100,\"code\":\"   \",\"name\":\"Course\",\"credits\":3}",
        "{\"id\":100,\"code\":\"SE411\",\"name\":\"A\",\"credits\":3}",
        "{\"id\":100,\"code\":\"SE411\",\"name\":\"Course\",\"credits\":0}",
        "{\"id\":100,\"code\":\"SE411\",\"name\":\"Course\",\"credits\":11}",
        "{\"id\":100,\"code\":\"SE411\",\"name\":\"Course\",\"credits\":null}",
        "{\"code\":\"SE411\",\"name\":\"Course\",\"credits\":3}",
        "{"
    })
    void rejectsInvalidPostBodies(String body) throws Exception {
        mvc.perform(post("/api/v1/courses").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        assertEquals(2, courses.count());
    }

    @Test
    void validatesPutAsWellAsPost() throws Exception {
        mvc.perform(put("/api/v1/courses/1").contentType(MediaType.APPLICATION_JSON)
                .content(course(1).replace("\"credits\":3", "\"credits\":11")))
                .andExpect(status().isBadRequest());
        assertEquals(3, courses.findById(1L).orElseThrow().credits());
    }

    @Test
    void permitsZeroIdAndCreditBoundaries() throws Exception {
        mvc.perform(post("/api/v1/courses").contentType(MediaType.APPLICATION_JSON)
                .content(course(0).replace("\"credits\":3", "\"credits\":1")))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/v1/courses").contentType(MediaType.APPLICATION_JSON)
                .content(course(100).replace("\"credits\":3", "\"credits\":10")))
                .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "abc", "9223372036854775808"})
    void invalidPathIdsReturn400(String id) throws Exception {
        mvc.perform(get("/api/v1/courses/" + id)).andExpect(status().isBadRequest());
        mvc.perform(delete("/api/v1/courses/" + id)).andExpect(status().isBadRequest());
    }
}

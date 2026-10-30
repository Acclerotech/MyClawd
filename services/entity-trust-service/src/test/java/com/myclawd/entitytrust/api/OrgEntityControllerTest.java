package com.myclawd.entitytrust.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.myclawd.platform.web.ApiExceptionHandler;
import com.myclawd.platform.web.NotFoundException;
import com.myclawd.entitytrust.app.OrgEntityService;
import com.myclawd.entitytrust.domain.OrgEntity;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class OrgEntityControllerTest {

    private OrgEntityService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(OrgEntityService.class);
        mvc = MockMvcBuilders.standaloneSetup(new OrgEntityController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void listsRecords() throws Exception {
        when(service.list()).thenReturn(List.of(new OrgEntity("name-value", "entityType-value", "country-value")));

        mvc.perform(get("/api/entity-trust/entities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void returns404WhenMissing() throws Exception {
        when(service.get(99L)).thenThrow(new NotFoundException("Entity", 99L));

        mvc.perform(get("/api/entity-trust/entities/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createsRecord() throws Exception {
        when(service.create(any(), eq("tester"))).thenReturn(new OrgEntity("name-value", "entityType-value", "country-value"));

        mvc.perform(post("/api/entity-trust/entities")
                        .header("X-Actor", "tester")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"name-value\",\"entityType\":\"entityType-value\",\"country\":\"country-value\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    void rejectsInvalidRequest() throws Exception {
        mvc.perform(post("/api/entity-trust/entities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$..name").exists());
    }
}

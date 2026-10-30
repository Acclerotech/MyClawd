package com.myclawd.ra.api;

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
import com.myclawd.ra.app.RaCaseService;
import com.myclawd.ra.domain.RaCase;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class RaCaseControllerTest {

    private RaCaseService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(RaCaseService.class);
        mvc = MockMvcBuilders.standaloneSetup(new RaCaseController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void listsRecords() throws Exception {
        when(service.list()).thenReturn(List.of(new RaCase("subjectType-value", 1L, "certRef-value")));

        mvc.perform(get("/api/registration-authority/cases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void returns404WhenMissing() throws Exception {
        when(service.get(99L)).thenThrow(new NotFoundException("RA case", 99L));

        mvc.perform(get("/api/registration-authority/cases/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createsRecord() throws Exception {
        when(service.create(any(), eq("tester"))).thenReturn(new RaCase("subjectType-value", 1L, "certRef-value"));

        mvc.perform(post("/api/registration-authority/cases")
                        .header("X-Actor", "tester")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"subjectType\":\"subjectType-value\",\"subjectId\":1,\"certRef\":\"certRef-value\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.subjectType").exists());
    }

    @Test
    void rejectsInvalidRequest() throws Exception {
        mvc.perform(post("/api/registration-authority/cases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$..subjectType").exists());
    }
}

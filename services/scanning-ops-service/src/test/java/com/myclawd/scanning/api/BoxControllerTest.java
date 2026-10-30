package com.myclawd.scanning.api;

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
import com.myclawd.scanning.app.BoxService;
import com.myclawd.scanning.domain.Box;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class BoxControllerTest {

    private BoxService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(BoxService.class);
        mvc = MockMvcBuilders.standaloneSetup(new BoxController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void listsRecords() throws Exception {
        when(service.list()).thenReturn(List.of(new Box(1L, "boxNumber-value", "status-value")));

        mvc.perform(get("/api/scanning-ops/boxes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void returns404WhenMissing() throws Exception {
        when(service.get(99L)).thenThrow(new NotFoundException("Box", 99L));

        mvc.perform(get("/api/scanning-ops/boxes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createsRecord() throws Exception {
        when(service.create(any(), eq("tester"))).thenReturn(new Box(1L, "boxNumber-value", "status-value"));

        mvc.perform(post("/api/scanning-ops/boxes")
                        .header("X-Actor", "tester")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"workOrderId\":1,\"boxNumber\":\"boxNumber-value\",\"status\":\"status-value\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.workOrderId").exists());
    }

    @Test
    void rejectsInvalidRequest() throws Exception {
        mvc.perform(post("/api/scanning-ops/boxes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$..workOrderId").exists());
    }
}

package com.myclawd.reports.api;

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
import com.myclawd.reports.app.ReportJobService;
import com.myclawd.reports.domain.ReportJob;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ReportJobControllerTest {

    private ReportJobService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(ReportJobService.class);
        mvc = MockMvcBuilders.standaloneSetup(new ReportJobController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void listsRecords() throws Exception {
        when(service.list()).thenReturn(List.of(new ReportJob("reportType-value", 1L, "requestedBy-value", "blobUri-value")));

        mvc.perform(get("/api/reports/report-jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void returns404WhenMissing() throws Exception {
        when(service.get(99L)).thenThrow(new NotFoundException("Report job", 99L));

        mvc.perform(get("/api/reports/report-jobs/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createsRecord() throws Exception {
        when(service.create(any(), eq("tester"))).thenReturn(new ReportJob("reportType-value", 1L, "requestedBy-value", "blobUri-value"));

        mvc.perform(post("/api/reports/report-jobs")
                        .header("X-Actor", "tester")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reportType\":\"reportType-value\",\"workOrderId\":1,\"requestedBy\":\"requestedBy-value\",\"blobUri\":\"blobUri-value\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reportType").exists());
    }

    @Test
    void rejectsInvalidRequest() throws Exception {
        mvc.perform(post("/api/reports/report-jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$..reportType").exists());
    }
}

package com.myclawd.audit.api;

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
import com.myclawd.audit.app.AuditEventService;
import com.myclawd.audit.domain.AuditEvent;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AuditEventControllerTest {

    private AuditEventService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(AuditEventService.class);
        mvc = MockMvcBuilders.standaloneSetup(new AuditEventController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void listsRecords() throws Exception {
        when(service.list()).thenReturn(List.of(new AuditEvent("source-value", "actor-value", "event-value", "evidence-value", "correlationId-value", "occurredAt-value")));

        mvc.perform(get("/api/audit/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void returns404WhenMissing() throws Exception {
        when(service.get(99L)).thenThrow(new NotFoundException("Audit event", 99L));

        mvc.perform(get("/api/audit/events/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createsRecord() throws Exception {
        when(service.create(any(), eq("tester"))).thenReturn(new AuditEvent("source-value", "actor-value", "event-value", "evidence-value", "correlationId-value", "occurredAt-value"));

        mvc.perform(post("/api/audit/events")
                        .header("X-Actor", "tester")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"source\":\"source-value\",\"actor\":\"actor-value\",\"event\":\"event-value\",\"evidence\":\"evidence-value\",\"correlationId\":\"correlationId-value\",\"occurredAt\":\"occurredAt-value\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.source").exists());
    }

    @Test
    void rejectsInvalidRequest() throws Exception {
        mvc.perform(post("/api/audit/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$..source").exists());
    }
}

package com.myclawd.dashboard.api;

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
import com.myclawd.dashboard.app.NotificationService;
import com.myclawd.dashboard.domain.Notification;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class NotificationControllerTest {

    private NotificationService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(NotificationService.class);
        mvc = MockMvcBuilders.standaloneSetup(new NotificationController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void listsRecords() throws Exception {
        when(service.list()).thenReturn(List.of(new Notification(1L, "title-value", "body-value")));

        mvc.perform(get("/api/dashboard-notifications/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void returns404WhenMissing() throws Exception {
        when(service.get(99L)).thenThrow(new NotFoundException("Notification", 99L));

        mvc.perform(get("/api/dashboard-notifications/notifications/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createsRecord() throws Exception {
        when(service.create(any(), eq("tester"))).thenReturn(new Notification(1L, "title-value", "body-value"));

        mvc.perform(post("/api/dashboard-notifications/notifications")
                        .header("X-Actor", "tester")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"recipientUserId\":1,\"title\":\"title-value\",\"body\":\"body-value\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.recipientUserId").exists());
    }

    @Test
    void rejectsInvalidRequest() throws Exception {
        mvc.perform(post("/api/dashboard-notifications/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$..recipientUserId").exists());
    }
}

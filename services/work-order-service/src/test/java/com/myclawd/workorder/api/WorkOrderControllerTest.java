package com.myclawd.workorder.api;

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
import com.myclawd.workorder.app.WorkOrderService;
import com.myclawd.workorder.domain.WorkOrder;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class WorkOrderControllerTest {

    private WorkOrderService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(WorkOrderService.class);
        mvc = MockMvcBuilders.standaloneSetup(new WorkOrderController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void listsRecords() throws Exception {
        when(service.list()).thenReturn(List.of(new WorkOrder("reference-value", 1L, 1L, 1L, 1L)));

        mvc.perform(get("/api/work-order/work-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void returns404WhenMissing() throws Exception {
        when(service.get(99L)).thenThrow(new NotFoundException("Work order", 99L));

        mvc.perform(get("/api/work-order/work-orders/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createsRecord() throws Exception {
        when(service.create(any(), eq("tester"))).thenReturn(new WorkOrder("reference-value", 1L, 1L, 1L, 1L));

        mvc.perform(post("/api/work-order/work-orders")
                        .header("X-Actor", "tester")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reference\":\"reference-value\",\"clientEntityId\":1,\"providerEntityId\":1,\"clientContactUserId\":1,\"assignedManagerId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reference").exists());
    }

    @Test
    void rejectsInvalidRequest() throws Exception {
        mvc.perform(post("/api/work-order/work-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$..reference").exists());
    }
}

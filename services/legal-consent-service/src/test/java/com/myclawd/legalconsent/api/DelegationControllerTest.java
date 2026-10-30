package com.myclawd.legalconsent.api;

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
import com.myclawd.legalconsent.app.DelegationService;
import com.myclawd.legalconsent.domain.Delegation;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class DelegationControllerTest {

    private DelegationService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(DelegationService.class);
        mvc = MockMvcBuilders.standaloneSetup(new DelegationController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void listsRecords() throws Exception {
        when(service.list()).thenReturn(List.of(new Delegation(1L, 1L, 1L, 1L)));

        mvc.perform(get("/api/legal-consent/delegations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void returns404WhenMissing() throws Exception {
        when(service.get(99L)).thenThrow(new NotFoundException("Delegation", 99L));

        mvc.perform(get("/api/legal-consent/delegations/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createsRecord() throws Exception {
        when(service.create(any(), eq("tester"))).thenReturn(new Delegation(1L, 1L, 1L, 1L));

        mvc.perform(post("/api/legal-consent/delegations")
                        .header("X-Actor", "tester")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clientEntityId\":1,\"providerEntityId\":1,\"delegateUserId\":1,\"grantedByUserId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clientEntityId").exists());
    }

    @Test
    void rejectsInvalidRequest() throws Exception {
        mvc.perform(post("/api/legal-consent/delegations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$..clientEntityId").exists());
    }
}

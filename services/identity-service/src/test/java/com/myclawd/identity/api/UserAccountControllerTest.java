package com.myclawd.identity.api;

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
import com.myclawd.identity.app.UserAccountService;
import com.myclawd.identity.domain.UserAccount;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class UserAccountControllerTest {

    private UserAccountService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(UserAccountService.class);
        mvc = MockMvcBuilders.standaloneSetup(new UserAccountController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void listsRecords() throws Exception {
        when(service.list()).thenReturn(List.of(new UserAccount("fullName-value", "email-value", "role-value", "accountType-value")));

        mvc.perform(get("/api/identity/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void returns404WhenMissing() throws Exception {
        when(service.get(99L)).thenThrow(new NotFoundException("User", 99L));

        mvc.perform(get("/api/identity/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createsRecord() throws Exception {
        when(service.create(any(), eq("tester"))).thenReturn(new UserAccount("fullName-value", "email-value", "role-value", "accountType-value"));

        mvc.perform(post("/api/identity/users")
                        .header("X-Actor", "tester")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"fullName-value\",\"email\":\"email-value\",\"role\":\"role-value\",\"accountType\":\"accountType-value\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").exists());
    }

    @Test
    void rejectsInvalidRequest() throws Exception {
        mvc.perform(post("/api/identity/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$..fullName").exists());
    }
}

package com.myclawd.identity.api;

import com.myclawd.identity.app.UserAccountService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/identity/users")
public class UserAccountController {

    private final UserAccountService service;

    public UserAccountController(UserAccountService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserAccountResponse> list() {
        return service.list().stream().map(UserAccountResponse::from).toList();
    }

    @GetMapping("/{id}")
    public UserAccountResponse get(@PathVariable Long id) {
        return UserAccountResponse.from(service.get(id));
    }

    // X-Actor is a placeholder until the gateway validates Entra tokens and forwards the caller.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserAccountResponse create(@Valid @RequestBody UserAccountRequest request,
                              @RequestHeader(value = "X-Actor", defaultValue = "system") String actor) {
        return UserAccountResponse.from(service.create(request, actor));
    }

    @GetMapping("/by-code/{userCode}")
    public UserAccountResponse getByCode(@PathVariable String userCode) {
        return UserAccountResponse.from(service.getByCode(userCode));
    }
}

package com.myclawd.dashboard.api;

import com.myclawd.dashboard.app.NotificationService;
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
@RequestMapping("/api/dashboard-notifications/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping
    public List<NotificationResponse> list() {
        return service.list().stream().map(NotificationResponse::from).toList();
    }

    @GetMapping("/{id}")
    public NotificationResponse get(@PathVariable Long id) {
        return NotificationResponse.from(service.get(id));
    }

    // X-Actor is a placeholder until the gateway validates Entra tokens and forwards the caller.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationResponse create(@Valid @RequestBody NotificationRequest request,
                              @RequestHeader(value = "X-Actor", defaultValue = "system") String actor) {
        return NotificationResponse.from(service.create(request, actor));
    }
}

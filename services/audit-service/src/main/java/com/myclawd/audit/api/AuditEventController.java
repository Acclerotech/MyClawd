package com.myclawd.audit.api;

import com.myclawd.audit.app.AuditEventService;
import jakarta.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
@RequestMapping("/api/audit/events")
public class AuditEventController {

    private final AuditEventService service;

    public AuditEventController(AuditEventService service) {
        this.service = service;
    }

    @GetMapping
    public List<AuditEventResponse> list() {
        return service.list().stream().map(AuditEventResponse::from).toList();
    }

    @GetMapping("/{id}")
    public AuditEventResponse get(@PathVariable Long id) {
        return AuditEventResponse.from(service.get(id));
    }

    // X-Actor is a placeholder until the gateway validates Entra tokens and forwards the caller.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuditEventResponse create(@Valid @RequestBody AuditEventRequest request,
                              @RequestHeader(value = "X-Actor", defaultValue = "system") String actor) {
        return AuditEventResponse.from(service.create(request, actor));
    }

    @GetMapping("/verify")
    public Map<String, Object> verify() {
        Long broken = service.firstBroken();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("valid", broken == null);
        result.put("firstBrokenId", broken);
        return result;
    }
}

package com.myclawd.ra.api;

import com.myclawd.ra.app.RaCaseService;
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
@RequestMapping("/api/registration-authority/cases")
public class RaCaseController {

    private final RaCaseService service;

    public RaCaseController(RaCaseService service) {
        this.service = service;
    }

    @GetMapping
    public List<RaCaseResponse> list() {
        return service.list().stream().map(RaCaseResponse::from).toList();
    }

    @GetMapping("/{id}")
    public RaCaseResponse get(@PathVariable Long id) {
        return RaCaseResponse.from(service.get(id));
    }

    // X-Actor is a placeholder until the gateway validates Entra tokens and forwards the caller.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RaCaseResponse create(@Valid @RequestBody RaCaseRequest request,
                              @RequestHeader(value = "X-Actor", defaultValue = "system") String actor) {
        return RaCaseResponse.from(service.create(request, actor));
    }
}

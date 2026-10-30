package com.myclawd.scanning.api;

import com.myclawd.scanning.app.BoxService;
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
@RequestMapping("/api/scanning-ops/boxes")
public class BoxController {

    private final BoxService service;

    public BoxController(BoxService service) {
        this.service = service;
    }

    @GetMapping
    public List<BoxResponse> list() {
        return service.list().stream().map(BoxResponse::from).toList();
    }

    @GetMapping("/{id}")
    public BoxResponse get(@PathVariable Long id) {
        return BoxResponse.from(service.get(id));
    }

    // X-Actor is a placeholder until the gateway validates Entra tokens and forwards the caller.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BoxResponse create(@Valid @RequestBody BoxRequest request,
                              @RequestHeader(value = "X-Actor", defaultValue = "system") String actor) {
        return BoxResponse.from(service.create(request, actor));
    }
}

package com.myclawd.entitytrust.api;

import com.myclawd.entitytrust.app.OrgEntityService;
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
@RequestMapping("/api/entity-trust/entities")
public class OrgEntityController {

    private final OrgEntityService service;

    public OrgEntityController(OrgEntityService service) {
        this.service = service;
    }

    @GetMapping
    public List<OrgEntityResponse> list() {
        return service.list().stream().map(OrgEntityResponse::from).toList();
    }

    @GetMapping("/{id}")
    public OrgEntityResponse get(@PathVariable Long id) {
        return OrgEntityResponse.from(service.get(id));
    }

    // X-Actor is a placeholder until the gateway validates Entra tokens and forwards the caller.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrgEntityResponse create(@Valid @RequestBody OrgEntityRequest request,
                              @RequestHeader(value = "X-Actor", defaultValue = "system") String actor) {
        return OrgEntityResponse.from(service.create(request, actor));
    }
}

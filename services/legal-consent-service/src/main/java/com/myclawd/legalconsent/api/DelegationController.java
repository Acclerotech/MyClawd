package com.myclawd.legalconsent.api;

import com.myclawd.legalconsent.app.DelegationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/legal-consent/delegations")
public class DelegationController {

    private final DelegationService service;

    public DelegationController(DelegationService service) {
        this.service = service;
    }

    @GetMapping
    public List<DelegationResponse> list() {
        return service.list().stream().map(DelegationResponse::from).toList();
    }

    @GetMapping("/{id}")
    public DelegationResponse get(@PathVariable Long id) {
        return DelegationResponse.from(service.get(id));
    }

    // X-Actor is a placeholder until the gateway validates Entra tokens and forwards the caller.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DelegationResponse create(@Valid @RequestBody DelegationRequest request,
                              @RequestHeader(value = "X-Actor", defaultValue = "system") String actor) {
        return DelegationResponse.from(service.create(request, actor));
    }

    @PostMapping("/{id}/revoke")
    public DelegationResponse revoke(@PathVariable Long id,
                                     @RequestHeader(value = "X-Actor", defaultValue = "system") String actor) {
        return DelegationResponse.from(service.revoke(id, actor));
    }

    @GetMapping("/active")
    public List<DelegationResponse> active(@RequestParam Long clientEntityId) {
        return service.activeFor(clientEntityId).stream().map(DelegationResponse::from).toList();
    }
}

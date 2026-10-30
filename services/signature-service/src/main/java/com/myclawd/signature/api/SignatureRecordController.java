package com.myclawd.signature.api;

import com.myclawd.signature.app.SignatureRecordService;
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
@RequestMapping("/api/signature/signatures")
public class SignatureRecordController {

    private final SignatureRecordService service;

    public SignatureRecordController(SignatureRecordService service) {
        this.service = service;
    }

    @GetMapping
    public List<SignatureRecordResponse> list() {
        return service.list().stream().map(SignatureRecordResponse::from).toList();
    }

    @GetMapping("/{id}")
    public SignatureRecordResponse get(@PathVariable Long id) {
        return SignatureRecordResponse.from(service.get(id));
    }

    // X-Actor is a placeholder until the gateway validates Entra tokens and forwards the caller.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SignatureRecordResponse create(@Valid @RequestBody SignatureRecordRequest request,
                              @RequestHeader(value = "X-Actor", defaultValue = "system") String actor) {
        return SignatureRecordResponse.from(service.create(request, actor));
    }
}

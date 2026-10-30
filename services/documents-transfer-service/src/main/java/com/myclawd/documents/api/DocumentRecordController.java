package com.myclawd.documents.api;

import com.myclawd.documents.app.DocumentRecordService;
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
@RequestMapping("/api/documents-transfer/documents")
public class DocumentRecordController {

    private final DocumentRecordService service;

    public DocumentRecordController(DocumentRecordService service) {
        this.service = service;
    }

    @GetMapping
    public List<DocumentRecordResponse> list() {
        return service.list().stream().map(DocumentRecordResponse::from).toList();
    }

    @GetMapping("/{id}")
    public DocumentRecordResponse get(@PathVariable Long id) {
        return DocumentRecordResponse.from(service.get(id));
    }

    // X-Actor is a placeholder until the gateway validates Entra tokens and forwards the caller.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentRecordResponse create(@Valid @RequestBody DocumentRecordRequest request,
                              @RequestHeader(value = "X-Actor", defaultValue = "system") String actor) {
        return DocumentRecordResponse.from(service.create(request, actor));
    }
}

package com.myclawd.reports.api;

import com.myclawd.reports.app.ReportJobService;
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
@RequestMapping("/api/reports/report-jobs")
public class ReportJobController {

    private final ReportJobService service;

    public ReportJobController(ReportJobService service) {
        this.service = service;
    }

    @GetMapping
    public List<ReportJobResponse> list() {
        return service.list().stream().map(ReportJobResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ReportJobResponse get(@PathVariable Long id) {
        return ReportJobResponse.from(service.get(id));
    }

    // X-Actor is a placeholder until the gateway validates Entra tokens and forwards the caller.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReportJobResponse create(@Valid @RequestBody ReportJobRequest request,
                              @RequestHeader(value = "X-Actor", defaultValue = "system") String actor) {
        return ReportJobResponse.from(service.create(request, actor));
    }
}

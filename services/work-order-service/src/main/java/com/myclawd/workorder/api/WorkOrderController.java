package com.myclawd.workorder.api;

import com.myclawd.workorder.app.WorkOrderService;
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
@RequestMapping("/api/work-order/work-orders")
public class WorkOrderController {

    private final WorkOrderService service;

    public WorkOrderController(WorkOrderService service) {
        this.service = service;
    }

    @GetMapping
    public List<WorkOrderResponse> list() {
        return service.list().stream().map(WorkOrderResponse::from).toList();
    }

    @GetMapping("/{id}")
    public WorkOrderResponse get(@PathVariable Long id) {
        return WorkOrderResponse.from(service.get(id));
    }

    // X-Actor is a placeholder until the gateway validates Entra tokens and forwards the caller.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkOrderResponse create(@Valid @RequestBody WorkOrderRequest request,
                              @RequestHeader(value = "X-Actor", defaultValue = "system") String actor) {
        return WorkOrderResponse.from(service.create(request, actor));
    }

    @PostMapping("/{id}/advance")
    public WorkOrderResponse advance(@PathVariable Long id,
                                     @RequestHeader(value = "X-Actor", defaultValue = "system") String actor) {
        return WorkOrderResponse.from(service.advance(id, actor));
    }
}

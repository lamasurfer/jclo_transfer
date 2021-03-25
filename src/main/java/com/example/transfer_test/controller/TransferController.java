package com.example.transfer_test.controller;

import com.example.transfer_test.model.request.ConfirmationRequest;
import com.example.transfer_test.model.request.TransferRequest;
import com.example.transfer_test.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@CrossOrigin
@RestController
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<Object> transfer(@Valid @RequestBody TransferRequest request) {
        return transferService.transfer(request);
    }

    @PostMapping("/confirmOperation")
    public ResponseEntity<Object> confirmOperation(@Valid @RequestBody ConfirmationRequest request) {
        return transferService.confirmOperation(request);
    }
}

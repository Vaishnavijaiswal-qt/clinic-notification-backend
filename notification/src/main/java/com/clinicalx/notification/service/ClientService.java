package com.clinicalx.notification.service;

import com.clinicalx.notification.dto.ClientResponse;
import com.clinicalx.notification.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientService {

    private final ClientRepository clientRepository;

    public List<ClientResponse> getClients() {

        List<ClientResponse> responses = new ArrayList<>();

        for (var client : clientRepository.findAll()) {
            responses.add(
                    new ClientResponse(client.getId(), client.getName())
            );
        }

        return responses;
    }
}
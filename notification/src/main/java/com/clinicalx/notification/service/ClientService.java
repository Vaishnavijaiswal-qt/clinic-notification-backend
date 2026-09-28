package com.clinicalx.notification.service;

import com.clinicalx.notification.dto.ClientResponse;
import com.clinicalx.notification.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientService {

    private final ClientRepository clientRepository;

    public List<ClientResponse> getClients() {

        return clientRepository.findAll().
                stream().map(client -> new ClientResponse(client.getId(), client.getName())).toList();
    }

}

package com.duoc.pedidos.Client;

import com.duoc.pedidos.dto.ClienteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "cliente-service", url = "${api.clientes.url}")
public interface ClienteClient {

    @GetMapping("/clientes")
    List<ClienteDTO> obtenerClientes();
}

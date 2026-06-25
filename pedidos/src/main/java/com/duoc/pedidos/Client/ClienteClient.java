package com.duoc.pedidos.client;

import com.duoc.pedidos.dto.ClienteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cliente-service", url = "${api.clientes.url}")
public interface ClienteClient {

    @GetMapping("/clientes/{id}")
    ClienteDTO obtenerClientePorId(@PathVariable("id") Long id);
}

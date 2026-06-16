package com.microservicio.cliente.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuración de WebClient para la comunicación con otros microservicios.
 * Cada bean representa un cliente HTTP hacia un servicio diferente.
 */
@Configuration
public class WebClientConfig {

    @Value("${microservicios.pedidos.url}")
    private String pedidosUrl;

    @Value("${microservicios.despacho.url}")
    private String despachoUrl;

    /**
     * Cliente HTTP apuntando al microservicio de pedidos.
     * Usado para verificar historial de pedidos de un cliente.
     */
    @Bean(name = "pedidosWebClient")
    public WebClient pedidosWebClient() {
        return WebClient.builder()
                .baseUrl(pedidosUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * Cliente HTTP apuntando al microservicio de despacho.
     * Usado para consultar estado de despachos asociados a un cliente.
     */
    @Bean(name = "despachoWebClient")
    public WebClient despachoWebClient() {
        return WebClient.builder()
                .baseUrl(despachoUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}

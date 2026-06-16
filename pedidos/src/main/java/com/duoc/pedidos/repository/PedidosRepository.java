package com.duoc.pedidos.repository;


import com.duoc.pedidos.model.Pedidos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidosRepository extends JpaRepository<Pedidos, Integer> {
    List<Pedidos> findByIdCliente(Integer idCliente);
}

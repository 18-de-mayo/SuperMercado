package com.duoc.pedidos.repository;

import com.duoc.pedidos.model.DetallePedidos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedidos, Integer>{

}

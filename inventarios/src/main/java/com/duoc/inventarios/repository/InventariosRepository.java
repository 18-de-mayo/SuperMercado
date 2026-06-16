package com.duoc.inventarios.repository;

import com.duoc.inventarios.model.Inventarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventariosRepository extends JpaRepository<Inventarios, Integer> {
}

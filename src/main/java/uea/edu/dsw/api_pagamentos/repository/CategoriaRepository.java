package uea.edu.dsw.api_pagamentos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uea.edu.dsw.api_pagamentos.model.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long>  {

}

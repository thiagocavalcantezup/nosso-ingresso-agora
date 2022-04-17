package br.com.zup.edu.ingressoagora.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.zup.edu.ingressoagora.model.Ingresso;

public interface IngressoRepository extends JpaRepository<Ingresso, Long> {

}

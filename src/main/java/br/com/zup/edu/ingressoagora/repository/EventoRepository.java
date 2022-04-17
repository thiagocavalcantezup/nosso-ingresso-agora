package br.com.zup.edu.ingressoagora.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.zup.edu.ingressoagora.model.Evento;

public interface EventoRepository extends JpaRepository<Evento, Long> {

}

package br.com.zup.edu.ingressoagora.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Entity
public class Ingresso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EstadoIngresso estado = EstadoIngresso.NAOCONSUMIDO;

    @Column(nullable = false)
    private LocalDateTime compradoEm = LocalDateTime.now();

    @ManyToOne(optional = false)
    private Evento evento;

    public Ingresso(EstadoIngresso estado) {
        this.estado = estado;
    }

    /**
     * @deprecated construtor para uso exclusivo do Hibernate
     */
    @Deprecated
    public Ingresso() {}

    public void cancelar() {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

        if (!isNaoConsumido()) {
            throw new ResponseStatusException(
                status, "Impossível cancelar o ingresso. Ele já foi cancelado ou consumido."
            );
        }

        if (!isDentroDoPrazoDeCancelamento()) {
            throw new ResponseStatusException(
                status, "Impossível cancelar o ingresso. Ele está fora do prazo de cancelamento."
            );
        }

        estado = EstadoIngresso.CANCELADO;
    }

    public boolean isNaoConsumido() {
        return this.estado.equals(EstadoIngresso.NAOCONSUMIDO);
    }

    public boolean isDentroDoPrazoDeCancelamento() {
        return ChronoUnit.DAYS.between(LocalDateTime.now(), evento.getData()) >= 1;
    }

    public Long getId() {
        return id;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

}

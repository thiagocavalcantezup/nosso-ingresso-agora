package br.com.zup.edu.ingressoagora.model;

import static br.com.zup.edu.ingressoagora.model.EstadoIngresso.NAOCONSUMIDO;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Ingresso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EstadoIngresso estado = NAOCONSUMIDO;

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

    public Long getId() {
        return id;
    }

}

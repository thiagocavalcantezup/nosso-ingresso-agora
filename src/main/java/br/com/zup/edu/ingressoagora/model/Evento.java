package br.com.zup.edu.ingressoagora.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private LocalDateTime data;

    @OneToMany(mappedBy = "evento")
    private List<Ingresso> ingressos = new ArrayList<>();

    @Column(nullable = false)
    private BigDecimal preco;

    public Evento(String titulo, LocalDateTime data, BigDecimal preco) {
        this.titulo = titulo;
        this.data = data;
        this.preco = preco;
    }

    public Evento() {}

    public void adicionar(Ingresso ingresso) {
        ingresso.setEvento(this);
        this.ingressos.add(ingresso);
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getData() {
        return data;
    }

}

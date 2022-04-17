package br.com.zup.edu.ingressoagora;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import br.com.zup.edu.ingressoagora.model.EstadoIngresso;
import br.com.zup.edu.ingressoagora.model.Evento;
import br.com.zup.edu.ingressoagora.model.Ingresso;
import br.com.zup.edu.ingressoagora.repository.EventoRepository;
import br.com.zup.edu.ingressoagora.repository.IngressoRepository;

@Component
public class DataLoader implements CommandLineRunner {

    private final IngressoRepository ingressoRepository;
    private final EventoRepository eventoRepository;

    public DataLoader(IngressoRepository ingressoRepository, EventoRepository eventoRepository) {
        this.ingressoRepository = ingressoRepository;
        this.eventoRepository = eventoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Evento evento1 = new Evento(
            "Dr. Strange 2", LocalDateTime.now().plusMonths(1), new BigDecimal("20.0")
        );
        eventoRepository.save(evento1);

        Ingresso ingressoConsumido = new Ingresso();
        ingressoConsumido.setEvento(evento1);
        ingressoConsumido.setEstado(EstadoIngresso.CONSUMIDO);

        Ingresso ingressoCancelavel = new Ingresso();
        ingressoCancelavel.setEvento(evento1);

        ingressoRepository.save(ingressoConsumido);
        ingressoRepository.save(ingressoCancelavel);

        evento1.adicionar(ingressoConsumido);
        evento1.adicionar(ingressoCancelavel);
        eventoRepository.save(evento1);

        Evento evento2 = new Evento(
            "Adam Sandler Movie", LocalDateTime.now(), new BigDecimal("10.0")
        );
        eventoRepository.save(evento2);

        Ingresso ingresso = new Ingresso();
        ingresso.setEvento(evento2);

        ingressoRepository.save(ingresso);

        evento2.adicionar(ingresso);
        eventoRepository.save(evento2);
    }

}

package br.com.zup.edu.ingressoagora.controller;


import br.com.zup.edu.ingressoagora.model.EstadoIngresso;
import br.com.zup.edu.ingressoagora.model.Evento;
import br.com.zup.edu.ingressoagora.model.Ingresso;
import br.com.zup.edu.ingressoagora.repository.EventoRepository;
import br.com.zup.edu.ingressoagora.repository.IngressoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class CancelarIngressoControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IngressoRepository ingressoRepository;
    @Autowired
    private EventoRepository eventoRepository;

    private Evento evento;


    @BeforeEach
    void setUp() {
        ingressoRepository.deleteAll();
        eventoRepository.deleteAll();
    }


    @Test
    @DisplayName("nao deve cancelar um ingresso inexistente")
    void test() throws Exception {
        MockHttpServletRequestBuilder request = patch("/ingressos/{id}/cancelamento", Integer.MAX_VALUE)
                .contentType(MediaType.APPLICATION_JSON);


        Exception resolvedException = mockMvc.perform(request)
                .andExpect(
                        status().isNotFound()
                )
                .andReturn()
                .getResolvedException();

        assertNotNull(resolvedException);
        assertEquals(ResponseStatusException.class, resolvedException.getClass());
        assertEquals("Este ingresso não existe.", ((ResponseStatusException) resolvedException).getReason());


    }


    @Test
    @DisplayName("nao deve cancelar um ingresso no dia do evento")
    void test1() throws Exception {
        this.evento = new Evento(
                "Imersao Testes de Integracao com Spring",
                LocalDate.now(),
                new BigDecimal("1000000")
        );

        eventoRepository.save(evento);

        Ingresso ingresso = new Ingresso(evento);

        ingressoRepository.save(ingresso);

        MockHttpServletRequestBuilder request = patch("/ingressos/{id}/cancelamento", ingresso.getId())
                .contentType(MediaType.APPLICATION_JSON);


        Exception resolvedException = mockMvc.perform(request)
                .andExpect(
                        status().isUnprocessableEntity()
                )
                .andReturn()
                .getResolvedException();

        assertNotNull(resolvedException);
        assertEquals(ResponseStatusException.class, resolvedException.getClass());
        assertEquals(
                "Não é possivel cancelar faltando menos de 1 dia para data do evento",
                ((ResponseStatusException) resolvedException).getReason()
        );


    }

    @Test
    @DisplayName("nao deve cancelar um ingresso ja consumido")
    void test2() throws Exception {
        this.evento = new Evento(
                "Imersao Testes de Integracao com Spring",
                LocalDate.now().plusDays(2),
                new BigDecimal("1000000")
        );

        eventoRepository.save(evento);

        Ingresso ingresso = new Ingresso(evento);

        ingresso.consumir();

        ingressoRepository.save(ingresso);

        MockHttpServletRequestBuilder request = patch("/ingressos/{id}/cancelamento", ingresso.getId())
                .contentType(MediaType.APPLICATION_JSON);


        Exception resolvedException = mockMvc.perform(request)
                .andExpect(
                        status().isUnprocessableEntity()
                )
                .andReturn()
                .getResolvedException();

        assertNotNull(resolvedException);
        assertEquals(ResponseStatusException.class, resolvedException.getClass());
        assertEquals(
                "Impossivel cancelar um Ingresso já consumido.",
                ((ResponseStatusException) resolvedException).getReason()
        );

    }

    @Test
    @DisplayName("deve cancelar um ingresso")
    void test3() throws Exception {

        this.evento = new Evento(
                "Imersao Testes de Integracao com Spring",
                LocalDate.now().plusDays(2),
                new BigDecimal("1000000")
        );

        eventoRepository.save(evento);

        Ingresso ingresso = new Ingresso(evento);

        ingressoRepository.save(ingresso);

        MockHttpServletRequestBuilder request = patch("/ingressos/{id}/cancelamento", ingresso.getId())
                .contentType(MediaType.APPLICATION_JSON);


        mockMvc.perform(request)
                .andExpect(
                        status().isNoContent()
                );

        Optional<Ingresso> possivelIngresso = ingressoRepository.findById(ingresso.getId());
        assertTrue(possivelIngresso.isPresent());

        Ingresso ingressoPosCancelamento = possivelIngresso.get();
        assertEquals(EstadoIngresso.CANCELADO, ingressoPosCancelamento.getEstado());


    }

}
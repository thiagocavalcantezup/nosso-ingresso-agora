package br.com.zup.edu.ingressoagora.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import br.com.zup.edu.ingressoagora.model.EstadoIngresso;
import br.com.zup.edu.ingressoagora.model.Evento;
import br.com.zup.edu.ingressoagora.model.Ingresso;
import br.com.zup.edu.ingressoagora.repository.EventoRepository;
import br.com.zup.edu.ingressoagora.repository.IngressoRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CancelarIngressoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IngressoRepository ingressoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    private Evento eventoCancelavel;
    private Evento eventoNaoCancelavel;

    @BeforeEach
    void setUp() {
        ingressoRepository.deleteAll();
        eventoRepository.deleteAll();

        eventoCancelavel = new Evento(
            "Show do Patati Patatá", LocalDate.now().plusDays(2), new BigDecimal("20.00")
        );
        eventoNaoCancelavel = new Evento(
            "Show do Tiririca", LocalDate.now(), new BigDecimal("30.00")
        );
        eventoRepository.saveAll(List.of(eventoCancelavel, eventoNaoCancelavel));
    }

    @Test
    void deveCancelarUmIngresso() throws Exception {
        // cenário (given)
        //
        Ingresso ingresso = new Ingresso(eventoCancelavel);
        ingressoRepository.save(ingresso);

        MockHttpServletRequestBuilder request = patch(
            "/ingressos/{id}/cancelamento", ingresso.getId()
        ).contentType(APPLICATION_JSON);

        // ação (when) e corretude (then)
        //
        mockMvc.perform(request).andExpect(status().isNoContent());

        Optional<Ingresso> optionalIngresso = ingressoRepository.findById(ingresso.getId());

        assertTrue(optionalIngresso.isPresent());
        assertEquals(EstadoIngresso.CANCELADO, optionalIngresso.get().getEstado());
    }

    @Test
    void naoDeveCancelarUmIngressoNaoCadastrado() throws Exception {
        // cenário (given)
        //
        MockHttpServletRequestBuilder request = patch(
            "/ingressos/{id}/cancelamento", Integer.MAX_VALUE
        ).contentType(APPLICATION_JSON);

        // ação (when) e corretude (then)
        //
        Exception resolvedException = mockMvc.perform(request)
                                             .andExpect(status().isNotFound())
                                             .andReturn()
                                             .getResolvedException();

        assertNotNull(resolvedException);
        assertEquals(ResponseStatusException.class, resolvedException.getClass());
        ResponseStatusException responseStatusException = (ResponseStatusException) resolvedException;
        assertEquals("Este ingresso não existe.", responseStatusException.getReason());
    }

    @Test
    void naoDeveCancelarUmIngressoComMenosDeUmDiaDeAntecedencia() throws Exception {
        // cenário (given)
        //
        Ingresso ingresso = new Ingresso(eventoNaoCancelavel);
        ingressoRepository.save(ingresso);

        MockHttpServletRequestBuilder request = patch(
            "/ingressos/{id}/cancelamento", ingresso.getId()
        ).contentType(APPLICATION_JSON);

        // ação (when) e corretude (then)
        //
        Exception resolvedException = mockMvc.perform(request)
                                             .andExpect(status().isUnprocessableEntity())
                                             .andReturn()
                                             .getResolvedException();

        assertNotNull(resolvedException);
        assertEquals(ResponseStatusException.class, resolvedException.getClass());
        ResponseStatusException responseStatusException = (ResponseStatusException) resolvedException;
        assertEquals(
            "Não é possivel cancelar faltando menos de 1 dia para data do evento",
            responseStatusException.getReason()
        );
    }

    @Test
    void naoDeveCancelarUmIngressoJaConsumido() throws Exception {
        // cenário (given)
        //
        Ingresso ingresso = new Ingresso(eventoCancelavel);
        ingresso.consumir();
        ingressoRepository.save(ingresso);

        MockHttpServletRequestBuilder request = patch(
            "/ingressos/{id}/cancelamento", ingresso.getId()
        ).contentType(APPLICATION_JSON);

        // ação (when) e corretude (then)
        //
        Exception resolvedException = mockMvc.perform(request)
                                             .andExpect(status().isUnprocessableEntity())
                                             .andReturn()
                                             .getResolvedException();

        assertNotNull(resolvedException);
        assertEquals(ResponseStatusException.class, resolvedException.getClass());
        ResponseStatusException responseStatusException = (ResponseStatusException) resolvedException;
        assertEquals(
            "Impossivel cancelar um Ingresso já consumido.", responseStatusException.getReason()
        );
    }

}

package br.com.zup.edu.ingressoagora.controller;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.zup.edu.ingressoagora.model.Ingresso;
import br.com.zup.edu.ingressoagora.repository.IngressoRepository;

@RestController
@RequestMapping(IngressoController.BASE_URI)
public class IngressoController {

    public final static String BASE_URI = "/ingressos";

    private final IngressoRepository ingressoRepository;

    public IngressoController(IngressoRepository ingressoRepository) {
        this.ingressoRepository = ingressoRepository;
    }

    @Transactional
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        Ingresso ingresso = ingressoRepository.findById(id)
                                              .orElseThrow(
                                                  () -> new ResponseStatusException(
                                                      HttpStatus.NOT_FOUND,
                                                      "Não existe um ingresso com o id informado."
                                                  )
                                              );

        ingresso.cancelar();
        ingressoRepository.save(ingresso);

        return ResponseEntity.noContent().build();
    }

}

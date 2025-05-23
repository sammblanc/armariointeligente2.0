package br.com.unit.tokseg.armariointeligente.service;

import br.com.unit.tokseg.armariointeligente.exception.BadRequestException;
import br.com.unit.tokseg.armariointeligente.exception.ResourceAlreadyExistsException;
import br.com.unit.tokseg.armariointeligente.exception.ResourceNotFoundException;
import br.com.unit.tokseg.armariointeligente.model.*;
import br.com.unit.tokseg.armariointeligente.repository.CompartimentoRepository;
import br.com.unit.tokseg.armariointeligente.repository.EntregaRepository;
import br.com.unit.tokseg.armariointeligente.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EntregaService {

    @Autowired
    private EntregaRepository entregaRepository;

    @Autowired
    private CompartimentoRepository compartimentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CompartimentoService compartimentoService;

    @Transactional
    public Entrega registrarEntrega(Entrega entrega) {
        if (entrega == null) {
            throw new BadRequestException("Entrega não pode ser nula");
        }
        if (entrega.getCodigoRastreio() == null || entrega.getCodigoRastreio().isEmpty()) {
            throw new BadRequestException("Código de rastreio não pode ser nulo ou vazio");
        }
        if (entrega.getCompartimento() == null || entrega.getCompartimento().getId() == null) {
            throw new BadRequestException("Compartimento é obrigatório");
        }
        if (entrega.getEntregador() == null || entrega.getEntregador().getId() == null) {
            throw new BadRequestException("Entregador é obrigatório");
        }
        if (entrega.getDestinatario() == null || entrega.getDestinatario().getId() == null) {
            throw new BadRequestException("Destinatário é obrigatório");
        }

        entregaRepository.findByCodigoRastreio(entrega.getCodigoRastreio()).ifPresent(e -> {
            throw new ResourceAlreadyExistsException("Entrega", "código de rastreio", entrega.getCodigoRastreio());
        });

        Compartimento compartimento = compartimentoRepository.findById(entrega.getCompartimento().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Compartimento", "id", entrega.getCompartimento().getId()));

        if (compartimento.getOcupado()) {
            throw new BadRequestException("O compartimento selecionado já está ocupado");
        }

        Usuario entregador = usuarioRepository.findById(entrega.getEntregador().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Entregador", "id", entrega.getEntregador().getId()));

        Usuario destinatario = usuarioRepository.findById(entrega.getDestinatario().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Destinatário", "id", entrega.getDestinatario().getId()));

        // Verificar se o entregador tem permissão
        if (!entregador.getTipoUsuario().getNome().equals("Entregador") && 
            !entregador.getTipoUsuario().getNome().equals("Administrador")) {
            throw new BadRequestException("O usuário não tem permissão para registrar entregas");
        }

        // Definir valores padrão
        entrega.setDataEntrega(LocalDateTime.now());
        entrega.setStatus(StatusEntrega.ENTREGUE);
        entrega.setCompartimento(compartimento);
        entrega.setEntregador(entregador);
        entrega.setDestinatario(destinatario);

        // Atualizar status do compartimento
        compartimentoService.atualizarStatusCompartimento(compartimento.getId(), true);

        return entregaRepository.save(entrega);
    }

    @Transactional
    public Entrega registrarRetirada(Long id, String codigoAcesso) {
        Entrega entrega = entregaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrega", "id", id));

        if (entrega.getStatus() != StatusEntrega.ENTREGUE) {
            throw new BadRequestException("Esta entrega não está disponível para retirada");
        }

        Compartimento compartimento = entrega.getCompartimento();
        if (!compartimento.getCodigoAcesso().equals(codigoAcesso)) {
            throw new BadRequestException("Código de acesso inválido");
        }

        entrega.setDataRetirada(LocalDateTime.now());
        entrega.setStatus(StatusEntrega.RETIRADO);

        // Atualizar status do compartimento
        compartimentoService.atualizarStatusCompartimento(compartimento.getId(), false);
        
        // Gerar novo código de acesso para o compartimento
        compartimentoService.gerarNovoCodigoAcesso(compartimento.getId());

        return entregaRepository.save(entrega);
    }

    @Transactional
    public Entrega cancelarEntrega(Long id) {
        Entrega entrega = entregaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrega", "id", id));

        if (entrega.getStatus() != StatusEntrega.ENTREGUE && entrega.getStatus() != StatusEntrega.AGUARDANDO_ENTREGA) {
            throw new BadRequestException("Esta entrega não pode ser cancelada");
        }

        entrega.setStatus(StatusEntrega.CANCELADO);

        // Liberar o compartimento se a entrega já estava no compartimento
        if (entrega.getStatus() == StatusEntrega.ENTREGUE) {
            compartimentoService.atualizarStatusCompartimento(entrega.getCompartimento().getId(), false);
        }

        return entregaRepository.save(entrega);
    }

    @Transactional
    public List<Entrega> listarEntregas() {
        return entregaRepository.findAll();
    }

    @Transactional
    public List<Entrega> listarEntregasPorCompartimento(Long compartimentoId) {
        if (!compartimentoRepository.existsById(compartimentoId)) {
            throw new ResourceNotFoundException("Compartimento", "id", compartimentoId);
        }
        return entregaRepository.findByCompartimentoId(compartimentoId);
    }

    @Transactional
    public List<Entrega> listarEntregasPorEntregador(Long entregadorId) {
        if (!usuarioRepository.existsById(entregadorId)) {
            throw new ResourceNotFoundException("Entregador", "id", entregadorId);
        }
        return entregaRepository.findByEntregadorId(entregadorId);
    }

    @Transactional
    public List<Entrega> listarEntregasPorDestinatario(Long destinatarioId) {
        if (!usuarioRepository.existsById(destinatarioId)) {
            throw new ResourceNotFoundException("Destinatário", "id", destinatarioId);
        }
        return entregaRepository.findByDestinatarioId(destinatarioId);
    }

    @Transactional
    public List<Entrega> listarEntregasPorStatus(StatusEntrega status) {
        return entregaRepository.findByStatus(status);
    }

    @Transactional
    public Optional<Entrega> buscarEntregaPorId(Long id) {
        return entregaRepository.findById(id);
    }

    @Transactional
    public Optional<Entrega> buscarEntregaPorCodigoRastreio(String codigoRastreio) {
        return entregaRepository.findByCodigoRastreio(codigoRastreio);
    }

    @Transactional
    public List<Entrega> listarEntregasPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return entregaRepository.findByDataEntregaBetween(inicio, fim);
    }
}

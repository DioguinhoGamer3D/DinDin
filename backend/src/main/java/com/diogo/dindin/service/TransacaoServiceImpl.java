package com.diogo.dindin.service;

import com.diogo.dindin.dto.TransacaoRequest;
import com.diogo.dindin.dto.TransacaoResponse;
import com.diogo.dindin.exception.RecursoNaoEncontradoException;
import com.diogo.dindin.exception.TipoIncompativelException;
import com.diogo.dindin.model.Categoria;
import com.diogo.dindin.model.Transacao;
import com.diogo.dindin.repository.CategoriaRepository;
import com.diogo.dindin.repository.TransacaoRepository;
import com.diogo.dindin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransacaoServiceImpl implements TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TransacaoResponse> listar(UUID userId, YearMonth mes) {
        return transacaoRepository
                .findByUserIdAndDataBetween(userId, mes.atDay(1), mes.atEndOfMonth())
                .stream()
                .sorted(Comparator.comparing(Transacao::getData).reversed())
                .map(this::paraResponse)
                .toList();
    }

    @Override
    @Transactional
    public TransacaoResponse criar(UUID userId, TransacaoRequest request) {
        Categoria categoria = buscarCategoriaDoUsuario(userId, request.categoriaId());
        validarTipo(request, categoria);

        Transacao transacao = Transacao.builder()
                .descricao(request.descricao())
                .valor(request.valor())
                .data(request.data())
                .tipo(request.tipo())
                .categoria(categoria)
                .user(userRepository.getReferenceById(userId))
                .build();

        return paraResponse(transacaoRepository.save(transacao));
    }

    @Override
    @Transactional
    public TransacaoResponse atualizar(UUID userId, UUID id, TransacaoRequest request) {
        Transacao transacao = buscarDoUsuario(userId, id);
        Categoria categoria = buscarCategoriaDoUsuario(userId, request.categoriaId());
        validarTipo(request, categoria);

        transacao.setDescricao(request.descricao());
        transacao.setValor(request.valor());
        transacao.setData(request.data());
        transacao.setTipo(request.tipo());
        transacao.setCategoria(categoria);

        return paraResponse(transacaoRepository.save(transacao));
    }

    @Override
    @Transactional
    public void deletar(UUID userId, UUID id) {
        transacaoRepository.delete(buscarDoUsuario(userId, id));
    }

    private Transacao buscarDoUsuario(UUID userId, UUID id) {
        return transacaoRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Transação"));
    }

    private Categoria buscarCategoriaDoUsuario(UUID userId, UUID categoriaId) {
        return categoriaRepository.findByIdAndUserId(categoriaId, userId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria"));
    }

    private void validarTipo(TransacaoRequest request, Categoria categoria) {
        if (request.tipo() != categoria.getTipo()) {
            throw new TipoIncompativelException();
        }
    }

    private TransacaoResponse paraResponse(Transacao t) {
        return new TransacaoResponse(
                t.getId(),
                t.getDescricao(),
                t.getValor(),
                t.getData(),
                t.getTipo(),
                t.getCategoria().getId(),
                t.getCategoria().getNome()
        );
    }
}
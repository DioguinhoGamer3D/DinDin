package com.diogo.dindin.service;

import com.diogo.dindin.dto.CategoriaRequest;
import com.diogo.dindin.dto.CategoriaResponse;
import com.diogo.dindin.exception.CategoriaEmUsoException;
import com.diogo.dindin.exception.RecursoNaoEncontradoException;
import com.diogo.dindin.model.Categoria;
import com.diogo.dindin.repository.CategoriaRepository;
import com.diogo.dindin.repository.TransacaoRepository;
import com.diogo.dindin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final TransacaoRepository transacaoRepository;

    private final CategoriaRepository categoriaRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar(UUID userId) {
        return categoriaRepository.findByUserId(userId).stream()
                .map(this::paraResponse)
                .toList();
    }

    @Override
    @Transactional
    public CategoriaResponse criar(UUID userId, CategoriaRequest request) {
        Categoria categoria = Categoria.builder()
                .nome(request.nome())
                .tipo(request.tipo())
                .user(userRepository.getReferenceById(userId))
                .cor(request.cor())
                .build();

        return paraResponse(categoriaRepository.save(categoria));
    }

    @Override
    @Transactional
    public CategoriaResponse atualizar(UUID userId, UUID id, CategoriaRequest request) {
        Categoria categoria = buscarDoUsuario(userId, id);

        boolean mudouOTipo = categoria.getTipo() != request.tipo();
        if (mudouOTipo && transacaoRepository.existsByCategoriaId(id)) {
            throw new CategoriaEmUsoException(
                    "Não é possível alterar o tipo de uma categoria que possui transações vinculadas");
        }

        categoria.setNome(request.nome());
        categoria.setTipo(request.tipo());
        categoria.setCor(request.cor());

        return paraResponse(categoriaRepository.save(categoria));
    }

    @Override
    @Transactional
    public void deletar(UUID userId, UUID id) {
        Categoria categoria = buscarDoUsuario(userId, id);

        if (transacaoRepository.existsByCategoriaId(id)) {
            throw new CategoriaEmUsoException();
        }

        categoriaRepository.delete(categoria);
    }

    private Categoria buscarDoUsuario(UUID userId, UUID id) {
        return categoriaRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria"));
    }

    private CategoriaResponse paraResponse(Categoria categoria) {
        return new CategoriaResponse(categoria.getId(), categoria.getNome(), categoria.getTipo(), categoria.getCor());
    }
}
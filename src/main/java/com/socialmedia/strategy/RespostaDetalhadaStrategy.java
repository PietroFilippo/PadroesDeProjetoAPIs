package com.socialmedia.strategy;

import com.socialmedia.model.Publicacao;
import com.socialmedia.model.StatusPublicacao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Strategy que processa respostas com detalhes completos
public class RespostaDetalhadaStrategy implements RespostaStrategy {
    
    @Override
    @SuppressWarnings("unused")
    public RespostaUnificada processar(List<Publicacao> publicacoes) {
        if (publicacoes == null || publicacoes.isEmpty()) {
            return criarRespostaVazia();
        }

        int totalPublicacoes = publicacoes.size();
        int sucesso = 0;
        int falhas = 0;
        int agendadas = 0;
        List<String> erros = new ArrayList<>();
        Map<String, List<String>> publicacoesPorPlataforma = new HashMap<>();

        for (Publicacao pub : publicacoes) {
            StatusPublicacao status = pub.getStatus();
            
            // Contabiliza status
            switch (status) {
                case PUBLICADO:
                    sucesso++;
                    break;
                case AGENDADO:
                    agendadas++;
                    break;
                case FALHOU:
                    falhas++;
                    if (pub.getMensagemErro() != null) {
                        erros.add(String.format("[%s] %s", pub.getPlataforma(), pub.getMensagemErro()));
                    }
                    break;
                case PENDENTE:
                case CANCELADO:
                    // Não contabiliza em nenhuma categoria específica
                    break;
            }
            
            // Agrupa por plataforma
            String plataforma = pub.getPlataforma();
            publicacoesPorPlataforma
                .computeIfAbsent(plataforma, key -> new ArrayList<>())
                .add(String.format("%s (%s)", pub.getId(), status));
        }

        return new RespostaUnificada.Builder()
                .totalPublicacoes(totalPublicacoes)
                .sucesso(sucesso)
                .falhas(falhas)
                .agendadas(agendadas)
                .publicacoesPorPlataforma(publicacoesPorPlataforma)
                .erros(erros)
                .build();
    }

    private RespostaUnificada criarRespostaVazia() {
        return new RespostaUnificada.Builder()
                .totalPublicacoes(0)
                .build();
    }
}


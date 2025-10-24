package com.socialmedia.strategy;

import com.socialmedia.model.Publicacao;
import com.socialmedia.model.StatusPublicacao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Strategy que processa respostas de forma sumarizada (apenas contadores)
public class RespostaSumarizadaStrategy implements RespostaStrategy {
    
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
                    break;
                case PENDENTE:
                case CANCELADO:
                    // Não contabiliza em nenhuma categoria específica
                    break;
            }
            
            // Apenas conta por plataforma (sem detalhes)
            String plataforma = pub.getPlataforma();
            publicacoesPorPlataforma
                .computeIfAbsent(plataforma, key -> new ArrayList<>())
                .add(pub.getId());
        }

        return new RespostaUnificada.Builder()
                .totalPublicacoes(totalPublicacoes)
                .sucesso(sucesso)
                .falhas(falhas)
                .agendadas(agendadas)
                .publicacoesPorPlataforma(publicacoesPorPlataforma)
                .erros(List.of()) // Não inclui erros na versão sumarizada
                .build();
    }

    private RespostaUnificada criarRespostaVazia() {
        return new RespostaUnificada.Builder()
                .totalPublicacoes(0)
                .build();
    }
}


package com.socialmedia.strategy;

import com.socialmedia.model.Publicacao;

import java.util.List;


// Interface Strategy para processar respostas de publicações
public interface RespostaStrategy {
    

    // Processa a resposta de uma ou mais publicações
    RespostaUnificada processar(List<Publicacao> publicacoes);
}


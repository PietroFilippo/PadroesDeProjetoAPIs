package com.socialmedia.strategy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Resposta unificada do processamento de publicações
public class RespostaUnificada {
    private final int totalPublicacoes;
    private final int sucesso;
    private final int falhas;
    private final int agendadas;
    private final Map<String, List<String>> publicacoesPorPlataforma;
    private final List<String> erros;
    private final LocalDateTime dataProcessamento;

    private RespostaUnificada(Builder builder) {
        this.totalPublicacoes = builder.totalPublicacoes;
        this.sucesso = builder.sucesso;
        this.falhas = builder.falhas;
        this.agendadas = builder.agendadas;
        this.publicacoesPorPlataforma = Map.copyOf(builder.publicacoesPorPlataforma);
        this.erros = List.copyOf(builder.erros);
        this.dataProcessamento = builder.dataProcessamento;
    }

    public int getTotalPublicacoes() {
        return totalPublicacoes;
    }

    public int getSucesso() {
        return sucesso;
    }

    public int getFalhas() {
        return falhas;
    }

    public int getAgendadas() {
        return agendadas;
    }

    public Map<String, List<String>> getPublicacoesPorPlataforma() {
        return publicacoesPorPlataforma;
    }

    public List<String> getErros() {
        return erros;
    }

    public LocalDateTime getDataProcessamento() {
        return dataProcessamento;
    }

    public boolean isTodasSucesso() {
        return falhas == 0 && sucesso == totalPublicacoes;
    }

    public double getTaxaSucesso() {
        if (totalPublicacoes == 0) return 0.0;
        return (sucesso * 100.0) / totalPublicacoes;
    }

    @Override
    public String toString() {
        return String.format(
            "RespostaUnificada{total=%d, sucesso=%d (%.1f%%), falhas=%d, agendadas=%d, plataformas=%d}",
            totalPublicacoes, sucesso, getTaxaSucesso(), falhas, agendadas, 
            publicacoesPorPlataforma.size()
        );
    }

    public static class Builder {
        private int totalPublicacoes = 0;
        private int sucesso = 0;
        private int falhas = 0;
        private int agendadas = 0;
        private Map<String, List<String>> publicacoesPorPlataforma = new HashMap<>();
        private List<String> erros = new ArrayList<>();
        private LocalDateTime dataProcessamento = LocalDateTime.now();

        public Builder totalPublicacoes(int total) {
            this.totalPublicacoes = total;
            return this;
        }

        public Builder sucesso(int sucesso) {
            this.sucesso = sucesso;
            return this;
        }

        public Builder falhas(int falhas) {
            this.falhas = falhas;
            return this;
        }

        public Builder agendadas(int agendadas) {
            this.agendadas = agendadas;
            return this;
        }

        public Builder publicacoesPorPlataforma(Map<String, List<String>> publicacoes) {
            this.publicacoesPorPlataforma = new HashMap<>(publicacoes);
            return this;
        }

        public Builder erros(List<String> erros) {
            this.erros = new ArrayList<>(erros);
            return this;
        }

        public Builder dataProcessamento(LocalDateTime data) {
            this.dataProcessamento = data;
            return this;
        }

        public RespostaUnificada build() {
            return new RespostaUnificada(this);
        }
    }
}


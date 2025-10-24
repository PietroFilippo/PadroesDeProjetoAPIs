package com.socialmedia.model;

import java.time.LocalDateTime;
import java.util.Objects;

// Representa as estatísticas de uma publicação
public class Estatisticas {
    private final String publicacaoId;
    private final String plataforma;
    private final long visualizacoes;
    private final long curtidas;
    private final long compartilhamentos;
    private final long comentarios;
    private final double taxaEngajamento;
    private final LocalDateTime dataColeta;

    private Estatisticas(Builder builder) {
        this.publicacaoId = builder.publicacaoId;
        this.plataforma = builder.plataforma;
        this.visualizacoes = builder.visualizacoes;
        this.curtidas = builder.curtidas;
        this.compartilhamentos = builder.compartilhamentos;
        this.comentarios = builder.comentarios;
        this.taxaEngajamento = builder.taxaEngajamento;
        this.dataColeta = builder.dataColeta;
    }

    public String getPublicacaoId() {
        return publicacaoId;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public long getVisualizacoes() {
        return visualizacoes;
    }

    public long getCurtidas() {
        return curtidas;
    }

    public long getCompartilhamentos() {
        return compartilhamentos;
    }

    public long getComentarios() {
        return comentarios;
    }

    public double getTaxaEngajamento() {
        return taxaEngajamento;
    }

    public LocalDateTime getDataColeta() {
        return dataColeta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Estatisticas that = (Estatisticas) o;
        return Objects.equals(publicacaoId, that.publicacaoId) &&
               Objects.equals(plataforma, that.plataforma);
    }

    @Override
    public int hashCode() {
        return Objects.hash(publicacaoId, plataforma);
    }

    @Override
    public String toString() {
        return "Estatisticas{" +
                "plataforma='" + plataforma + '\'' +
                ", visualizacoes=" + visualizacoes +
                ", curtidas=" + curtidas +
                ", compartilhamentos=" + compartilhamentos +
                ", comentarios=" + comentarios +
                ", taxaEngajamento=" + String.format("%.2f%%", taxaEngajamento) +
                ", dataColeta=" + dataColeta +
                '}';
    }

    public static class Builder {
        private String publicacaoId;
        private String plataforma;
        private long visualizacoes = 0;
        private long curtidas = 0;
        private long compartilhamentos = 0;
        private long comentarios = 0;
        private double taxaEngajamento = 0.0;
        private LocalDateTime dataColeta = LocalDateTime.now();

        public Builder publicacaoId(String publicacaoId) {
            this.publicacaoId = publicacaoId;
            return this;
        }

        public Builder plataforma(String plataforma) {
            this.plataforma = plataforma;
            return this;
        }

        public Builder visualizacoes(long visualizacoes) {
            this.visualizacoes = visualizacoes;
            return this;
        }

        public Builder curtidas(long curtidas) {
            this.curtidas = curtidas;
            return this;
        }

        public Builder compartilhamentos(long compartilhamentos) {
            this.compartilhamentos = compartilhamentos;
            return this;
        }

        public Builder comentarios(long comentarios) {
            this.comentarios = comentarios;
            return this;
        }

        public Builder taxaEngajamento(double taxaEngajamento) {
            this.taxaEngajamento = taxaEngajamento;
            return this;
        }

        public Builder dataColeta(LocalDateTime dataColeta) {
            this.dataColeta = dataColeta;
            return this;
        }

        public Estatisticas build() {
            if (publicacaoId == null || publicacaoId.trim().isEmpty()) {
                throw new IllegalArgumentException("ID da publicação não pode ser vazio");
            }
            if (plataforma == null || plataforma.trim().isEmpty()) {
                throw new IllegalArgumentException("Plataforma não pode ser vazia");
            }
            return new Estatisticas(this);
        }
    }
}


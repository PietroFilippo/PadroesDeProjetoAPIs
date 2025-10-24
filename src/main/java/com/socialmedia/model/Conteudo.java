package com.socialmedia.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


// Representa o conteúdo a ser publicado nas redes sociais
public class Conteudo {
    private final String texto;
    private final List<String> midias;
    private final List<String> hashtags;
    private final LocalDateTime dataAgendamento;
    private final TipoConteudo tipo;

    private Conteudo(Builder builder) {
        this.texto = builder.texto;
        this.midias = builder.midias;
        this.hashtags = builder.hashtags;
        this.dataAgendamento = builder.dataAgendamento;
        this.tipo = builder.tipo;
    }

    public String getTexto() {
        return texto;
    }

    public List<String> getMidias() {
        return midias;
    }

    public List<String> getHashtags() {
        return hashtags;
    }

    public LocalDateTime getDataAgendamento() {
        return dataAgendamento;
    }

    public TipoConteudo getTipo() {
        return tipo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conteudo conteudo = (Conteudo) o;
        return Objects.equals(texto, conteudo.texto) &&
               Objects.equals(dataAgendamento, conteudo.dataAgendamento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(texto, dataAgendamento);
    }

    @Override
    public String toString() {
        return "Conteudo{" +
                "texto='" + texto + '\'' +
                ", tipo=" + tipo +
                ", hashtags=" + hashtags +
                ", midias=" + midias.size() + " arquivo(s)" +
                ", dataAgendamento=" + dataAgendamento +
                '}';
    }

    public static class Builder {
        private String texto;
        private List<String> midias = List.of();
        private List<String> hashtags = List.of();
        private LocalDateTime dataAgendamento;
        private TipoConteudo tipo = TipoConteudo.POST;

        public Builder texto(String texto) {
            this.texto = texto;
            return this;
        }

        public Builder midias(List<String> midias) {
            this.midias = midias != null ? List.copyOf(midias) : List.of();
            return this;
        }

        public Builder hashtags(List<String> hashtags) {
            this.hashtags = hashtags != null ? List.copyOf(hashtags) : List.of();
            return this;
        }

        public Builder dataAgendamento(LocalDateTime dataAgendamento) {
            this.dataAgendamento = dataAgendamento;
            return this;
        }

        public Builder tipo(TipoConteudo tipo) {
            this.tipo = tipo;
            return this;
        }

        public Conteudo build() {
            if (texto == null || texto.trim().isEmpty()) {
                throw new IllegalArgumentException("Texto do conteúdo não pode ser vazio");
            }
            return new Conteudo(this);
        }
    }
}


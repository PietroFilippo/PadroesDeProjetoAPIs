package com.socialmedia.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

// Representa uma publicação realizada em uma rede social
public class Publicacao {
    private final String id;
    private final String plataforma;
    private final Conteudo conteudo;
    private final LocalDateTime dataPublicacao;
    private final StatusPublicacao status;
    private final String urlPublicacao;
    private final String mensagemErro;

    private Publicacao(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.plataforma = builder.plataforma;
        this.conteudo = builder.conteudo;
        this.dataPublicacao = builder.dataPublicacao;
        this.status = builder.status;
        this.urlPublicacao = builder.urlPublicacao;
        this.mensagemErro = builder.mensagemErro;
    }

    public String getId() {
        return id;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public Conteudo getConteudo() {
        return conteudo;
    }

    public LocalDateTime getDataPublicacao() {
        return dataPublicacao;
    }

    public StatusPublicacao getStatus() {
        return status;
    }

    public String getUrlPublicacao() {
        return urlPublicacao;
    }

    public String getMensagemErro() {
        return mensagemErro;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Publicacao that = (Publicacao) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Publicacao{" +
                "id='" + id + '\'' +
                ", plataforma='" + plataforma + '\'' +
                ", status=" + status +
                ", dataPublicacao=" + dataPublicacao +
                (urlPublicacao != null ? ", url='" + urlPublicacao + '\'' : "") +
                (mensagemErro != null ? ", erro='" + mensagemErro + '\'' : "") +
                '}';
    }

    public static class Builder {
        private String id;
        private String plataforma;
        private Conteudo conteudo;
        private LocalDateTime dataPublicacao = LocalDateTime.now();
        private StatusPublicacao status = StatusPublicacao.PENDENTE;
        private String urlPublicacao;
        private String mensagemErro;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder plataforma(String plataforma) {
            this.plataforma = plataforma;
            return this;
        }

        public Builder conteudo(Conteudo conteudo) {
            this.conteudo = conteudo;
            return this;
        }

        public Builder dataPublicacao(LocalDateTime dataPublicacao) {
            this.dataPublicacao = dataPublicacao;
            return this;
        }

        public Builder status(StatusPublicacao status) {
            this.status = status;
            return this;
        }

        public Builder urlPublicacao(String urlPublicacao) {
            this.urlPublicacao = urlPublicacao;
            return this;
        }

        public Builder mensagemErro(String mensagemErro) {
            this.mensagemErro = mensagemErro;
            return this;
        }

        public Publicacao build() {
            if (plataforma == null || plataforma.trim().isEmpty()) {
                throw new IllegalArgumentException("Plataforma não pode ser vazia");
            }
            if (conteudo == null) {
                throw new IllegalArgumentException("Conteúdo não pode ser nulo");
            }
            return new Publicacao(this);
        }
    }
}


package com.socialmedia.config;

import java.util.Objects;

// Configuração de credenciais para uma plataforma
public class ConfiguracaoPlataforma {
    private final String plataforma;
    private final String credenciais;
    private final boolean ativa;

    private ConfiguracaoPlataforma(Builder builder) {
        this.plataforma = builder.plataforma;
        this.credenciais = builder.credenciais;
        this.ativa = builder.ativa;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public String getCredenciais() {
        return credenciais;
    }

    public boolean isAtiva() {
        return ativa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfiguracaoPlataforma that = (ConfiguracaoPlataforma) o;
        return Objects.equals(plataforma, that.plataforma);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plataforma);
    }

    @Override
    public String toString() {
        return "ConfiguracaoPlataforma{" +
                "plataforma='" + plataforma + '\'' +
                ", ativa=" + ativa +
                '}';
    }

    public static class Builder {
        private String plataforma;
        private String credenciais;
        private boolean ativa = true;

        public Builder plataforma(String plataforma) {
            this.plataforma = plataforma;
            return this;
        }

        public Builder credenciais(String credenciais) {
            this.credenciais = credenciais;
            return this;
        }

        public Builder ativa(boolean ativa) {
            this.ativa = ativa;
            return this;
        }

        public ConfiguracaoPlataforma build() {
            if (plataforma == null || plataforma.trim().isEmpty()) {
                throw new IllegalArgumentException("Plataforma não pode ser vazia");
            }
            if (credenciais == null || credenciais.trim().isEmpty()) {
                throw new IllegalArgumentException("Credenciais não podem ser vazias");
            }
            return new ConfiguracaoPlataforma(this);
        }
    }
}


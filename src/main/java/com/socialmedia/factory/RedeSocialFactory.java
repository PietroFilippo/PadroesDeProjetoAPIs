package com.socialmedia.factory;

import com.socialmedia.adapter.*;
import com.socialmedia.service.RedeSocialService;
import com.socialmedia.exception.AutenticacaoException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


// Factory para criar instâncias de RedeSocialService
// Implementa o padrão Factory Method com registro dinâmico
public class RedeSocialFactory {
    private static final Map<String, Supplier<RedeSocialService>> registry = new HashMap<>();
    
    static {
        // Registro das plataformas suportadas
        registrar("TWITTER", TwitterAdapter::new);
        registrar("INSTAGRAM", InstagramAdapter::new);
        registrar("LINKEDIN", LinkedInAdapter::new);
        registrar("TIKTOK", TikTokAdapter::new);
    }


    // Cria uma instância de RedeSocialService para a plataforma especificada
    public static RedeSocialService criar(String plataforma) {
        if (plataforma == null || plataforma.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da plataforma não pode ser vazio");
        }
        
        String plataformaNormalizada = plataforma.toUpperCase().trim();
        
        Supplier<RedeSocialService> supplier = registry.get(plataformaNormalizada);
        if (supplier == null) {
            throw new IllegalArgumentException(
                "Plataforma não suportada: " + plataforma + 
                ". Plataformas disponíveis: " + String.join(", ", registry.keySet())
            );
        }
        
        return supplier.get();
    }

    // Cria e autentica uma instância de RedeSocialService
    public static RedeSocialService criarAutenticado(String plataforma, String credenciais) 
            throws AutenticacaoException {
        RedeSocialService service = criar(plataforma);
        service.autenticar(credenciais);
        return service;
    }

    // Registra uma nova plataforma na factory
    // Permite extensibilidade para adicionar novas plataformas dinamicamente
    public static void registrar(String nome, Supplier<RedeSocialService> supplier) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da plataforma não pode ser vazio");
        }
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier não pode ser nulo");
        }
        
        registry.put(nome.toUpperCase().trim(), supplier);
    }

    // Remove uma plataforma do registro
    public static boolean removerRegistro(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }
        
        return registry.remove(nome.toUpperCase().trim()) != null;
    }

    // Verifica se uma plataforma está registrada
    public static boolean isPlataformaSuportada(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }
        
        return registry.containsKey(nome.toUpperCase().trim());
    }

    // Retorna todas as plataformas suportadas
    public static String[] getPlataformasSuportadas() {
        return registry.keySet().toArray(new String[0]);
    }
}


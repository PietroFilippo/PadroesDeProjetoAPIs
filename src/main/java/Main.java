import com.socialmedia.config.ConfiguracaoPlataforma;
import com.socialmedia.exception.AutenticacaoException;
import com.socialmedia.model.*;
import com.socialmedia.service.GerenciadorMidiaSocial;
import com.socialmedia.strategy.RespostaDetalhadaStrategy;
import com.socialmedia.strategy.RespostaSumarizadaStrategy;
import com.socialmedia.strategy.RespostaUnificada;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

// Classe principal demonstrando o uso do Sistema de Integração de APIs de Mídia Social
public class Main {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("SISTEMA DE INTEGRAÇÃO DE APIS DE MÍDIA SOCIAL");
        System.out.println("Demonstração do Padrão Adapter + Factory + Strategy");
        System.out.println("=".repeat(80));
        System.out.println();

        try {
            // Caso de Uso 1: Configuração e Autenticação
            demonstrarConfiguracaoInicial();
            
            // Caso de Uso 2: Publicação Simples em Múltiplas Plataformas
            demonstrarPublicacaoMultiplasPlataformas();
            
            // Caso de Uso 3: Agendamento de Conteúdo
            demonstrarAgendamento();
            
            // Caso de Uso 4: Publicação com Tipos Específicos
            demonstrarTiposEspecificos();
            
            // Caso de Uso 5: Obtenção de Estatísticas
            demonstrarEstatisticas();
            
            // Caso de Uso 6: Alternância de Strategy
            demonstrarAlternanciaStrategy();
            
            System.out.println("\n" + "=".repeat(80));
            System.out.println("DEMONSTRAÇÃO CONCLUÍDA COM SUCESSO");
            System.out.println("=".repeat(80));
            
        } catch (Exception e) {
            System.err.println("ERRO NA DEMONSTRAÇÃO: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void demonstrarConfiguracaoInicial() throws AutenticacaoException {
        imprimirSecao("CASO DE USO 1: Configuração e Autenticação");
        
        GerenciadorMidiaSocial gerenciador = new GerenciadorMidiaSocial(
            new RespostaDetalhadaStrategy()
        );

        // Configurando Twitter
        ConfiguracaoPlataforma configTwitter = new ConfiguracaoPlataforma.Builder()
            .plataforma("TWITTER")
            .credenciais("twitter-api-key:twitter-api-secret")
            .ativa(true)
            .build();
        gerenciador.adicionarPlataforma(configTwitter);
        System.out.println("Twitter configurado e autenticado");

        // Configurando Instagram
        ConfiguracaoPlataforma configInstagram = new ConfiguracaoPlataforma.Builder()
            .plataforma("INSTAGRAM")
            .credenciais("instagram-access-token-xyz123")
            .ativa(true)
            .build();
        gerenciador.adicionarPlataforma(configInstagram);
        System.out.println("Instagram configurado e autenticado");

        // Configurando LinkedIn
        ConfiguracaoPlataforma configLinkedIn = new ConfiguracaoPlataforma.Builder()
            .plataforma("LINKEDIN")
            .credenciais("linkedin-client-id:linkedin-client-secret:https://callback")
            .ativa(true)
            .build();
        gerenciador.adicionarPlataforma(configLinkedIn);
        System.out.println("LinkedIn configurado e autenticado");

        // Configurando TikTok
        ConfiguracaoPlataforma configTikTok = new ConfiguracaoPlataforma.Builder()
            .plataforma("TIKTOK")
            .credenciais("tiktok-app-id:tiktok-app-secret:auth-code-123")
            .ativa(true)
            .build();
        gerenciador.adicionarPlataforma(configTikTok);
        System.out.println("TikTok configurado e autenticado");

        System.out.println("\nPlataformas ativas: " + gerenciador.getPlataformasAtivas());
    }

    private static void demonstrarPublicacaoMultiplasPlataformas() throws AutenticacaoException {
        imprimirSecao("CASO DE USO 2: Publicação em Múltiplas Plataformas");
        
        GerenciadorMidiaSocial gerenciador = criarGerenciadorCompleto();

        // Criando conteúdo
        Conteudo conteudo = new Conteudo.Builder()
            .texto("Lançamento da nova campanha de marketing digital!")
            .hashtags(Arrays.asList("Marketing", "Digital", "Inovacao"))
            .midias(Arrays.asList("https://cdn.example.com/imagem1.jpg"))
            .tipo(TipoConteudo.POST)
            .build();

        System.out.println("Conteúdo criado: " + conteudo.getTexto());
        System.out.println("Publicando em: Twitter, Instagram e LinkedIn...\n");

        // Publicando em plataformas específicas
        RespostaUnificada resposta = gerenciador.publicarEmMultiplasPlataformas(
            conteudo,
            Arrays.asList("TWITTER", "INSTAGRAM", "LINKEDIN")
        );

        exibirResposta(resposta);
    }

    private static void demonstrarAgendamento() throws AutenticacaoException {
        imprimirSecao("CASO DE USO 3: Agendamento de Conteúdo");
        
        GerenciadorMidiaSocial gerenciador = criarGerenciadorCompleto();

        // Conteúdo agendado para amanhã às 10h
        LocalDateTime dataAgendamento = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        
        Conteudo conteudo = new Conteudo.Builder()
            .texto("Bom dia! Conheça nossas novidades desta semana.")
            .hashtags(Arrays.asList("BomDia", "Novidades"))
            .midias(Arrays.asList("https://cdn.example.com/banner-semana.jpg"))
            .dataAgendamento(dataAgendamento)
            .tipo(TipoConteudo.POST)
            .build();

        System.out.println("Agendando para: " + dataAgendamento);
        System.out.println("Plataformas: Todas ativas\n");

        RespostaUnificada resposta = gerenciador.agendarEmMultiplasPlataformas(
            conteudo,
            gerenciador.getPlataformasAtivas()
        );

        exibirResposta(resposta);
    }

    private static void demonstrarTiposEspecificos() throws AutenticacaoException {
        imprimirSecao("CASO DE USO 4: Publicação com Tipos Específicos");
        
        GerenciadorMidiaSocial gerenciador = criarGerenciadorCompleto();

        // Story para Instagram
        System.out.println("1. Publicando STORY no Instagram:");
        Conteudo story = new Conteudo.Builder()
            .texto("Novidade! Confira nossos stories")
            .midias(Arrays.asList("https://cdn.example.com/story.jpg"))
            .tipo(TipoConteudo.STORY)
            .build();
        
        RespostaUnificada respostaStory = gerenciador.publicarEmMultiplasPlataformas(
            story,
            Arrays.asList("INSTAGRAM")
        );
        exibirResposta(respostaStory);

        // Vídeo para TikTok
        System.out.println("\n2. Publicando VÍDEO no TikTok:");
        Conteudo video = new Conteudo.Builder()
            .texto("Nosso novo tutorial está no ar!")
            .hashtags(Arrays.asList("Tutorial", "Dicas", "Aprenda"))
            .midias(Arrays.asList("https://cdn.example.com/video-tutorial.mp4"))
            .tipo(TipoConteudo.VIDEO)
            .build();
        
        RespostaUnificada respostaVideo = gerenciador.publicarEmMultiplasPlataformas(
            video,
            Arrays.asList("TIKTOK")
        );
        exibirResposta(respostaVideo);

        // Artigo para LinkedIn
        System.out.println("\n3. Publicando ARTIGO no LinkedIn:");
        Conteudo artigo = new Conteudo.Builder()
            .texto("As 10 Tendências de Marketing Digital para 2025\n\n" +
                   "Confira nossa análise completa sobre o futuro do marketing...")
            .hashtags(Arrays.asList("Marketing", "Tendencias", "2025"))
            .tipo(TipoConteudo.ARTIGO)
            .build();
        
        RespostaUnificada respostaArtigo = gerenciador.publicarEmMultiplasPlataformas(
            artigo,
            Arrays.asList("LINKEDIN")
        );
        exibirResposta(respostaArtigo);
    }

    private static void demonstrarEstatisticas() throws AutenticacaoException {
        imprimirSecao("CASO DE USO 5: Obtenção de Estatísticas");
        
        GerenciadorMidiaSocial gerenciador = criarGerenciadorCompleto();

        // Primeiro publica conteúdo
        Conteudo conteudo = new Conteudo.Builder()
            .texto("Engajamento é tudo! Curta e compartilhe.")
            .hashtags(Arrays.asList("Engajamento", "Conteudo"))
            .midias(Arrays.asList("https://cdn.example.com/engajamento.jpg"))
            .tipo(TipoConteudo.POST)
            .build();

        RespostaUnificada resposta = gerenciador.publicarEmTodasPlataformas(conteudo);
        
        System.out.println("Publicações realizadas. Obtendo estatísticas...\n");

        // Simula obtenção de estatísticas (IDs das publicações seriam retornados)
        if (!resposta.getPublicacoesPorPlataforma().isEmpty()) {
            for (var entry : resposta.getPublicacoesPorPlataforma().entrySet()) {
                String plataforma = entry.getKey();
                List<String> publicacoes = entry.getValue();
                
                if (!publicacoes.isEmpty()) {
                    String publicacaoInfo = publicacoes.get(0);
                    // Extrai apenas o ID (antes do espaço)
                    String publicacaoId = publicacaoInfo.split(" ")[0];
                    
                    try {
                        Estatisticas stats = gerenciador.obterEstatisticas(plataforma, publicacaoId);
                        System.out.println(stats);
                    } catch (Exception e) {
                        System.out.println("Erro ao obter estatísticas de " + plataforma + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    private static void demonstrarAlternanciaStrategy() throws AutenticacaoException {
        imprimirSecao("CASO DE USO 6: Alternância de Strategy de Resposta");
        
        GerenciadorMidiaSocial gerenciador = criarGerenciadorCompleto();

        Conteudo conteudo = new Conteudo.Builder()
            .texto("Teste de strategies diferentes")
            .hashtags(Arrays.asList("Teste"))
            .midias(Arrays.asList("https://cdn.example.com/test.jpg"))
            .tipo(TipoConteudo.POST)
            .build();

        // Strategy Detalhada
        System.out.println("1. Usando RespostaDetalhadaStrategy:");
        gerenciador.setRespostaStrategy(new RespostaDetalhadaStrategy());
        RespostaUnificada respostaDetalhada = gerenciador.publicarEmTodasPlataformas(conteudo);
        exibirResposta(respostaDetalhada);

        // Strategy Sumarizada
        System.out.println("\n2. Usando RespostaSumarizadaStrategy:");
        gerenciador.setRespostaStrategy(new RespostaSumarizadaStrategy());
        
        Conteudo conteudo2 = new Conteudo.Builder()
            .texto("Segundo teste com strategy sumarizada")
            .hashtags(Arrays.asList("Teste2"))
            .midias(Arrays.asList("https://cdn.example.com/test2.jpg"))
            .tipo(TipoConteudo.POST)
            .build();
        
        RespostaUnificada respostaSumarizada = gerenciador.publicarEmTodasPlataformas(conteudo2);
        exibirResposta(respostaSumarizada);
    }

    // Métodos auxiliares

    private static GerenciadorMidiaSocial criarGerenciadorCompleto() throws AutenticacaoException {
        GerenciadorMidiaSocial gerenciador = new GerenciadorMidiaSocial(
            new RespostaDetalhadaStrategy()
        );

        gerenciador.adicionarPlataforma(new ConfiguracaoPlataforma.Builder()
            .plataforma("TWITTER")
            .credenciais("twitter-key:twitter-secret")
            .build());

        gerenciador.adicionarPlataforma(new ConfiguracaoPlataforma.Builder()
            .plataforma("INSTAGRAM")
            .credenciais("instagram-token")
            .build());

        gerenciador.adicionarPlataforma(new ConfiguracaoPlataforma.Builder()
            .plataforma("LINKEDIN")
            .credenciais("linkedin-id:linkedin-secret:callback")
            .build());

        gerenciador.adicionarPlataforma(new ConfiguracaoPlataforma.Builder()
            .plataforma("TIKTOK")
            .credenciais("tiktok-id:tiktok-secret:code")
            .build());

        return gerenciador;
    }

    private static void imprimirSecao(String titulo) {
        System.out.println("\n" + "-".repeat(80));
        System.out.println(titulo);
        System.out.println("-".repeat(80));
    }

    private static void exibirResposta(RespostaUnificada resposta) {
        System.out.println("\nRESULTADO:");
        System.out.println("   Total de publicações: " + resposta.getTotalPublicacoes());
        System.out.println("   V Sucesso: " + resposta.getSucesso());
        System.out.println("   X Falhas: " + resposta.getFalhas());
        System.out.println("   Agendadas: " + resposta.getAgendadas());
        System.out.println("   Taxa de sucesso: " + String.format("%.1f%%", resposta.getTaxaSucesso()));
        
        if (!resposta.getErros().isEmpty()) {
            System.out.println("\nERROS:");
            resposta.getErros().forEach(erro -> System.out.println("   - " + erro));
        }
        
        if (!resposta.getPublicacoesPorPlataforma().isEmpty()) {
            System.out.println("\n TEMPO POR PLATAFORMA:");
            resposta.getPublicacoesPorPlataforma().forEach((plataforma, publicacoes) -> {
                System.out.println("   " + plataforma + ": " + publicacoes.size() + " publicação(ões)");
                if (resposta.getErros().isEmpty() || resposta instanceof RespostaUnificada) {
                    publicacoes.forEach(pub -> System.out.println("      - " + pub));
                }
            });
        }
    }
}


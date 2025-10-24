# Sistema de Integração de APIs de Mídia Social

Sistema unificado de gerenciamento de múltiplas redes sociais desenvolvido com o padrão Adapter, Factory Method e Strategy, para integração transparente com diferentes APIs de mídia social (Twitter, Instagram, LinkedIn, TikTok).

## Projeto

O sistema oferece:

- **Interface Unificada**: Uma única API para gerenciar todas as plataformas
- **Flexibilidade**: Adicione ou remova plataformas dinamicamente
- **Extensibilidade**: Fácil adição de novas redes sociais
- **Tratamento de Erros Robusto**: Sistema de resposta unificado com diferentes estratégias
- **Simulação Realista**: APIs simuladas que replicam o comportamento das APIs reais

## Arquitetura

O sistema segue uma arquitetura em camadas com separação clara de responsabilidades:

```
┌─────────────────────────────────────────────┐
│         Camada de Apresentação              │
│              (Main, CLI)                    │
└─────────────────┬───────────────────────────┘
                  │
┌─────────────────▼───────────────────────────┐
│      Camada de Serviço (Facade)             │
│       GerenciadorMidiaSocial                │
└───┬──────────────┬──────────────┬───────────┘
    │              │              │
┌───▼────┐   ┌────▼────┐   ┌────▼────┐
│Factory │   │Strategy │   │Adapter  │
│        │   │         │   │ Layer   │
└────────┘   └─────────┘   └────┬────┘
                                 │
                      ┌──────────┼──────────┐
                      │          │          │
                 ┌────▼───┐ ┌───▼─────┐ ┌──▼─────┐
                 │Twitter │ │Instagram│ │LinkedIn│
                 │  API   │ │   API   │ │  API   │
                 └────────┘ └─────────┘ └────────┘
```

## Padrões de Projeto

### 1. Adapter Pattern

**Problema**: Cada rede social possui sua própria API com interfaces e estruturas de dados diferentes.

**Solução**: Adaptadores convertem as interfaces específicas de cada plataforma para uma interface unificada `RedeSocialService`.

**Implementação**:
- `RedeSocialService`: Interface alvo unificada
- `TwitterAdapter`, `InstagramAdapter`, `LinkedInAdapter`, `TikTokAdapter`: Adaptadores concretos
- `TwitterAPI`, `InstagramAPI`, etc.: APIs adaptadas (simuladas)

**Benefícios**:
- Isolamento da lógica de negócio das especificidades das APIs
- Fácil substituição ou atualização de APIs
- Código cliente independente das implementações

### 2. Factory Method Pattern

**Problema**: Criação e configuração de diferentes adapters de forma flexível.

**Solução**: Factory centralizada com registro dinâmico de plataformas.

**Implementação**:
- `RedeSocialFactory`: Factory com registro de suppliers
- Método `criar()`: Cria instâncias por nome de plataforma
- Método `criarAutenticado()`: Cria e autentica automaticamente
- Registro dinâmico: Permite adicionar novas plataformas em runtime

**Benefícios**:
- Centralização da lógica de criação
- Extensibilidade sem modificar código existente (Open/Closed Principle)
- Configuração flexível

### 3. Strategy Pattern

**Problema**: Diferentes formas de processar e apresentar respostas de publicações.

**Solução**: Estratégias intercambiáveis para processamento de respostas.

**Implementação**:
- `RespostaStrategy`: Interface strategy
- `RespostaDetalhadaStrategy`: Inclui erros e detalhes completos
- `RespostaSumarizadaStrategy`: Apenas contadores e resumo

**Benefícios**:
- Flexibilidade no formato de resposta
- Facilita testes e diferentes contextos de uso
- Fácil adição de novas estratégias

### 4. Builder Pattern

**Uso**: Construção de objetos complexos como `Conteudo`, `Publicacao`, `Estatisticas`.

**Benefícios**:
- Imutabilidade dos objetos
- API fluente e legível
- Validação centralizada

## Estrutura do Projeto

```
social-media-integration/
├── src/main/java/
│   ├── Main.java                                  # Classe principal
│   └── com/socialmedia/
│       ├── adapter/                               # Adapters
│       │   ├── TwitterAdapter.java
│       │   ├── InstagramAdapter.java
│       │   ├── LinkedInAdapter.java
│       │   └── TikTokAdapter.java
│       ├── api/                                   # APIs Simuladas
│       │   ├── twitter/TwitterAPI.java
│       │   ├── instagram/InstagramAPI.java
│       │   ├── linkedin/LinkedInAPI.java
│       │   └── tiktok/TikTokAPI.java
│       ├── config/                                # Configurações
│       │   └── ConfiguracaoPlataforma.java
│       ├── exception/                             # Exceções
│       │   ├── PublicacaoException.java
│       │   └── AutenticacaoException.java
│       ├── factory/                               # Factory
│       │   └── RedeSocialFactory.java
│       ├── model/                                 # Modelos de Domínio
│       │   ├── Conteudo.java
│       │   ├── Publicacao.java
│       │   ├── Estatisticas.java
│       │   ├── TipoConteudo.java
│       │   └── StatusPublicacao.java
│       ├── service/                               # Serviços
│       │   ├── RedeSocialService.java             # Interface unificada
│       │   └── GerenciadorMidiaSocial.java        # Facade principal
│       └── strategy/                              # Strategies
│           ├── RespostaStrategy.java
│           ├── RespostaUnificada.java
│           ├── RespostaDetalhadaStrategy.java
│           └── RespostaSumarizadaStrategy.java
├── docs/
│   ├── diagramas/
│   │   ├── diagrama-classes.png
│   │   └── diagrama-sequencia.png
│   └── ARCHITECTURE.md
├── pom.xml                                        # Maven POM
└── README.md
```

## Uso

### Exemplo Básico

```java
// 1. Criar gerenciador
GerenciadorMidiaSocial gerenciador = new GerenciadorMidiaSocial(
    new RespostaDetalhadaStrategy()
);

// 2. Configurar plataformas
ConfiguracaoPlataforma configTwitter = new ConfiguracaoPlataforma.Builder()
    .plataforma("TWITTER")
    .credenciais("api-key:api-secret")
    .ativa(true)
    .build();

gerenciador.adicionarPlataforma(configTwitter);

// 3. Criar conteúdo
Conteudo conteudo = new Conteudo.Builder()
    .texto("Minha primeira publicação unificada!")
    .hashtags(Arrays.asList("Marketing", "Digital"))
    .midias(Arrays.asList("https://exemplo.com/imagem.jpg"))
    .tipo(TipoConteudo.POST)
    .build();

// 4. Publicar
RespostaUnificada resposta = gerenciador.publicarEmTodasPlataformas(conteudo);

// 5. Verificar resultado
System.out.println("Sucesso: " + resposta.getSucesso());
System.out.println("Taxa de sucesso: " + resposta.getTaxaSucesso() + "%");
```

### Publicação em Plataformas Específicas

```java
// Publicar apenas no Twitter e Instagram
RespostaUnificada resposta = gerenciador.publicarEmMultiplasPlataformas(
    conteudo,
    Arrays.asList("TWITTER", "INSTAGRAM")
);
```

### Agendamento

```java
LocalDateTime dataAgendamento = LocalDateTime.now().plusDays(1);

Conteudo conteudoAgendado = new Conteudo.Builder()
    .texto("Post agendado para amanhã")
    .dataAgendamento(dataAgendamento)
    .tipo(TipoConteudo.POST)
    .build();

RespostaUnificada resposta = gerenciador.agendarEmMultiplasPlataformas(
    conteudoAgendado,
    Arrays.asList("TWITTER", "LINKEDIN")
);
```

### Obter Estatísticas

```java
Estatisticas stats = gerenciador.obterEstatisticas("TWITTER", "tw_12345");

System.out.println("Visualizações: " + stats.getVisualizacoes());
System.out.println("Curtidas: " + stats.getCurtidas());
System.out.println("Engajamento: " + stats.getTaxaEngajamento() + "%");
```

### Alternar Strategy

```java
// Mudar para resposta sumarizada
gerenciador.setRespostaStrategy(new RespostaSumarizadaStrategy());

// Voltar para resposta detalhada
gerenciador.setRespostaStrategy(new RespostaDetalhadaStrategy());
```

## Casos de Uso

### Caso de Uso 1: Campanha Multi-Plataforma

Uma agência precisa lançar uma campanha simultaneamente em todas as plataformas.

```java
Conteudo campanha = new Conteudo.Builder()
    .texto("🚀 Lançamento da Nova Linha de Produtos!")
    .hashtags(Arrays.asList("Lancamento", "Novidade", "ProdutosTop"))
    .midias(Arrays.asList("banner.jpg", "video.mp4"))
    .tipo(TipoConteudo.POST)
    .build();

RespostaUnificada resposta = gerenciador.publicarEmTodasPlataformas(campanha);
```

### Caso de Uso 2: Conteúdo Específico por Plataforma

Publicar vídeo apenas no TikTok e Instagram Reels.

```java
Conteudo videoViral = new Conteudo.Builder()
    .texto("Confira nosso novo tutorial! #Tutorial #Aprenda")
    .midias(Arrays.asList("tutorial.mp4"))
    .tipo(TipoConteudo.REEL)
    .build();

gerenciador.publicarEmMultiplasPlataformas(
    videoViral,
    Arrays.asList("TIKTOK", "INSTAGRAM")
);
```

### Caso de Uso 3: Análise de Performance

Obter estatísticas consolidadas de múltiplas publicações.

```java
Map<String, String> publicacoes = Map.of(
    "TWITTER", "tw_12345",
    "INSTAGRAM", "ig_67890",
    "LINKEDIN", "urn:li:share:abc123"
);

List<Estatisticas> stats = gerenciador.obterEstatisticasConsolidadas(publicacoes);

long totalVisualizacoes = stats.stream()
    .mapToLong(Estatisticas::getVisualizacoes)
    .sum();
```

## Extensibilidade

### Adicionar Nova Plataforma

Para adicionar suporte a uma nova plataforma (ex: YouTube):

1. **Criar a API Simulada**:

```java
public class YouTubeAPI {
    public YouTubeVideo uploadVideo(VideoRequest request) { ... }
    // ... outros métodos
}
```

2. **Criar o Adapter**:

```java
public class YouTubeAdapter implements RedeSocialService {
    private final YouTubeAPI youtubeAPI;
    
    @Override
    public Publicacao publicar(Conteudo conteudo) throws PublicacaoException {
        // Adapta a chamada para a API do YouTube
    }
    // ... implementar outros métodos
}
```

3. **Registrar na Factory**:

```java
RedeSocialFactory.registrar("YOUTUBE", YouTubeAdapter::new);
```

4. **Usar normalmente**:

```java
gerenciador.adicionarPlataforma(new ConfiguracaoPlataforma.Builder()
    .plataforma("YOUTUBE")
    .credenciais("youtube-api-key")
    .build());
```

### Criar Nova Strategy

```java
public class RespostaComMetricasStrategy implements RespostaStrategy {
    @Override
    public RespostaUnificada processar(List<Publicacao> publicacoes) {
        // Implementação customizada com métricas específicas
    }
}

// Usar
gerenciador.setRespostaStrategy(new RespostaComMetricasStrategy());
```

## Diagrama de Classes

### Visão Geral

```
┌─────────────────────────────────────────────────────────────────┐
│                     <<interface>>                               │
│                   RedeSocialService                             │
├─────────────────────────────────────────────────────────────────┤
│ + autenticar(credenciais: String): void                         │
│ + publicar(conteudo: Conteudo): Publicacao                      │
│ + agendar(conteudo: Conteudo): Publicacao                       │
│ + remover(publicacaoId: String): boolean                        │
│ + obterEstatisticas(publicacaoId: String): Estatisticas         │
│ + getNomePlataforma(): String                                   │
│ + isAutenticado(): boolean                                      │
└────────────────────┬────────────────────────────────────────────┘
                     │ implementa
         ┌───────────┼──────────┬───────────┐
         │           │          │           │
    ┌────▼───┐  ┌───▼─────┐  ┌──▼─────┐  ┌──▼──────┐
    │Twitter │  │Instagram│  │LinkedIn│  │TikTok   │
    │Adapter │  │Adapter  │  │Adapter │  │Adapter  │
    └────┬───┘  └───┬─────┘  └──┬─────┘  └──┬──────┘
         │          │           │           │
         │ usa      │ usa       │ usa       │ usa
         │          │           │           │
    ┌────▼───┐  ┌──▼──────┐  ┌──▼─────┐  ┌──▼──────┐
    │Twitter │  │Instagram│  │LinkedIn│  │TikTok   │
    │  API   │  │  API    │  │  API   │  │  API    │
    └────────┘  └─────────┘  └────────┘  └─────────┘
```

### Factory e Strategy

```
┌──────────────────────────────┐
│   RedeSocialFactory          │
├──────────────────────────────┤
│ - registry: Map<String, ...> │
├──────────────────────────────┤
│ + criar(plataforma): Service │
│ + criarAutenticado(...): ... │
│ + registrar(nome, ...): void │
└──────────────────────────────┘

┌──────────────────────────────────┐
│   <<interface>>                  │
│   RespostaStrategy               │
├──────────────────────────────────┤
│ + processar(List): Resposta      │
└────────────┬─────────────────────┘
             │
      ┌──────┴─────────────┐
      │                    │
┌─────▼───────────┐  ┌─────▼────────────┐
│RespostaDetalhada│  │RespostaSumarizada│
│  Strategy       │  │  Strategy        │
└─────────────────┘  └──────────────────┘
```
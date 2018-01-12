# Atividade prática de programação

O objetivo desta prática é adicionar ao jogo **SpaceEvader** uma forma de controlar os mecanismos do jogo por meio da orientação do dispositivo móvel.
Para implementar essa funcionalidade, duas APIs serão utilizadas: A API da biblioteca **Gamepad**; e a API **padrão** do SDK da plataforma Android.
Durante a prática, os participantes podem utilizar recursos online, como a documentação das respectivas APIs, bem como os documentos de treinamento de cada API: [treinamento-android](treinamento-android.md) e [treinamento-gamepad](treinamento-gamepad.md).

A sequência de passos a ser executada no projeto **SpaceEvader** é apresenta a seguir nesse documento.
Além disso, o código fonte inicial do projeto apresenta comentários (`// TODO: ...`) indicando qual passo precisa ser implementado, e o projeto contém um conjunto de *testes de unidade* que podem ser executados a qualquer momento do experimento para verificar o progresso da implementação.

#### Tela do menu principal

Dentro do arquivo `MenuActivity.kt`:

1. **Declare as variáveis** necessárias para receber os dados sobre a **orientação** do dispositivo móvel;

Dentro do arquivo `MenuView.kt`:

2. **Adicione** na classe `MenuView` **a interface** que habilita o recebimento **de eventos** dos sensores por meio de um *método callback*;

De volta ao arquivo `MenuActivity.kt`:

3. **Inicialize e configure** as variáveis declaradas no *passo #1* dentro do método `onCreate()` da classe `MenuActivity`;

4. Adicione o método `onResume()` e implemente os procedimentos necessários para **iniciar o recebimento de eventos**;

5. Adicione o método `onPause()` e implemente os procedimentos necessários para **cancelar o recebimento de eventos**;

Dentro do arquivo `MenuView.kt`:

6. **Implemente** dentro do **método callback** da interface adicionada no *passo #2* os procedimentos necessários **para obter** o vetor com **a orientação** do dispositivo, e utilize o **primeiro valor do vetor** (*azimuth*) como argumento para chamar o método `atualizarEixo()`.

#### Tela do jogo

Dentro do arquivo `GameActivity.kt`:

7. **Declare as variáveis** necessárias para receber os dados sobre a **orientação** do dispositivo móvel;

Dentro do arquivo `GameView.kt`:

8. **Adicione** na classe `GameView` **a interface** que habilita o recebimento **de eventos** dos sensores por meio de um *método callback*;

De volta ao arquivo `GameActivity.kt`:

9. **Inicialize e configure** as variáveis declaradas no *passo #7* dentro do método `onCreate()` da classe `GameActivity`;

10. Implemente no método `onResume()` os procedimentos necessários para **iniciar o recebimento de eventos**;

11. Implemente no método `onPause()` os procedimentos necessários para **cancelar o recebimento de eventos**;

Dentro do arquivo `GameView.kt`:

12. **Implemente** dentro do **método callback** da interface adicionada no *passo #8* os procedimentos necessários **para obter** o vetor com **a orientação** do dispositivo, e utilize o **primeiro valor do vetor** (*azimuth*) como argumento para chamar o método `atualizarEixo()`. 

13. Execute os testes no pacote `io.github.vekat.spaceevader (test)` e verifique o resultado. 

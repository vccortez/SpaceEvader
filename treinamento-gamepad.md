# API da biblioteca Gamepad

Para obtermos acesso aos dados dos sensores de um dispositivo Android por meio da API da biblioteca Gamepad, são necessários pelo menos dois componentes: uma instância configurada da classe `Gamepad`; e uma implementação da interface `ViewHolder`.

### Utilização básica

A classe `Gamepad` faz o papel de controle ou *joystick* para o qual serão adicionados um conjunto de botões (classe `Input`) que recebem sinais de diferentes fontes (classe `Source`). Esses objetos são configurados de forma declarativa de forma a facilitar o reúso e a modularidade do tratamento de dados contextuais.

#### 1. Declarando as variáveis config e Gamepad

Funções de extensão são uma das funcionalidades da linguagem Kotlin que foram usadas no *design* da biblioteca **Gamepad**. Para inicializarmos um `Gamepad`, é possível utilizar a função `gamepad()` de uma classe ou objeto que implemente a interface `ViewHolder`. O método `gamepad()` recebe como argumento uma função inicializadora com assinatura `Gamepad.() -> Unit`, ou seja, uma função que possui acesso ao escopo de um `Gamepad` e retorna nada. Todas as alterações realizadas no `Gamepad` desse escopo serão aplicadas ao objeto `Gamepad` que será instanciado e retornado pela função. 

Uma vez que funções em Kotlin podem ser armazenadas em variáveis, é possível armazenar a **configuração** de um `Gamepad` em uma variável externa, permitindo a reutilização dessa configuração na criação de novos `Gamepad`s. O exemplo abaixo apresenta como essa tarefa pode ser realizada:

````kotlin
// Declarando `config` fora da activity, no escopo do projeto
// permitindo que ela seja reutilizada por outras activities
lateinit var config: Gamepad.() -> Unit

// Exemplo dentro de uma activity chamada `GamepadActivity`
class GamepadActivity : AppCompatActivity() {
  // Declarando um `Gamepad` para agrupar botões
  lateinit var gamepad: Gamepad

  /* ... Restante da Activity ... */
}
````

#### 2. Adicionando a interface ViewHolder

Para recebermos eventos do `Gamepad`, é necessário implementar a interface `ViewHolder`. Essa interface possui uma propriedade do tipo `View` (`instance`), e um método **callback** (`onGamepadEvent()`) que será chamado pela biblioteca sempre que novos eventos forem processados por um `Gamepad`.

Normalmente, essa interface é implementada por uma subclasse de `View` que quer utilizar os dados dos sensores. Para fazer isso, basta adicionar a interface na assinatura da classe, por exemplo:

````kotlin
// Adicionando `ViewHolder` na view `ExemploView`
class ExemploView(context: Context) : View(context), ViewHolder {
  // Implementação da propriedade `instance`
  // `instance` aponta para a `View` atual
  override val instance: View = this

  // Implementação vazia do método `onGamepadEvent()`
  override fun onGamepadEvent(event: GamepadEvent) {}

  /* ... Restante da View ... */
}
````

#### 3. Configurando e inicializando o Gamepad

Após a declaração das variáveis mencionadas na [seção 1](#1-declarando-as-variáveis-config-e-gamepad), e a implementação da interface `ViewHolder` em uma *view* como na [seção 2](#2-adicionando-a-interface-viewholder), é possível configurar (com a variável `config`) e inicializar o `Gamepad` dentro to método `onCreate()`. É importante notar que a inicialização da variável `config` só precisa ser realizada **uma vez**, pela primeira activity que irá utilizá-la.

````kotlin
/* ... Declarações vistas na seção #1 ... */

// Exemplo dentro de uma activity chamada `GamepadActivity`
class GamepadActivity : AppCompatActivity() {
  /* ... Declarações vistas na seção #1 ... */
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    /* ... Outras inicializações relaciondas com a View ... */
    
    // Inicializando a variável `config` pela primeira vez
    config = {
      // Declarando um botão do tipo `AxisButton`
      axisButton {
        // Declarando a variável `sensor` como acelerômetro
        val sensor = accelerometer("sensor")

        // Declarando como os dados serão processados
        // pelo botão com a função `handler`
        handler = fun(dados: ContextMap): Float {
          // Recupera os dados mais recentes do sensor
          // acelerômetro e os atribui à variável `acc`
          val acc = dados[sensor]!!

          // Retorna um valor `Float` que é igual à soma
          // das medidas de cada eixo do acelerômetro
          return (acc.x + acc.y + acc.z)
        }
      }
    }
    
    // Inicializando a variável `gamepad` com a config
    gamepad = view.gamepad(config)
  }
}
````

Diferentes sensores já foram implementados na biblioteca, incluíndo processos mais complexas como obter a orientação do dispositivo. Todos os sensores padrões oferecem funções para facilitar sua criação, como por exemplo o acelerômetro (`accelerometer()`), orientação (`orientation()`) e proximidade (`proximity()`).

#### 4. Ativando e desativando eventos em uma Activity

Após a inicialização do `Gamepad` e dos sensores escolhidos, é necessário chamar seu método `enableInputEvents()` para ativar o recebimento de eventos. Além disso, também é indicado desativar o recebimento de eventos com o método `disableInputEvents()` quando a `Activity` não estiver sendo utilizada. O local ideal para realizar esses procedimentos são os métodos `onResume()` e `onPause()` do ciclo de vida de uma `Activity`, como exemplificado a seguir:

````kotlin
// Exemplo dentro de uma activity chamada `SensorActivity`
class SensorActivity : AppCompatActivity() {
  /* ... Declarações vistas na seção #1 ... */
  
  /* ... Inicializações vistas na seção #3 ... */
  
  override fun onResume() {
    super.onResume()

    // Ativa os eventos quando a `Activity` for iniciada
    gamepad.enableInputEvents()
  }
  
  override fun onPause() {
    super.onPause()

    // Desativa os eventos quando a `Activity` for pausada
    gamepad.disableInputEvents()
  }
}
````

#### 5. Implementando o callback onGamepadEvent

Com a ativação dos eventos, o método **callback** `onGamepadEvent()` passará a receber eventos dos botões configurados na `Activity`. Dessa forma, o último passo é utilizar os dados desses eventos (`event`), que se encontram no vetor `axis`, por exemplo:

````kotlin
// Adicionando `ViewHolder` na view `ExemploView`
class ExemploView(context: Context) : View(context), ViewHolder {
  // Implementação da propriedade `instance`
  override val instance = this

  // Implementação simples do método `onGamepadEvent()`
  override fun onGamepadEvent(event: GamepadEvent) {
    // Escreve os valores no vetor do evento
    // para a saída padrão da aplicação
    println( Arrays.toString(event.axis) )
  }

  /* ... Restante da View ... */
}
````

### Exemplo avançado

O exemplo a seguir apresenta como podemos configurar um `Gamepad` em uma `Activity` para receber os dados da orientação do dispositivo móvel e enviar apenas a direção do norte magnético (`azimuth`) para uma `View` que implemente a interface `ViewHolder`, permitindo que esse dado seja utilizado de alguma forma:

````kotlin
/* Declarando `config` fora da activity (seção 1) */
lateinit var config: Gamepad.() -> Unit

// Exemplo de implementação na activity `OrientationActivity`
class OrientationActivity : AppCompatActivity() {
  // Exemplo de view principal da activity
  lateinit var view: OrientationView

  /* Declarando um `Gamepad` para agrupar botões (seção 1) */
  lateinit var gamepad: Gamepad

  /* No método `onCreate()`, `config` e `gamepad` são inicializados (seção 3) */
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Exemplo de inicialização da view
    view = OrientationView(this)

    setContentView(view)

    // Inicialização da configuração `config`
    config = {
      axisButton {
        val orientacao = orientation("orientacao")

        handler = fun(dados: ContextMap): Float {
          val dadosOrientacao = dados[orientacao]!!

          return dadosOrientacao.azimuth
        }
      }
    }

    // Inicializando o `gamepad`
    gamepad = view.gamepad(config)
  }

  /* No método `onResume()`, os eventos são ativados (seção 4) */
  override fun onResume() {
    super.onResume()

    // A partir desse momento, é possível receber eventos
    gamepad.enableInputEvents()
  }

  /* No método `onPause()`, os eventos são desativados (seção 4) */
  override fun onPause() {
    super.onPause()

    // A partir desse momento, não é possível receber eventos
    gamepad.disableInputEvents()
  }
}
````

View que implementa a interface `ViewHolder` e utiliza o valor do `azimuth` obtido na activity `OrientationActivity` acima:

````kotlin
/* Adicionando `ViewHolder` na view `OrientationView` (seção 2) */
class OrientationView(context: Context) : View(context), ViewHolder {
  // Implementação da propriedade `instance`
  override val instance = this

  /* Implementação do método `onGamepadEvent()` (seção 5) */
  override fun onGamepadEvent(event: GamepadEvent) {
    // Recupera o 'azimuth' na primeira posição
    // do vetor de dados do evento
    val direcaoNorte = event.axis[0]

    /* ... Use `direcaoNorte` de alguma forma ... */
  }

  /* ... Restante da View ... */
}
````
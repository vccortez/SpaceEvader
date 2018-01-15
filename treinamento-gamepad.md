# API da biblioteca Gamepad

Para obtermos acesso aos dados dos sensores de um dispositivo Android por meio da API da biblioteca Gamepad, são necessários pelo menos dois componentes: uma instância configurada da classe `Gamepad`; e uma implementação da interface `ViewHolder`.

### Utilização básica

#### 1. Declarando as variáveis config e Gamepad

````kotlin
// Declarando `config` fora da activity, no escopo do projeto
lateinit var config: Gamepad.() -> Unit

// Exemplo dentro de uma activity chamada `GamepadActivity`
class GamepadActivity : AppCompatActivity() {
  // Declarando um `Gamepad` para agrupar botões
  lateinit var gamepad: Gamepad

  /* ... Restante da Activity ... */
}
````

#### 2. Adicionando a interface ViewHolder

````kotlin
// Adicionando `ViewHolder` na view `ExemploView`
class ExemploView(context: Context): View(context), ViewHolder {
  // Implementação da propriedade `instance`
  override val instance = this

  // Implementação vazia do método `onGamepadEvent()`
  override fun onGamepadEvent(event: GamepadEvent) {}

  /* ... Restante da View ... */
}
````

#### 3. Configurando e inicializando o Gamepad

````kotlin
// Exemplo dentro de uma activity chamada `GamepadActivity`
class GamepadActivity : AppCompatActivity() {
  /* ... Declarações vistas na seção #1 ... */
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    /* ... Outras inicializações relaciondas com a View ... */
    
    // Inicializando a variável `config`
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

#### 4. Ativando e desativando eventos em uma Activity

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

### Exemplo avançado

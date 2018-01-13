# API padrão do Android SDK

Para acessar os dados dos sensores de um dispositivo Android por meio da API padrão, são necessários pelo menos três componentes:
uma instância da classe `SensorManager`; uma ou mais instâncias da classe `Sensor`; e uma implementação da interface `SensorEventListener`.

### Utilização básica

Geralmente, a declaração e inicialização da classe `SensorManager` e das instâncias de `Sensor` estão associadas com o ciclo de vida de uma `Activity` Android, enquanto a implementação da interface `SensorEventListener` pode ser realizada de forma independente.
Nas seções a seguir, serão exemplificados como isso pode ser implementado.

#### 1. Declarando as variáveis SensorManager e Sensors

Um `SensorManager` pode ser visto como um serviço que pertence ao contexto da aplicação Android (`Context`) e, por esse motivo, a sua inicialização só pode ser realizada *após* termos acesso ao contexto da aplicação.
Uma maneira de declarar um `SensorManager` e `Sensor` em Kotlin sem utilizar tipos anuláveis é por meio da palavra chave `lateinit`, por exemplo:

````kotlin
// Exemplo dentro de uma activity chamada `SensorActivity`
class SensorActivity : AppCompatActivity() {
  // Declarando um `SensorManager` para administrar os sensores
  lateinit var sensorManager: SensorManager
  
  // Declarando um `Sensor` para inicializar futuramente
  lateinit var sensor: Sensor
  
  /* ... Restante da Activity ... */
}
````

#### 2. Adicionando a interface SensorEventListener

A API padrão precisa de uma implementação da interface `SensorEventListener`.
Essa interface possui dois métodos **callback** (`onAccuracyChanged()` e `onSensorChanged`) que serão chamados sempre que novos dados forem capturados pelos sensores registrados.
Apenas o método `onSensorChanged()` retorna as leituras dos sensores, sendo o **callback** mais importante para o nosso caso. 

Normalmente, essa interface é implementada pela classe que quer utilizar os dados dos sensores, como por exemplo, uma `View`.
Para fazer isso, basta adicionar a interface na assinatura da classe, por exemplo:

````kotlin
// Adicionando `SensorEventListener` na view `ExemploView`
class ExemploView(context: Context): View(context), SensorEventListener {
  // Implementação vazia do método `onSensorChanged()`
  override fun onSensorChanged(event: SensorEvent?) {}
  
  // Implementação vazia do método `onAccuracyChanged()`
  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
  
  /* ... Restante da View ... */
}
````

#### 3. Inicializando o SensorManager e Sensors 

Como mencionado na **seção #1**, somente é possível obter instâncias de `SensorManager` e `Sensor` quando o contexto da aplicação for devidamente inicializado.
Dessa forma, a inicialização das variáveis declaradas na primeira seção desse documento é realizada no método `onCreate()` da seguinte forma:

````kotlin
// Exemplo dentro de uma activity chamada `SensorActivity`
class SensorActivity : AppCompatActivity() {
  /* ... Declarações vistas na seção #1 ... */
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    /* ... Outras inicializações relaciondas com a View ... */
    
    // Inicializando a variável `sensorManager`
    sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    
    // Inicializando a variável `sensor` como acelerômetro
    sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
  }
}
````

Para cada dispositivo Android, existem diferentes tipos e quantidades de sensores disponíveis.
No entanto, os sensores **acelerômetro** (`Sensor.TYPE_ACCELEROMETER`) e **magnetômetro** (`Sensor.TYPE_MAGNETIC_FIELD`) estão presentes na maioria dos dispositivos.

#### 4. Ativando e desativando eventos em uma Activity

Após a inicialização do `SensorManager` e dos sensores escolhidos, é necessário chamar o método `registerListener()` para ativar o recebimento de eventos.
Além disso, também é indicado desativar o recebimento de eventos com o método `unregisterListener()` quando a `Activity` não estiver sendo utilizada.
O local ideal para realizar esses procedimentos são os métodos `onResume()` e `onPause` do ciclo de vida de uma `Activity`, como exemplificado a seguir:

````kotlin
// Exemplo dentro de uma activity chamada `SensorActivity`
class SensorActivity : AppCompatActivity() {
  /* ... Declarações vistas na seção #1 ... */
  
  /* ... Inicializações vistas na seção #3 ... */
  
  override fun onResume() {
    super.onResume()
    // Registrando o listener `view` para o `sensor`, com velocidade de eventos `SENSOR_DELAY_GAME` 
    sensorManager.registerListener(view, sensor, SensorManager.SENSOR_DELAY_GAME)
  }
  
  override fun onPause() {
    super.onPause()
    // Desativando o listener `view` para todos os sensores registrados anteriormente 
    sensorManager.unregisterListener(view)
  }
}
````

#### 5. Implementando o callback onSensorChanged

Com a `Activity` devidamente configurada, o método `onSensorChanged()` passará a receber eventos de todos os sensores registrados para essa instância.
Dessa forma, o último passo é utilizar os dados do evento (`event`), que se encontram no vetor `values`, por exemplo:

````kotlin
// Adicionando `SensorEventListener` na view `ExemploView`
class ExemploView(context: Context): View(context), SensorEventListener {
  // Implementação simples do método `onSensorChanged()`
  override fun onSensorChanged(event: SensorEvent?) {
    // Verifica se o evento não é nulo
    if (event != null) {
      // Escreve os valores no vetor do evento
      // para a saída padrão da aplicação
      println( Arrays.toString(event.values) )
    }
  }
  
  // Implementação vazia do método `onAccuracyChanged()`
  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
  
  /* ... Restante da View ... */
}
````

### Exemplo avançado

O sensor **acelerômetro** mede a aceleração aplicada ao dispositivo (m/s*s).
Já o sensor **magnetômetro** mede os campos magnéticos do ambiente.
Ambos os sensores retornam 3 valores, correspondendo aos eixos X, Y e Z.
É possível combinar os dados medidos pelos dois sensores para obter a **orientação** do dispositivo móvel, por meio dos métodos `SensorManager.getOrientation()` e `SensorManager.getRotationMatrix()` da seguinte forma:

````kotlin
class OrientationActivity : AppCompatActivity() {
  // Declare um SensorManager para administrar os sensores
  lateinit var sensorManager: SensorManager
  // Declare um Sensor para o acelerômetro
  lateinit var sensorAcc: Sensor
  // Declare um Sensor para o magnetômetro
  lateinit var sensorMag: Sensor

  // No método onCreate, o manager e o sensor são inicializados
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    sensorManager = getSystemService(Context.SENSOR_SERVICE)

    sensorAcc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    sensorMag = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
  }

  // No método onResume, o SensorEventListener é registrado
  override fun onResume() {
    super.onResume()

    // A partir desse momento, é possível receber eventos
    sensorManager.registerListener(this, sensorAcc, SensorManager.SENSOR_DELAY_GAME)
    sensorManager.registerListener(this, sensorMag, SensorManager.SENSOR_DELAY_GAME)
  }

  // No método onPause, o SensorEventListener é removido
  override fun onPause() {
    super.onPause()

    // A partir desse momento, não é possível receber eventos
    sensorManager.unregisterListener(this)
  }
}
````

View que implementa a interface `SensorEventListener` e processa os valores do acelerômetro e magnetômetro:

````kotlin
class OrientationView(context: Context) : View(context), SensorEventListener {
  // Declare dois vetores para guardar os valores dos sensores
  var dadosAcc: FloatArray? = null
  var dadosMag: FloatArray? = null
  
  override fun onSensorChanged(event: SensorEvent?) {
    if (event != null) {
      // Descobre qual sensor enviou dados e
      // salva os dados no vetor correto
      when (event.sensor.type) {
        Sensor.TYPE_ACCELEROMETER -> dadosAcc = event.values
        Sensor.TYPE_MAGNETIC_FIELD -> dadosMag = event.values
      }

      // Se os dois vetores possuírem dados
      if (dadosAcc != null && dadosMag != null) {
        val matrizIdentidade = FloatArray(9)
        val matrizRotacional = FloatArray(9)
        
        // Calcula a matriz rotacional do dispositivo
        // a partir dos dados dos sensores
        if (SensorManager.getRotationMatrix(matrizRotacional, matrizIdentidade, dadosAcc, dadosMag)) {
          val orientacao = FloatArray(3)
        
          // Calcula o vetor de orientação do dispositivo
          // em ângulos de Euler
          SensorManager.getOrientation(matrizRotacional, orientacao)
        
          // O primeiro valor do vetor de orientação
          // indica o 'azimuth', a direção do Norte magnético
          val direcaoNorte = orientacao[0]
          // Faça alguma coisa com o valor da direção Norte
        }
      }
    }
  }

  override fun onAccuracyChanged(sensor: Sensor?, precisao: Int) {}
}
````

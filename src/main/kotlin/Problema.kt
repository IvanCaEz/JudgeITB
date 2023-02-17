import com.sun.org.apache.xpath.internal.operations.Bool
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File



@Serializable
data class Problema (var numProblema: Int, var enunciado: String, var inputPub: Array<String>,
                var outputPub: Array<String>, var inputPriv: Array<String>,
                     var outputPriv: Array<String>, var resuelto: Boolean, var intentos: Int) {


    fun mostrarProblema(problema: Problema){
        println(problema.enunciado)
        println("$cyan${bold}Exemple 1$reset")
        println("${problema.inputPub[0]}\n$purple${bold}Entrada:$reset ${problema.inputPub[1]}")
        println("${problema.outputPub[0]}\n$purple${bold}Sortida:$reset ${problema.outputPub[1]}")
        println("$cyan${bold}Exemple 2$reset")
        println("$purple${bold}Entrada:$reset ${problema.inputPub[2]}")
        println("$purple${bold}Sortida:$reset ${problema.outputPub[2]}")
    }

    fun intentarProblema(numProblema: Int, currentProblema: Problema): Boolean {
        //val listaIntentos = mutableListOf<Triple<Int, String, Boolean>>()

        val random = (0..2).random()
        println("El teu input")
        println("$purple${bold}Entrada:$reset ${currentProblema.inputPriv[random]}")
        println("$purple${bold}Sortida:$reset ???")
        println("Per abandonar el problema entra SORTIR")
        var userAnswer: String
        var resolt = false
        var intents = 0
        val listOfUserAnswers = mutableListOf<String>()
        do {
            intents++
            userAnswer = scanner.nextLine().uppercase()
            if (userAnswer != currentProblema.outputPriv[random].uppercase() && userAnswer != "SORTIR" ) {
                println("Resposta incorrecta, torna-ho a intentar")
            } else if (userAnswer == currentProblema.outputPriv[random].uppercase()){
                resolt = true
            }
            //listaIntentos.add(Triple(intents, userAnswer, resolt))
            listOfUserAnswers.add(userAnswer)

        } while (userAnswer != currentProblema.outputPriv[random].uppercase() && userAnswer != "SORTIR")

        val userIntent = Json.encodeToString(Intento(numProblema, currentProblema.enunciado,
            currentProblema.inputPriv[random], listOfUserAnswers, intents, resolt ))

        File("src/main/kotlin/problemes/intentos.json").appendText(userIntent+"\n")


        return resolt

    }

}

@Serializable
data class Intento(val numProblema: Int, val enunciado: String, val inputPriv: String,
                   val outputPriv: MutableList<String>, val intentos: Int, val resuelto: Boolean)


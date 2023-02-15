import com.sun.org.apache.xpath.internal.operations.Bool
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File


class ListaProblemas{
    /*
    val listaProblemasResueltos = mutableListOf<ProblemaResuelto>()

    val listaProblemas = arrayOf(
        Problema("L'usuari escriu 4 enters i s'imprimeix el valor de sumar el primer amb el segon, multiplicat per el mòdul del tercer amb el quart."
            , arrayOf("Per l’entrada rebreu 4 nombres enters introduïts per l’usuari.","2 5 10 4","23 31 156 5"), arrayOf("La sortida ha de ser un enter que representa el resultat de la següent operació (a + b) * (c % d)..","14","54"),
            arrayOf("25 45 56 12", "23 258 12 25", "-5 2 -78 32"), arrayOf("560","3372", "42" )),
        Problema("En una escola tenim tres classes i volem saber quin és el nombre de taules que necessitarem tenir en total.\nDependrà del nombre d'alumnes per aula. Cal tenir en compte que a cada taula hi caben 2 alumnes."
            , arrayOf("Per l’entrada rebreu 3 enters que representen el nombre d’alumnes que hi ha a cada classe.","22 25 26","31 29 19"),
            arrayOf("La sortida ha de ser un enter que representi el nombre de pupitres necessaris.","37","41"),
            arrayOf("25 11 29", "1 2 5", "25 2 1"), arrayOf("34", "5", "15")),
        Problema("Escriu un programa que donat un nombre enter, indiqui si aquest és primer o no."
            , arrayOf("Per l’entrada rebreu un nombre enter.","11","128"), arrayOf("La sortida ha d’imprimir si el nombre és primer o no.","Primer","No primer"),
            arrayOf("425", "367", "666"), arrayOf("NO PRIMER", "PRIMER", "NO PRIMER")))


     */
}
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
        val listaIntentos = mutableListOf<Triple<Int, String, Boolean>>()

        val random = (0..2).random()
        println("$purple${bold}Entrada:$reset ${currentProblema.inputPriv[random]}")
        println("$purple${bold}Sortida:$reset ???")
        println("Per abandonar el problema entra SORTIR")
        var userAnswer: String
        var resolt = false
        var intents = 0
        val listOfUserAnswers = mutableListOf<String>()
        do {
            intents++
            userAnswer = scanner.nextLine().uppercase().toString()
            if (userAnswer != currentProblema.outputPriv[random].uppercase() && userAnswer != "SORTIR" ) {
                println("Resposta incorrecta, torna-ho a intentar")
            } else if (userAnswer == currentProblema.outputPriv[random].uppercase()){
                resolt = true
            }
                    listaIntentos.add(Triple(intents, userAnswer, resolt))
            listOfUserAnswers.add(userAnswer)

        } while (userAnswer != currentProblema.outputPriv[random].uppercase() && userAnswer != "SORTIR")

        val userIntent = Json.encodeToString<Intento>(Intento(numProblema, currentProblema.enunciado,
            currentProblema.inputPriv[random], listOfUserAnswers, intents, resolt ))

        File("src/main/kotlin/problemes/intentos.json").appendText(userIntent+"\n")


        return resolt

    }

}

@Serializable
data class Intento(val numProblema: Int, val enunciado: String, val inputPriv: String,
                   val outputPriv: MutableList<String>, val intentos: Int, val resuelto: Boolean)


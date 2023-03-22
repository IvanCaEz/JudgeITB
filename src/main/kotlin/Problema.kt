import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.sql.Connection
import java.sql.PreparedStatement


@Serializable
class Problema (var numProblema: Int, var enunciado: String, var inputPub: Array<String>,
                var outputPub: Array<String>, var inputPriv: Array<String>,
                     var outputPriv: Array<String>) {

    fun mostrarProblema(problema: Problema){
        println(problema.enunciado)
        println("$CYAN${BOLD}Exemple 1$RESET")
        println("${problema.inputPub[0]}\n$PURPLE${BOLD}Entrada:$RESET ${problema.inputPub[1]}")
        println("${problema.outputPub[0]}\n$PURPLE${BOLD}Sortida:$RESET ${problema.outputPub[1]}")
        println("$CYAN${BOLD}Exemple 2$RESET")
        println("$PURPLE${BOLD}Entrada:$RESET ${problema.inputPub[2]}")
        println("$PURPLE${BOLD}Sortida:$RESET ${problema.outputPub[2]}")
    }

    fun intentarProblema(numProblema: Int, currentProblema: Problema): Boolean {
        val random = (0..2).random()
        println("El teu input")
        println("$PURPLE${BOLD}Entrada:$RESET ${currentProblema.inputPriv[random]}")
        println("$PURPLE${BOLD}Sortida:$RESET ???")
        println("Per abandonar el problema entra $RED$BOLD$BOX SORTIR $RESET")
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
            listOfUserAnswers.add(userAnswer)

        } while (userAnswer != currentProblema.outputPriv[random].uppercase() && userAnswer != "SORTIR")
        listOfUserAnswers.remove("SORTIR")
        val userIntent = Intento(numProblema, currentProblema.enunciado,
            currentProblema.inputPriv[random], listOfUserAnswers, intents, resolt )
        val intentToJSON = Json.encodeToString(userIntent)

        saveIntentToDB(userIntent, connectToDB())

        File("src/main/kotlin/problemes/intentos.json").appendText(intentToJSON+"\n")

        return resolt

    }

    fun saveIntentToDB(intent: Intento, connection: Connection){

        val currentProblemaStatement = "INSERT INTO intents(num_problema, enunciat, input_priv, output_priv, intentos, resuelto ) VALUES (?, ?, ?, ?, ?, ?)"
        val statement: PreparedStatement = connection.prepareStatement(currentProblemaStatement)
        statement.setInt(1, intent.numProblema)
        statement.setString(2, intent.enunciado)
        statement.setString(3, intent.inputPriv)
        statement.setArray(4, intent.outputPriv as java.sql.Array )
        statement.setInt(5, intent.intentos)
        statement.setBoolean(6, intent.resuelto)
        statement.executeUpdate()

    connection.close()
    println("Conexión a la base de datos finalizada")
    }

}



@Serializable
data class Intento(val numProblema: Int, val enunciado: String, val inputPriv: String,
                   val outputPriv: MutableList<String>, val intentos: Int, val resuelto: Boolean)

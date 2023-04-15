import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException


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
            currentProblema.inputPriv[random], listOfUserAnswers, intents, resolt)
        val intentToJSON = Json.encodeToString(userIntent)

        saveIntentToDB(userIntent, db.connection!!)

        File("src/main/kotlin/problemes/intentos.json").appendText(intentToJSON+"\n")

        return resolt

    }

    fun saveIntentToDB(intent: Intento, connection: Connection){
        var idPrivat  = 0

            // Preparamos una query para obtener el id que corresponde al input y output privado que se ha generado
            // de manera aleatoria en el intento
            val selectOutputPriv = "SELECT id_prova_privat FROM prova_privat WHERE  input = ? "
           // val outputPrivResult = getOutputPrivStatement.executeQuery(selectOutputPriv)
            val outputStatement = connection.prepareStatement(selectOutputPriv)
            outputStatement.setString(1, intent.inputPriv)
            val result = outputStatement.executeQuery()
            while (result.next()){
                idPrivat = result.getInt("id_prova_privat")
                println("El id privat es $idPrivat")
            }

            for (respostaUsuari in 0.. intent.outputPriv.lastIndex){
                val currentProblemaStatement = "INSERT INTO intents(num_problema, id_prova_privat, resposta_usuari, resuelto ) VALUES (?, ?, ?, ?)"
                val statement: PreparedStatement = connection.prepareStatement(currentProblemaStatement)
                statement.setInt(1, intent.numProblema)
                statement.setInt(2, idPrivat)
                statement.setString(3, intent.outputPriv[respostaUsuari].toString())
                statement.setBoolean(4, intent.resuelto)
                statement.executeUpdate()
            }

    }

}



@Serializable
data class Intento(val numProblema: Int, val enunciado: String, val inputPriv: String,
                   val outputPriv: MutableList<String>, val intentos: Int, val resuelto: Boolean)

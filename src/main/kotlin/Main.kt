import kotlinx.serialization.json.Json
import java.io.File
import java.util.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString



const val reset = "\u001b[0m"
const val box = "\u001b[51m"
const val bold = "\u001b[1m"
const val underline = "\u001b[21m"
const val bgGold = "\u001b[43m"
const val bgGreen = "\u001b[48;5;28m"
const val bgGray = "\u001b[47m"
const val red = "\u001b[31m"
const val cyan = "\u001b[38;5;87m"
const val green = "\u001b[38;5;10m"
const val gold = "\u001b[33m"
const val yellow = "\u001b[38;5;11m"
const val gray = "\u001b[38;5;7m"
const val pink = "\u001b[38;5;207m"
const val purple = "\u001b[38;5;99m"
const val blue = "\u001b[38;5;69m"

val scanner = Scanner(System.`in`)
fun main() {
    var numProblema = 0
    val listaProblemas =  File("src/main/kotlin/problemes/problemes.json").readLines()
    var currentProblema: Problema
    var problema: Problema
    var problemasRemaining: MutableList<Int>
    when (showMenu()){
        1 ->{
            problemasRemaining = itinerariAprenentatge()
            for (i in problemasRemaining){
                numProblema = i
                do {
                    println("Problema $box$bold$pink ${numProblema + 1} $reset")
                    problema = Json.decodeFromString<Problema>(listaProblemas[numProblema])
                    currentProblema = Problema(problema.numProblema, problema.enunciado,problema.inputPub,
                        problema.outputPub,problema.inputPriv,problema.outputPriv,
                        resuelto = false, intentos = 0)
                    currentProblema.mostrarProblema(problema)
                    println("Vols intentar aquest problema?\n$green$bold$box SI $reset $red$bold$box NO $reset")
                    val intentar = scanner.nextLine().uppercase()

                    if (intentar == "SI"){
                        val intentoProblema = currentProblema.intentarProblema(problema.numProblema, problema)
                        if (intentoProblema){
                            println("Has acertat !")
                        } else println("No has pogut amb el problema")

                     //   numProblema++

                    } else if (intentar == "NO"){
                    //    numProblema++
                        val userIntent = Json.encodeToString<Intento>(Intento(numProblema, currentProblema.enunciado,
                            "NO INTENTAT", mutableListOf("NO INTENTAT"), 0, false ))

                        File("src/main/kotlin/problemes/intentos.json").appendText(userIntent+"\n")
                    }

                } while (numProblema != 3 || intentar == "SORTIR")

            }
        }
        2 -> showProblemList()
        3 -> showHistoryCompleted()
        4 -> showHelp()
        5 -> profe()
    }


    println("Vols veure l'historial?\n$green$bold$box SI $reset $red$bold$box NO $reset")
    val historial = scanner.nextLine().uppercase()
    if (historial == "SI"){
        println(showHistory())
    }
}

fun profe(){

}

fun showHelp(){

}
fun showHistoryCompleted(){

}
fun showProblemList(){

}
fun itinerariAprenentatge(): MutableList<Int> {
    val listaIntentos =  File("src/main/kotlin/problemes/intentos.json").readLines()
    val listaProblemasResueltos = mutableListOf<Int>()
    var listaProblemas = mutableListOf(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20)
    for (i in listaIntentos) {
        val intento = Json.decodeFromString<Intento>(i)
        if (intento.numProblema in listaProblemas){
            listaProblemas.remove(intento.numProblema)
        }
    }
    return  listaProblemas
}
fun showHistory(){
    val listaIntentos =  File("src/main/kotlin/problemes/intentos.json").readLines()
    for (i in listaIntentos){
        val intento = Json.decodeFromString<Intento>(i)
        println("Problema $box$bold$pink ${intento.numProblema} $reset")
        println(intento.enunciado)
        println("$purple${bold}Entrada:$reset ${intento.inputPriv}")
        println("$purple${bold}Sortida:$reset ${intento.outputPriv}")
        if (intento.resuelto){
            println("$purple${bold}Resolt:$reset $bold$box$green SI $reset")
        } else println("$purple${bold}Resolt:$reset $bold$box$red NO $reset")
        println("$purple${bold}Intents:$reset ${intento.intentos}")
    }
}

fun showMenu(): Int {
    var instruction: Int
    do {
        println("1. Seguir amb l'itinerari del projecte")
        println("2. Llista de problemes")
        println("3. Consultar històric de problemes resolts")
        println("4. Ajuda")
        println("5. Identifiació de professorat")
        println("6. Sortir")
        instruction = scanner.nextInt()
    } while (instruction !in 1..6 )

    return instruction
}




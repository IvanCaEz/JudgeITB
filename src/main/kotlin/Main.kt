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
    var numProblema: Int
    val listaProblemas =  File("src/main/kotlin/problemes/problemes.json").readLines()
    var currentProblema: Problema
    var problema: Problema
    when (showMenu("Main")){
        1 ->{
            val problemasRemaining = itinerariAprenentatge()
            for (i in problemasRemaining){
                numProblema = i-1
                do {
                    println("Problema $box$bold$pink ${numProblema+1} $reset")

                    problema = Json.decodeFromString(listaProblemas[numProblema])
                    currentProblema = Problema(problema.numProblema, problema.enunciado,problema.inputPub,
                        problema.outputPub,problema.inputPriv,problema.outputPriv,
                        problema.resuelto, problema.intentos)

                    currentProblema.mostrarProblema(problema)

                    println("Vols intentar aquest problema?\n$green$bold$box SI $reset $red$bold$box NO $reset")

                    val intentar = scanner.nextLine().uppercase()

                    if (intentar == "SI"){
                        val intentoProblema = currentProblema.intentarProblema(problema.numProblema, problema)
                        if (intentoProblema) {
                            println("Has acertat !")
                        } else{
                            println("No has pogut amb el problema")
                        }
                    } else if (intentar == "NO"){
                        val userIntent = Json.encodeToString(Intento(currentProblema.numProblema, currentProblema.enunciado,
                            "NO INTENTAT", mutableListOf("NO INTENTAT"), 0, false ))

                        File("src/main/kotlin/problemes/intentos.json").appendText(userIntent+"\n")
                    }

                } while (intentar == "SORTIR")

            }
        }
        2 -> showProblemList()
        3 -> showHistoryOfCompletedProblems()
        4 -> showHelp()
        5 ->{

            if (teacherLogin()){
                when (showMenu("Teacher")){
                    1 ->{
                        do {
                            addNewProblem()
                            println("Problema afegit, vols afegir un altre?\n$green$bold$box SI $reset $red$bold$box NO $reset")
                            val altreProblema = scanner.nextLine().uppercase()
                        } while (altreProblema != "NO")
                    }
                    2 ->{
                        when(showMenu("Report")){
                        1 -> puntuation()
                        2 -> restaPorIntentos()
                        3 -> resultadoGrafic()
                        }
                    }

                }
            } else{
                println("No t'has pogut identificar")
            }
        }
    }


    /*
     println("Vols veure l'historial?\n$green$bold$box SI $reset $red$bold$box NO $reset")
    val historial = scanner.nextLine().uppercase()
    if (historial == "SI"){
        println(showHistory())
    }
     */

}
fun addNewProblem(){
    val listaProblemas =  File("src/main/kotlin/problemes/problemes.json").readLines()
    val nextProblemNumber = listaProblemas.size+1
    println("Entra el enunciat")
    val enunciado = scanner.nextLine()
    println("""Entra les entrades del joc de proves public
        |Primer entra l'explicació de la entrada
        |Després entra les entrades
        |Entra ! quan acabis per desar.
    """.trimMargin())
    val inputPublic = mutableListOf<String>()
    do {
        val singleInput = scanner.nextLine()
        if (singleInput != "!"){
            inputPublic.add(singleInput)
        }
    } while (singleInput != "!")
    println("""Entra les sortides del joc de proves public
        |Primer entra l'explicació de la sortida
        |Després entra les sortides
        |Entra ! quan acabis per desar.
    """.trimMargin())
    val outputPublic = mutableListOf<String>()
    do {
        val singleOutput = scanner.nextLine()
        if (singleOutput != "!"){
            outputPublic.add(singleOutput)
        }
    } while (singleOutput != "!")

    println("""Entra les entrades del joc de proves privat
        |Entra tantes entrades com vulguis
        |Entra ! quan acabis per desar.
    """.trimMargin())

    val inputPriv = mutableListOf<String>()
    do {
        val singleInput = scanner.nextLine()
        if (singleInput != "!"){
            inputPriv.add(singleInput)
        }
    } while (singleInput != "!")

    println("""Entra les sortides del joc de proves privat
        |Entra tantes sortides com vulguis
        |Entra ! quan acabis per desar.
    """.trimMargin())
    val outputPrivat = mutableListOf<String>()
    do {
        val singleOutput = scanner.nextLine()
        if (singleOutput != "!"){
            outputPrivat.add(singleOutput)
        }
    } while (singleOutput != "!")

    val newProblema = Problema(nextProblemNumber, enunciado,inputPublic.toTypedArray(),
        outputPublic.toTypedArray(),inputPriv.toTypedArray(),outputPrivat.toTypedArray(),
        resuelto = false, intentos = 0)

    val encodeProblem = Json.encodeToString(newProblema )
    File("src/main/kotlin/problemes/problemes.json").appendText("\n"+encodeProblem)

}
fun teacherLogin(): Boolean {
    var password: String
    var intents = 3
    var profeName: String
    println("Entra el teu nom de profe")
    do{
        profeName = scanner.nextLine().uppercase()
        if (profeName != "JORDI" && profeName != "DANI" && profeName != "CIDO"){
            println("Aquest profe no existeix")
        }
    } while (profeName != "JORDI" && profeName != "DANI" && profeName != "CIDO")

    println("Benvingut $profeName")

    println("Entra la contrasenya $green$bold(COLINABO)$reset")
    do {
        password = scanner.nextLine().uppercase()
        if (password != "COLINABO"){
            intents--
            println("Contrasenya incorrecta, et queden $box$bold$red $intents $reset")
        }
    } while (password != "COLINABO" && intents != 0)

    return intents != 0

}

fun showHelp(){

}
fun showHistoryOfCompletedProblems() {
    val listaIntentos =  File("src/main/kotlin/problemes/intentos.json").readLines()
    for (i in listaIntentos) {
        val intento = Json.decodeFromString<Intento>(i)
        if (intento.resuelto){
            println("Problema $box$bold$pink ${intento.numProblema} $reset")
            println(intento.enunciado)
            println("$purple${bold}Entrada:$reset ${intento.inputPriv}")
            println("$purple${bold}Sortida:$reset ${intento.outputPriv}")
            println("$purple${bold}Resolt:$reset $bold$box$green SI $reset")
            println("$purple${bold}Intents:$reset ${intento.intentos}")
        }
    }
}
fun showProblemList(){

}
fun itinerariAprenentatge(): MutableList<Int> {
    val listaIntentos =  File("src/main/kotlin/problemes/intentos.json").readLines()
    val listaProblemas = mutableListOf(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20)
    for (i in listaIntentos) {
        val intento = Json.decodeFromString<Intento>(i)
        if (intento.resuelto){
            listaProblemas.remove(intento.numProblema)
        }
    }
    return  listaProblemas
}

fun showMenu(menuType: String): Int {
    var instruction = 0
    when (menuType){
        "Main" -> {
            do {
                println("1. Seguir amb l'itinerari del projecte")
                println("2. Llista de problemes")
                println("3. Consultar històric de problemes resolts")
                println("4. Ajuda")
                println("5. Identifiació de professorat")
                println("6. Sortir")
                instruction = scanner.nextLine().toInt()
            } while (instruction !in 1..6 )
            return instruction
        }
        "Teacher" -> {
            do {
                println("1. Afegir nous problemes")
                println("2. Treure report de la feina")
                instruction = scanner.nextLine().toInt()
            } while (instruction !in 1..2 )
            return instruction
        }
        "Report" -> {
            do {
                println("1. Treure una puntuació en funció dels problemes resolts")
                println("2. Descomptar per intents")
                println("3. Mostrar-ho de manera més o menys gràfica (a través de consola)")
                instruction = scanner.nextLine().toInt()
            } while (instruction !in 1..3 )
            return instruction
        }
    }
    return instruction
}
fun puntuation(){

}
fun restaPorIntentos(){

}
fun resultadoGrafic(){

}



/*
fun showFullHistory(){
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
 */


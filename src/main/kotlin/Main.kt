import kotlinx.serialization.json.Json
import java.io.File
import java.util.*
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
    val listaIntentos =  File("src/main/kotlin/problemes/intentos.json").readLines()
    var currentProblema: Problema
    var problema: Problema
    var instruction = 0
    do{
        showMenu("Main")
        instruction = scanner.nextLine().toInt()
        when(instruction){
            1 ->{
                var stop = false
                val problemasRemaining = itinerariAprenentatge(listaIntentos)
                while (!stop) {
                    for (i in problemasRemaining){
                        numProblema = i-1
                        println("Problema $box$bold$pink ${numProblema+1} $reset")

                        problema = Json.decodeFromString(listaProblemas[numProblema])
                        currentProblema = Problema(problema.numProblema, problema.enunciado,problema.inputPub,
                            problema.outputPub,problema.inputPriv,problema.outputPriv,
                            problema.resuelto, problema.intentos)

                        currentProblema.mostrarProblema(problema)

                        println("""Vols intentar aquest problema?
                                    |         $green$bold$box SI $reset $yellow$bold$box NEXT $reset
                                    |Per deixar de fer problemes entra $red$bold$box SORTIR $reset""".trimMargin())

                        val intentar = scanner.nextLine().uppercase()

                        if (intentar == "SI"){

                            val intentoProblema = currentProblema.intentarProblema(problema.numProblema, problema)
                            if (intentoProblema) {
                                println("Has acertat !")
                            } else{
                                println("No has pogut amb el problema")
                            }
                        } else if (intentar == "NEXT"){
                            println("""Carregant el següent problema$blue$bold ... $reset""".trimMargin())
                            val userIntent = Json.encodeToString(Intento(currentProblema.numProblema, currentProblema.enunciado,
                                "NO INTENTAT", mutableListOf("NO INTENTAT"), 0, false ))

                            File("src/main/kotlin/problemes/intentos.json").appendText(userIntent+"\n")
                        } else if (intentar == "SORTIR"){
                            stop = true
                            break
                        }
                    }
                }

            }
            2 ->{
                val problemAIntentar = showProblemList(listaProblemas)
                currentProblema = decodeProblem(problemAIntentar)

                println("Problema $box$bold$pink $problemAIntentar $reset")
                currentProblema.mostrarProblema(currentProblema)
                val intentoProblema = currentProblema.intentarProblema(problemAIntentar, currentProblema)
                if (intentoProblema) {
                    println("Has acertat !")
                } else{
                    println("No has pogut amb el problema")
                }
            }
            3 -> showHistoryOfCompletedProblems(listaIntentos)
            4 -> showHelp()
            5 ->{
                if (teacherLogin()){
                    do {
                        showMenu("Teacher")
                        val teacherInstruction = scanner.nextLine().toInt()
                        when (teacherInstruction){
                            1 ->{
                                do {
                                    addNewProblem(listaProblemas)
                                    println("Problema afegit, vols afegir un altre?\n$green$bold$box SI $reset $red$bold$box NO $reset")
                                    val altreProblema = scanner.nextLine().uppercase()
                                } while (altreProblema != "NO")
                            }
                            2 ->{
                                showMenu("Report")
                                do {
                                    val reportInstruction = scanner.nextLine().toInt()
                                    when(reportInstruction){
                                        1 -> puntuation()
                                        2 -> restaPorIntentos()
                                        3 -> resultadoGrafic()
                                    }
                                } while (reportInstruction != 0)
                            }
                        }
                    } while (teacherInstruction != 0)
                } else{
                    println("No t'has pogut identificar")
                }
            }
            6 -> println("""Sortint del Judge$blue$bold ITB $reset
                |        $blue$bold...$reset
            """.trimMargin())
        }

    } while(instruction != 6)

}

fun decodeProblem(numProblema: Int): Problema {
    val listaProblemas = File("src/main/kotlin/problemes/problemes.json").readLines()
    val problema: Problema = Json.decodeFromString(listaProblemas[numProblema])
    return Problema(
        problema.numProblema, problema.enunciado, problema.inputPub,
        problema.outputPub, problema.inputPriv, problema.outputPriv,
        problema.resuelto, problema.intentos
    )
}

fun showProblemList(listaProblemas: List<String>): Int {
    var problemAIntentar: Int
    var numProblema = 0
    var currentProblema: Problema
    var problema: Problema
    for (i in listaProblemas){
        println("Problema $box$bold$pink ${numProblema+1} $reset")

        problema = Json.decodeFromString(listaProblemas[numProblema])
        currentProblema = Problema(problema.numProblema, problema.enunciado,problema.inputPub,
            problema.outputPub,problema.inputPriv,problema.outputPriv,
            problema.resuelto, problema.intentos)

        currentProblema.mostrarProblema(problema)
        numProblema++
    }
    println("Entra el número del problema que vols intentar")
    do {
        problemAIntentar = scanner.nextLine().toInt()
    } while (problemAIntentar > listaProblemas.size)
    return problemAIntentar
}
fun addNewProblem(listaProblemas: List<String>){
    //val listaProblemas =  File("src/main/kotlin/problemes/problemes.json").readLines()
    val nextProblemNumber = listaProblemas.size+1
    println("Entra el enunciat")
    val enunciado = scanner.nextLine()
    println("""Entra les entrades del joc de proves public
        |Primer entra l'explicació de la entrada
        |Després entra les entrades
        |Entra $blue$bold!$reset quan acabis per desar.
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
        |Entra $blue$bold!$reset quan acabis per desar.
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
        |Entra $blue$bold!$reset quan acabis per desar.
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
        |Entra $blue$bold!$reset quan acabis per desar.
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
    println("""    Benvingut al ═════════════ Judge$blue$bold ITB $reset═════════════
        |══════════════════════════$bold$blue ESTUDIANTS $reset══════════════════════════
        |1. Seguir amb l'itinerari -> Mostrarà els problemes que falten
        |                             per resoldre.
        |2. Llista de problemes   ->  Mostrarà una llista de tots els 
        |                             problemes i permetrà escollir els
        |                             problemes que vols fer.
        |3. Consultar històric   ->   Mostrarà els problemes resolts amb
        |                             les respostes i intents que van
        |                             ser entrats.
        |══════════════════════════$bold$blue PROFESSORS $reset══════════════════════════
        |El sistema et demanarà un num d'usuari i una contrasenya per
        |identificar-te.
        |1. Afegir problema -> Permetrà afegir un nou problema a
        |                      la base de dades.
        |2. Report -> TODO
        |═════════════════════════════════════════════════════════════════
    """.trimMargin())
    println("""                    Tornant al menú principal
                |                              $blue$bold ...... $reset
            """.trimMargin())

}
fun showHistoryOfCompletedProblems(listaIntentos: List<String>) {
    //val listaIntentos =  File("src/main/kotlin/problemes/intentos.json").readLines()
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

fun itinerariAprenentatge(listaIntentos: List<String>): MutableList<Int> {
    //val listaIntentos =  File("src/main/kotlin/problemes/intentos.json").readLines()
    val listaProblemas = mutableListOf(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20)
    for (i in listaIntentos) {
        val intento = Json.decodeFromString<Intento>(i)
        if (intento.resuelto){
            listaProblemas.remove(intento.numProblema)
        }
    }
    return  listaProblemas
}

fun showMenu(menuType: String){
    when (menuType){
        "Main" -> {
            println("1. Seguir amb l'itinerari del projecte")
            println("2. Llista de problemes")
            println("3. Consultar històric de problemes resolts")
            println("4. Ajuda")
            println("5. Identifiació de professorat")
            println("6. Sortir")
        }
        "Teacher" -> {
            println("1. Afegir nous problemes")
            println("2. Treure report de la feina")
            println("0. Enrere")
        }
        "Report" -> {
            println("1. Treure una puntuació en funció dels problemes resolts")
            println("2. Descomptar per intents")
            println("3. Mostrar-ho de manera més o menys gràfica (a través de consola)")
            println("0. Enrere")
        }
    }
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


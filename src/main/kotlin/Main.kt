/**
 * @author Iván Martínez Cañero
 * @version 2.0 - 2023/02/08
 */

import java.util.*
import kotlin.math.round


const val RESET = "\u001b[0m"
const val BOX = "\u001b[51m"
const val BOLD = "\u001b[1m"
const val UNDERLINE = "\u001b[21m"
const val RED = "\u001b[31m"
const val CYAN = "\u001b[38;5;87m"
const val GREEN = "\u001b[38;5;10m"
const val ORANGE = "\u001b[38;5;202m"
const val YELLOW = "\u001b[38;5;11m"
const val PINK = "\u001b[38;5;177m"
const val PURPLE = "\u001b[38;5;99m"
const val BLUE = "\u001b[38;5;69m"

val scanner = Scanner(System.`in`)
fun main() {
    val listaProblemasDB = getProblemaFromDB(connectToDB())
    val listaIntentosDB = getIntentsFromDB(connectToDB())
    var currentProblema: Problema
    var instruction: Int
    do{
        showMenu("Main")
        instruction = scanner.nextLine().toInt()
        when(instruction){
            1 ->{
                var stop = false
                val problemasRemaining = itinerariAprenentatgeDB(listaProblemasDB, listaIntentosDB)
                while (!stop) {
                    for (problema in listaProblemasDB){
                        if (problema.numProblema in problemasRemaining){
                            println("Problema $BOX$BOLD$PINK ${problema.numProblema} $RESET")
                            problema.mostrarProblema(problema)
                            println("""Vols intentar aquest problema?
                                    |         $GREEN$BOLD$BOX SI $RESET $YELLOW$BOLD$BOX NEXT $RESET
                                    |Per deixar de fer problemes entra $RED$BOLD$BOX SORTIR $RESET""".trimMargin())

                            val intentar = scanner.nextLine().uppercase()
                            if (intentar == "SI"){
                                val intentoProblema = problema.intentarProblema(problema.numProblema, problema)
                                if (intentoProblema) {
                                    println("Has acertat !")
                                } else{
                                    println("No has pogut amb el problema")
                                }
                            } else if (intentar == "NEXT"){
                                println("""Carregant el següent problema$BLUE$BOLD ... $RESET""".trimMargin())
                            } else if (intentar == "SORTIR"){
                                stop = true
                                break
                            }
                        }

                    }
                }
            }
            2 ->{
                val problemAIntentar = showProblemList(listaProblemasDB)
                if (problemAIntentar != 0){
                    currentProblema = decodeProblem(problemAIntentar, listaProblemasDB)
                    println("Problema $BOX$BOLD$PINK $problemAIntentar $RESET")
                    currentProblema.mostrarProblema(currentProblema)
                    val intentoProblema = currentProblema.intentarProblema(problemAIntentar, currentProblema)
                    if (intentoProblema) {
                        println("Has acertat !")
                    } else{
                        println("No has pogut amb el problema")
                    }
                }

            }
            3 -> showHistoryOfProblemsFromDB(listaIntentosDB)
            4 -> showHelp()
            5 ->{
                if (teacherLogin()){
                    do {
                        showMenu("Teacher")
                        val teacherInstruction = scanner.nextLine().toInt()
                        when (teacherInstruction){
                            1 ->{
                                do {
                                    addNewProblem(listaProblemasDB)
                                    println("Problema afegit, vols afegir un altre?\n$GREEN$BOLD$BOX SI $RESET $RED$BOLD$BOX NO $RESET")
                                    val altreProblema = scanner.nextLine().uppercase()
                                } while (altreProblema != "NO")
                            }
                            2 -> getPuntuation(getIntents(listaIntentosDB), listaProblemasDB)
                        }
                    } while (teacherInstruction != 0)
                } else{
                    println("""No t'has pogut identificar.
                        |La teva direcció IP s'ha enviat al cap d'estudis.""".trimMargin())
                }
            }
            6 -> println("""          Sortint del Judge$BLUE$BOLD ITB $RESET
                |                  $BLUE$BOLD...$RESET
            """.trimMargin())
        }

    } while(instruction != 6)
}
/**
 * Aquesta funció filtra la llista de problemes amb el número del problema a intentar i el retorna
 * @param numProblema número del problema a intentar
 * @return Un Problema
 */
fun decodeProblem(numProblema: Int, listaProblemas: List<Problema>): Problema {
    return listaProblemas.filter { numProblema == it.numProblema }[0]
}
/**
 * Aquesta funció mostra tots els problemas que hi ha a la base de dades.
 * També permet escollir un problema per intentar resoldre'l,
 * si no es vol intentar cap, s'ha d'entrar 0
 * @param listaProblemas Llista amb tots els problemes
 * @return Un Int (el número del problema a intentar)
 */
fun showProblemList(listaProblemas: List<Problema>): Int {
    var problemAIntentar: String
    for (problema in listaProblemas){
        println("Problema $BOX$BOLD$PINK ${problema.numProblema} $RESET")
        problema.mostrarProblema(problema)
    }
    println("""Entra el$BOLD número$RESET del problema que vols intentar
        |Si no vols intentar cap problema entra $BOX$BOLD$ORANGE 0 $RESET
    """.trimMargin())
    do {
        problemAIntentar = scanner.nextLine()
    } while (problemAIntentar.toInt() > listaProblemas.size)
    return problemAIntentar.toInt()
}

/**
 * Aquesta funció permet al professor crear un nou problema.
 * Va demanant al professor que entri els camps del problema (enunciat, joc de proves...)
 * Per pasar al següent camp s'ha d'entrar "!"
 * @param listaProblemas Llista amb tots els problemes
 */
fun addNewProblem(listaProblemas: List<Problema>){
    val nextProblemNumber = listaProblemas.size+1
    println("Entra el enunciat")
    val enunciado = scanner.nextLine()
    println("""Entra les entrades del joc de proves public
        |Primer entra l'explicació de la entrada
        |Després entra les entrades
        |Entra $BLUE$BOLD!$RESET quan acabis per desar.
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
        |Entra $BLUE$BOLD!$RESET quan acabis per desar.
    """.trimMargin())
    val outputPublic = mutableListOf<String>()
    do {
        val singleOutput = scanner.nextLine()
        if (singleOutput != "!"){
            outputPublic.add(singleOutput)
        }
    } while (singleOutput != "!")

    println("""Entra les entrades del joc de proves privat.
        |Entra $BLUE${BOLD}3$RESET entrades.
    """.trimMargin())

    val inputPriv = mutableListOf<String>()
    repeat(3){
        val singleInput = scanner.nextLine()
        inputPriv.add(singleInput)

    }

    println("""Entra les sortides del joc de proves privat.
        |Entra $BLUE${BOLD}3$RESET sortides.
    """.trimMargin())
    val outputPrivat = mutableListOf<String>()
    repeat(3){
        val singleOutput = scanner.nextLine()
        outputPrivat.add(singleOutput)
    }

    val newProblema = Problema(nextProblemNumber, enunciado,inputPublic.toTypedArray(),
        outputPublic.toTypedArray(),inputPriv.toTypedArray(),outputPrivat.toTypedArray())

    //Cridem a la funció que s'encarrega de inserir el problema a la base de dades
    insertProblema(connectToDB(), newProblema)


}
/**
 * Aquesta funció demana un nom d'usuari i una contrasenya.
 * Si s'esgoten els intents a l'entrar la contrasenya, retorna fals i no s'entra al menu per professors,
 * @return Un Booleà
 */
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
    println("Entra la contrasenya $GREEN$BOLD(COLINABO)$RESET")
    do {
        password = scanner.nextLine().uppercase()
        if (password != "COLINABO"){
            intents--
            println("Contrasenya incorrecta, et queden $BOX$BOLD$RED $intents $RESET intents")
        }
    } while (password != "COLINABO" && intents != 0)

    return intents != 0
}

/**
 * Aquesta funció explica les instruccions
 */
fun showHelp(){
    println("""  Benvingut al ═══════ Judge$BLUE$BOLD ITB $RESET═══════
        |════════════════$BOLD$BLUE ESTUDIANTS $RESET════════════════
        |$BLUE${BOLD}1.$RESET ${UNDERLINE}Seguir amb l'itinerari$RESET
        |   Mostrarà els problemes que falten per
        |   resoldre.                            
        |$BLUE${BOLD}2.$RESET ${UNDERLINE}Llista de problemes$RESET
        |   Mostrarà una llista de tots els problemes 
        |   i permetrà escollir els problemes que
        |   vols fer.                        
        |$BLUE${BOLD}3.$RESET ${UNDERLINE}Consultar històric$RESET
        |   Mostrarà els problemes resolts amb les
        |   respostes i intents que van ser entrats.                           
        |════════════════$BOLD$BLUE PROFESSORS $RESET════════════════
        |  El sistema et demanarà un nom d'usuari i 
        |  una contrasenya per identificar-te.
        |$BLUE${BOLD}1. $RESET${UNDERLINE}Afegir problema$RESET
        |   Permetrà afegir un nou problema
        |   a la base de dades.
        |$BLUE${BOLD}2. $RESET${UNDERLINE}Report$RESET
        |   Mostrarà els problemes resolts, quants
        |   intents s'han fet servir i calcularà
        |   puntuació total 
        |═══════════════════════════════════════════
    """.trimMargin())
    println("""          Tornant al menú principal
                |                $BLUE$BOLD ...... $RESET
            """.trimMargin())

}
/**
 * Aquesta funció mostra els intents realitzats a cada problema i mostra si han estat resolts o no.
 * @param listaIntentos Llista amb tots els intents
 */
fun showHistoryOfProblemsFromDB(listaIntentos: List<Intento>) {
    for (intento in listaIntentos) {
        println("Problema $BOX$BOLD$PINK ${intento.numProblema} $RESET")
        println(intento.enunciado)
        println("$PURPLE${BOLD}Entrada:$RESET ${intento.inputPriv}")
        println("$PURPLE${BOLD}Sortida:$RESET ${intento.outputPriv}")
        if (intento.resuelto){
            println("$PURPLE${BOLD}Resolt:$RESET $BOLD$BOX$GREEN SI $RESET")
        } else println("$PURPLE${BOLD}Resolt:$RESET $BOLD$BOX$RED NO $RESET")
        println("$PURPLE${BOLD}Intents:$RESET ${intento.intentos}")
    }
}
/**
 * Aquesta funció itera a través de la llista de problemas de la base de dades
 * i va afegint els seus números a una llista mutable.
 * Després itera la llista d'intents i quan troba un problema resolt,
 * treu el número corresponent de la llista anterior i la retorna.
 * @param listaProblemas Llista amb tots els problemes
 * @param listaIntentos Llista amb tots els intents
 * @return  Una llista mutable de Ints
 */
fun itinerariAprenentatgeDB( listaProblemas: List<Problema>, listaIntentos: List<Intento>): MutableList<Int> {
    val listaNumeros = mutableListOf<Int>()
    for (problema in listaProblemas){
        listaNumeros.add(problema.numProblema)
    }
    for (intento in listaIntentos) {
        if (intento.resuelto){
            listaNumeros.remove(intento.numProblema)
        }
    }
    return listaNumeros
}
/**
 * Aquesta funció mostra els diferents menus en funció d'un string que rep.
 *
 * @param menuType String amb el tipus de menu
 */
fun showMenu(menuType: String){
    println("═══════════════════════════════════════════")
    when (menuType){
        "Main" -> {
            println("$BLUE${BOLD}1.$RESET Seguir amb l'itinerari del projecte")
            println("$BLUE${BOLD}2.$RESET Llista de problemes")
            println("$BLUE${BOLD}3.$RESET Consultar històric de problemes")
            println("$BLUE${BOLD}4.$RESET Ajuda")
            println("$BLUE${BOLD}5.$RESET Identifiació de professorat")
            println("$BLUE${BOLD}6.$RESET Sortir")
        }
        "Teacher" -> {
            println("$BLUE${BOLD}1.$RESET Afegir nous problemes")
            println("$BLUE${BOLD}2.$RESET Treure report de la feina")
            println("$BLUE${BOLD}0.$RESET Enrere")
        }
    }
    println("═══════════════════════════════════════════")

}
/**
 * Aquesta funció mostra quants problemes han estat resolts, quins, amb quants intents i la puntuació total.
 * Segons els intents que s'han fet, es resta més o menys.
 *
 * En funció dels problemes totals, el nombre de problemes resolts i els seus intents, es calcula una puntuació
 * en format Double que hem rodonejat a un decimal.
 *
 * @param problemasResueltos Llista de pars d'ints que corresponen al número del problema resolt i els seus intents.
 * @param listaProblemas Llista de problemas
 * @see getIntents
 * @see Double.round
 */
fun getPuntuation(problemasResueltos: List<Pair<Int,Int>>, listaProblemas: List<Problema>) {
    val problemasTotales = listaProblemas.size
    var fallos = 0.0
    println("""═══════════════════════════════════════════
        |         S'han resolt $BOLD$BOX$CYAN ${problemasResueltos.size} $RESET de $BOLD$BOX$BLUE $problemasTotales $RESET
        |═════════════════════╦═════════════════════
    """.trimMargin())

    for (i in problemasResueltos){
        var intentColor: String
        var resta: Double
        when (i.second){
            1 ->{
                resta = 1.0
                intentColor = GREEN
            }
            in 2..3 ->{
                resta = 0.1
                intentColor = YELLOW
            }
            in 4..5 ->{
                resta = 0.2
                intentColor = ORANGE
            }
            else ->{
                resta = 0.33
                intentColor = RED
            }
        }
        if (i.second != 1){
            fallos += 1.0*resta
        }
        println("""    Problema: $BOLD$BOX$PINK ${i.first} $RESET    ║     Intents: $BOLD$BOX$intentColor ${i.second} $RESET""".trimMargin())
    }
    val score = ((((problemasResueltos.size-fallos)*100)/problemasTotales)/10).round(1)
    val color: String = if (score < 5){
        RED
    } else GREEN

    println("""═════════════════════╩═════════════════════
        |             Puntuació: $BOLD$BOX$color $score $RESET
        |═══════════════════════════════════════════""".trimMargin())
}

/**
 * Aquesta funció itera a través de la llista d'intents i retorna un Pair amb el número del problema i els intents.
 * Per a que no es repeteixin els problemes, comprova que no estiguin ja als sets que definim i si no ho estan, afegeix
 * el número del problema a un set i un par amb el mateix número i els seus intents a un altre set.
 * Retorna el set de pars.
 *
 * @param listaIntentos Llista amb els intents
 * @return Una llista de Pair de Ints
 * @see getPuntuation
 */
fun getIntents(listaIntentos: List<Intento>): List<Pair<Int, Int>> {
    val problemasResueltos = mutableSetOf<Int>()
    val problemasResueltosWithIntentos = mutableSetOf<Pair<Int, Int>>()
    for (intento in listaIntentos) {
        if (intento.resuelto) {
            if (intento.numProblema !in problemasResueltos) {
                problemasResueltos.add(intento.numProblema)
                problemasResueltosWithIntentos.add(intento.numProblema to intento.intentos)
            }
        }
    }
    return problemasResueltosWithIntentos.sortedBy { it.first }
}
/**
 * Aquesta extension function rep un Int que és el nombre de decimals amb els quals volem obtenir el nostre Double.
 * Multiplica per 10 el multiplicador tantes vegades com el Int que rep i retorna redondejat
 * el valor del double multiplicat pel multiplicador i després, una vegada redondejat,
 * es divideix pel mateix multiplicador.
 * @param decimals Un Int
 * @return Un double amb els mateixos decimals que el Int
 */
fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) {
        multiplier *= 10
    }
    return round(this * multiplier) / multiplier
}
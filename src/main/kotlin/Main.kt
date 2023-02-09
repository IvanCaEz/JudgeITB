import java.io.File
import java.util.*

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
    val problemasResueltos = mutableListOf<ProblemaResuelto>()
    var numProblema = 0
    do {
        println("Problema $box$bold$pink ${numProblema + 1} $reset")
        val currentProblema = ListaProblemas().listaProblemas[numProblema]
        enunciados(currentProblema)
        println("Vols intentar aquest problema?\n$green$bold$box SI $reset $red$bold$box NO $reset")
        val intentar = scanner.nextLine().uppercase()
        if (intentar == "SI"){
            val problema = intentarProblema(currentProblema, problemasResueltos)
            if (problema.first){
                val intents= problema.second
                println("Has acertat amb $intents intents")
            } else println("No has pogut amb el problema")
            numProblema++
        } else if (intentar == "NO") numProblema++
    } while (numProblema != ListaProblemas().listaProblemas.size)

    println("Vols veure l'historial?\n$green$bold$box SI $reset $red$bold$box NO $reset")
    val historial = scanner.nextLine().uppercase()
    if (historial == "SI"){
        for (i in 0 .. problemasResueltos.size){
            println(problemasResueltos[i].enunciado)
            println("$purple${bold}Entrada:$reset ${problemasResueltos[i].inputPriv}")
            println("$purple${bold}Sortida:$reset ${problemasResueltos[i].outputPriv}")
            println("$purple${bold}Resolt:$reset ${problemasResueltos[i].resuelto}")
            println("$purple${bold}Intents:$reset ${problemasResueltos[i].intentos}")
        }
    }
}

fun printHistorial(problema: Int){
}
fun enunciados(currentProblema: Problema){
    var listaEnunciados = File("src/main/kotlin/enunciados/enunciados.txt").readText()
    var listaOutputPub = File("src/main/kotlin/provesPublic/outputsPublic.txt").readText()
    var listaInputPub = File("src/main/kotlin/provesPublic/inputsPublic.txt").readText()

    var listaInputPriv = File("src/main/kotlin/provesPriv/inputPriv.txt").readText()

    println(currentProblema.enunciado)
    println("$cyan${bold}Exemple 1$reset")
    println("${currentProblema.listaInputPub[0]}\n$purple${bold}Entrada:$reset ${currentProblema.listaInputPub[1]}")
    println("${currentProblema.listaOutputPub[0]}\n$purple${bold}Sortida:$reset ${currentProblema.listaOutputPub[1]}")
    println("$cyan${bold}Exemple 2$reset")
    println("$purple${bold}Entrada:$reset ${currentProblema.listaInputPub[2]}")
    println("$purple${bold}Sortida:$reset ${currentProblema.listaOutputPub[2]}")

}
fun intentarProblema(currentProblema: Problema, problemasResueltos: MutableList<ProblemaResuelto>): Pair<Boolean, Int> {
    val random = (0..2).random()
    println("$purple${bold}Entrada:$reset ${currentProblema.inputPriv[random]}")
    println("$purple${bold}Sortida:$reset ???")
    println("Per abandonar el problema entra SORTIR")
    var userAnswer: String
    var resolt = false
    var intents = 0
    do {
        intents++
        userAnswer = scanner.nextLine().uppercase().toString()
        if (userAnswer != currentProblema.outputPriv[random].uppercase() && userAnswer != "SORTIR" ) {
            println("Resposta incorrecta, torna-ho a intentar")
        } else if (userAnswer == currentProblema.outputPriv[random].uppercase()){
            resolt = true
        }
    } while (userAnswer != currentProblema.outputPriv[random].uppercase() && userAnswer != "SORTIR")

    if (userAnswer == "SORTIR") intents = 0

    problemasResueltos.add(ProblemaResuelto(currentProblema.enunciado,
        currentProblema.inputPriv[random], currentProblema.outputPriv[random],resolt, intents ))

    return (userAnswer == currentProblema.outputPriv[random]) to intents

}
fun historialProblemas(currentProblema: Problema, resuelto: Boolean, intentos: Int){

}

/*
var listaEnunciados = File("src/main/kotlin/enunciados/enunciados.txt").readText()
    var listaOutputPub = File("src/main/kotlin/provesPublic/outputsPublic.txt").readText()
    var listaInputPub = File("src/main/kotlin/provesPublic/inputsPublic.txt").readText()

    var listaInputPriv = File("src/main/kotlin/provesPriv/inputPriv.txt").readText()



    var currentProblema = Problema(numProblema, listaEnunciados, listaInputPub, listaOutputPub,listaInputPriv)
    println(currentProblema.enunciado)
    println(currentProblema.printProblemaPublic())
 */
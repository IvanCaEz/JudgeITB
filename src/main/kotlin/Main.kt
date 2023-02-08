import java.io.File
import java.io.BufferedReader
import java.io.FileReader
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.io.path.*


fun main() {

    enunciados()
}

fun enunciados(){
    var listaEnunciados = File("src/main/kotlin/enunciados/enunciados.txt").readText()
    var listaOutputPub = File("src/main/kotlin/provesPublic/outputsPublic.txt").readText()
    var listaInputPub = File("src/main/kotlin/provesPublic/inputsPublic.txt").readText()

    var listaInputPriv = File("src/main/kotlin/provesPriv/inputPriv.txt").readText()



    var numProblema = 0
    var currentProblema = Problema(numProblema, listaEnunciados, listaInputPub, listaOutputPub,listaInputPriv)
    println(currentProblema.enunciado)
    println(currentProblema.printProblemaPublic())
}
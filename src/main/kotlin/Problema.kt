import java.io.File

class Problema (var numProblema: Int, var listaEnunciados: String, var listaInputPub: String,
                var listaOutputPub: String, var listaInputPriv: String) {

    val enunciado = listaEnunciados.split(";")[numProblema]

    fun printProblemaPublic(){
        for (i in 0 until 2){
            println(listaInputPub.split(";")[numProblema+i])
            println(listaOutputPub.split(";")[numProblema+i])
        }
    }
    fun printProblemaPriv(){
            println(listaInputPriv.split(";")[numProblema])
    }

}
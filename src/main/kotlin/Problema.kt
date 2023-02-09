import java.io.File


open class ListaProblemas (){
    val listaProblemas = arrayOf(
        Problema("Escriu un programa que donat dos nombres enters imprimeixi la seqüència entre el primer i el segon."
        , arrayOf("Per l’entrada rebreu dos nombres enters","3 14","14 3"), arrayOf("La sortida ha de ser una seqüència de números del primer fins al segon nombre introduït, imprimint-los tot en una mateixa línia.","3,4,5,6,7,8,9,10,11,12,13,14","14,13,12,11,10,9,8,7,6,5,4,3"),
        "4 19", "4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19"))
}
class Problema (var enunciado: String, var listaInputPub: Array<String>,
                var listaOutputPub: Array<String>, var inputPriv: String, var outputPriv: String) {

}

/*
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
 */
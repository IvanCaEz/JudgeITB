import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.postgresql.util.PSQLException
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException

fun connectToDB(): Connection {
    lateinit var connection: Connection
    try {
        val usuario = "postgres"
        val contrasena = "Program101010Ivan"
        val jdbcUrl = "jdbc:postgresql://localhost:5432/judgeitb"
         connection = DriverManager.getConnection(jdbcUrl, usuario, contrasena)

        println("Conexión a la base de datos: ${connection.isValid(0)}")

    } catch (e: SQLException){
        println("Error: "+e.errorCode + e.message)
    }
    return connection

}

fun main() {
    //Per popular la tabla de la base de dades amb les dades del JSON descomenta la següent linea
    //insertsIntoTables(connectToDB(), getListaProblemasFromJSON())

    //Per veure els problemes des de la tabla de la base de dades
    printProblema(getProblemaFromDB(connectToDB()))

    // Per veure els intents de la base de dades
    val listaIntents = getIntentsFromDB(connectToDB())
    for (intent in listaIntents){
        println(intent)
    }


}


fun getProblemaFromDB(connection: Connection): List<Problema> {
    val listaProblemasFromDB = mutableListOf<Problema>()
    try {
        val numProblemaStatement = connection.createStatement()
        val numProblemSelect = "SELECT * FROM problema"
        val numProblemaResult = numProblemaStatement.executeQuery(numProblemSelect)
        var counter = 0
        while (numProblemaResult.next()){
            counter++
        }
        numProblemaStatement.close()

        for (i in 1..counter){
            val statement = connection.createStatement()
            val select = "SELECT * FROM problema, prova_public, prova_privat " +
                    "WHERE problema.num_problema = $i AND problema.num_problema = prova_public.num_problema AND " +
                    "prova_privat.num_problema = problema.num_problema"
            val result = statement.executeQuery(select)

            // Agafem les dades de la query y "construim" el nostre problema

            while (result.next()){
                val numProblema = result.getInt("num_problema")
                val enunciat = result.getString("enunciat")


                val inputPublic = arrayOf(result.getString("explicacio_input"),
                    result.getString("input_1"),
                    result.getString("input_2"))
                val outputPublic = arrayOf(result.getString("explicacio_output"),
                    result.getString("output_1"),
                    result.getString("output_2"))


                val inputPrivList = mutableListOf<String>()
                val outputPrivList = mutableListOf<String>()

                // Lanzamos otra select para conseguir los inputs y outputs privados de cada problema
                val privStatement = connection.createStatement()
                val selectPriv = "SELECT * FROM prova_privat WHERE " +
                        "num_problema = $numProblema"
                val resultPriv = privStatement.executeQuery(selectPriv)
                // Añadimos cada input y output a una lista
                while (resultPriv.next()){
                    inputPrivList.add(result.getString("input"))
                    outputPrivList.add(result.getString("output"))
                }
                //Creamos el problema con todos los atributos que necesitamos, y para el
                // input y output privados creamos una array iterando las listas que creamos previamente
                val newProblema = Problema(numProblema, enunciat,
                    inputPublic, outputPublic,
                    arrayOf(inputPrivList[0], inputPrivList[1], inputPrivList[2]),
                    arrayOf(outputPrivList[0], outputPrivList[1], outputPrivList[2]))

                if (listaProblemasFromDB.none {it.numProblema == newProblema.numProblema }){
                    listaProblemasFromDB.add(newProblema)
                }
            }
            statement.close()
        }
        // Tanquem connexió a la BD
        connection.close()

    } catch (e : SQLException){
        println("Error: " + e.errorCode + ": " + e.message)
    }
    return listaProblemasFromDB
}

fun printProblema(listaProblemas: List<Problema>){
    // Iterem la llista de problemes per printejar-la
    for (problema in listaProblemas){
        problema.mostrarProblema(problema)
    }
}


fun getIntentsFromDB(connection: Connection): List<Intento> {
    val listaIntentsFromDB = mutableListOf<Intento>()
    try {
            val statement = connection.createStatement()
            val select = "SELECT * FROM intents"
            val result = statement.executeQuery(select)

            // Agafem les dades de la query y "construim" el nostre problema

            while (result.next()){
                val numProblema = result.getInt("num_problema")
                val idPrivat = result.getInt("id_prova_privat")
                // Lanzamos otra select para conseguir los inputs y outputs privados de cada problema
                val privStatement = connection.createStatement()
                val selectPriv = "SELECT * FROM prova_privat" +
                        " WHERE id_prova_privat = $idPrivat AND num_problema = $numProblema"
                val resultPriv = privStatement.executeQuery(selectPriv)
                // Actualizamos el input privado
                var inputPrivat = ""
                while (resultPriv.next()){
                     inputPrivat = resultPriv.getString("input")
                }

                val enunStatement = connection.createStatement()
                val selectEnun = "SELECT enunciat FROM problema" +
                        " WHERE num_problema = $numProblema"
                val resultEnun = enunStatement.executeQuery(selectEnun)
                var enunciat  = ""
                while (resultEnun.next()){
                    enunciat = resultEnun.getString("enunciat")
                }


                // Lanzamos otra select para conseguir las respuestas del usuario
                val respostaStatement = connection.createStatement()
                val selectResposta = "SELECT resposta_usuari FROM intents" +
                        " WHERE id_prova_privat = $idPrivat AND num_problema = $numProblema "
                val resultResposta = respostaStatement.executeQuery(selectResposta)
                val respostes = mutableListOf<String>()
                while (resultResposta.next()) {
                    respostes.add(resultResposta.getString("resposta_usuari"))
                }
                val resuelto = result.getBoolean("resuelto")

                //Creamos el intento con todos los atributos que necesitamos, y miramos que no haya ya
                //un intento con el mismo num de problema y input privado ya que eso significará que es el mismo intento

                val intent = Intento(numProblema, enunciat,
                    inputPrivat, respostes, respostes.size, resuelto)
                if (listaIntentsFromDB.none { it.inputPriv == intent.inputPriv && it.numProblema == intent.numProblema }){
                    listaIntentsFromDB.add(intent)
                }

            }
            statement.close()
        // Tanquem connexió a la BD
        connection.close()

    } catch (e : SQLException){
        println("Error: " + e.errorCode + ": " + e.message)
    }
    return listaIntentsFromDB
}

fun getListaProblemasFromJSON(): MutableList<Problema> {
    val nuevaListaProblemas = mutableListOf<Problema>()
    val listaProblemasJSON =  File("src/main/kotlin/problemes/problemes.json").readLines()
    for (i in 0 .. listaProblemasJSON.lastIndex){
        val problemas = Json.decodeFromString<Problema>(listaProblemasJSON[i])
        val currentProblema = Problema(problemas.numProblema,
            problemas.enunciado,
            problemas.inputPub ,
            problemas.outputPub ,
            problemas.inputPriv ,
            problemas.outputPriv)
        nuevaListaProblemas.add(currentProblema)
    }
    return nuevaListaProblemas
}

fun insertsIntoTables(connection: Connection, nuevaListaProblemas: MutableList<Problema>){
    try {
        for (i in 0..nuevaListaProblemas.lastIndex){

            // Insertar numProblema y enunciando a tabla PROBLEMA
            val instertIntoProblema = "INSERT INTO problema(enunciat) VALUES ( ?)"
            val statementProblema: PreparedStatement = connection.prepareStatement(instertIntoProblema)
            statementProblema.setString(1, nuevaListaProblemas[i].enunciado)
            statementProblema.executeUpdate()
            statementProblema.close()

            // Insertar prova publica
            val instertIntoPublic = "INSERT INTO prova_public(num_problema, id_prova_public, " +
                    "explicacio_input, input_1, input_2, explicacio_output, output_1, output_2) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            val statementPublic: PreparedStatement = connection.prepareStatement(instertIntoPublic)
            statementPublic.setInt(1, nuevaListaProblemas[i].numProblema)
            statementPublic.setInt(2, nuevaListaProblemas[i].numProblema)
            statementPublic.setString(3, nuevaListaProblemas[i].inputPub[0])
            statementPublic.setString(4, nuevaListaProblemas[i].inputPub[1])
            statementPublic.setString(5, nuevaListaProblemas[i].inputPub[2])
            statementPublic.setString(6, nuevaListaProblemas[i].outputPub[0])
            statementPublic.setString(7, nuevaListaProblemas[i].outputPub[1])
            statementPublic.setString(8, nuevaListaProblemas[i].outputPub[2])
            statementPublic.executeUpdate()
            statementPublic.close()

            //inserir prova Privada
            val instertIntoPriv = "INSERT INTO prova_privat(num_problema, " +
                    " input, output) VALUES (?, ?, ?)"
            val statementPriv: PreparedStatement = connection.prepareStatement(instertIntoPriv)

            for (j in 0..2){
                statementPriv.setInt(1, nuevaListaProblemas[i].numProblema)
                statementPriv.setString(2, nuevaListaProblemas[i].inputPriv[j])
                statementPriv.setString(3, nuevaListaProblemas[i].outputPriv[j])
                statementPriv.executeUpdate()
            }

            statementPriv.close()

        }

        connection.close()
        println("Conexión a la base de datos finalizada")

    } catch (e : SQLException){
        println("Error " + e.errorCode + ": " + e.message)
    }

}

fun insertProblema(connection: Connection, newProblema: Problema){
    try {
            // Insertar numProblema y enunciando a tabla PROBLEMA
            val instertIntoProblema = "INSERT INTO problema(enunciat) VALUES ( ?)"
            val statementProblema: PreparedStatement = connection.prepareStatement(instertIntoProblema)
            statementProblema.setString(1, newProblema.enunciado)
            statementProblema.executeUpdate()
            statementProblema.close()

            // Insertar prova publica
            val instertIntoPublic = "INSERT INTO prova_public(num_problema, id_prova_public, " +
                    "explicacio_input, input_1, input_2, explicacio_output, output_1, output_2) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            val statementPublic: PreparedStatement = connection.prepareStatement(instertIntoPublic)
            statementPublic.setInt(1, newProblema.numProblema)
            statementPublic.setInt(2, newProblema.numProblema)
            statementPublic.setString(3, newProblema.inputPub[0])
            statementPublic.setString(4, newProblema.inputPub[1])
            statementPublic.setString(5, newProblema.inputPub[2])
            statementPublic.setString(6, newProblema.outputPub[0])
            statementPublic.setString(7, newProblema.outputPub[1])
            statementPublic.setString(8, newProblema.outputPub[2])
            statementPublic.executeUpdate()
            statementPublic.close()

            //inserir prova Privada
            val instertIntoPriv = "INSERT INTO prova_privat(num_problema, " +
                    " input, output) VALUES (?, ?, ?)"
            val statementPriv: PreparedStatement = connection.prepareStatement(instertIntoPriv)

            for (j in 0..2){
                statementPriv.setInt(1, newProblema.numProblema)
                statementPriv.setString(2, newProblema.inputPriv[j])
                statementPriv.setString(3, newProblema.outputPriv[j])
                statementPriv.executeUpdate()
            }

            statementPriv.close()

        connection.close()
        println("Conexión a la base de datos finalizada")

    } catch (e : SQLException){
        println("Error " + e.errorCode + ": " + e.message)
    }

}




import org.junit.jupiter.api.Assertions.*
import org.junit.Test


internal class MainKtTest{
    // Double.round
    @Test
    fun roundToOneDecimal(){
        assertEquals(3.5, 3.4666.round(1))
    }
    @Test
    fun roundTo3Decimals(){
        assertEquals(7.128, 7.127995421.round(3) )
    }
    @Test
    fun roundTo5Decimals(){
        assertEquals(2.54124, 2.541235254218.round(5) )
    }
    // getIntents
    @Test
    fun getIntentsReturnsAListOfPairsSortedByProblemNumber(){
        assertEquals(listOf(1 to 1, 3 to 4), getIntents(listaIntentos = listOf("{\"numProblema\":3,\"enunciado\":\"Feu un programa que rebi una temperatura en graus Celsius i la converteixi en graus Fahrenheit\",\"inputPriv\":\"-7.4\",\"outputPriv\":[\"19.4\",\"18.4\",\"18.62\",\"18.68\"],\"intentos\":4,\"resuelto\":true}\n",
            "{\"numProblema\":1,\"enunciado\":\"Fes un programa que ens ajudi a calcular les dimensions d'una habitació.\\nLlegeix l'amplada i la llargada en metres (enters) i mostra'n l'àrea.\",\"inputPriv\":\"24.5 11.3\",\"outputPriv\":[\"276.85\"],\"intentos\":1,\"resuelto\":true}\n")))
    }


}
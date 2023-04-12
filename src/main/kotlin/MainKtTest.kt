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



}
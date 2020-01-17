package utwente.jjw.meijer.hll;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HyperLogLogTest {

    @Test 
    public void testAddfunction(){
        int numberOfBits = 6;
        HLLCounter testCounter = new HLLCounter(numberOfBits);

        // register index is equal to 9 
        // leading zeroes is equal to 4
        long testHash = 0b0010010000100000000000000000000000000000000000000000000000000000L; 
        testCounter.add(testHash);

        assertEquals(5, testCounter.getRegister(9)); // test leading zeroes is equal to 4+1.

        // register index is equal to 9
        // leading zeroes is equal to 5
        testHash = 0b0010010000010000000000000000000000000000000000000000000000000000L; 
        testCounter.add(testHash);

        assertEquals(6, testCounter.getRegister(9)); // test leading zeroes is equal to 5+1.
    }
}
package utwente.jjw.meijer.hll;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
/**
 * All tests assume the google murmurhash-32 function.
 */
public class HyperLogLogTest {


    @Test 
    public void testAddfunction(){
        final int NUMBER_OF_BITS = 5;
        HLLCounter testCounter = new HLLCounter(NUMBER_OF_BITS);

        final int testItem1 = 27;
        /*
        The hash of 27 is equal to 00000110111100100010100111011101.
        This hash has thus an index of 0, and 0 leading zeroes.
        */

        final int testItem2 = 38;
        /*
        The hash of 38 is equal to 00000010011010000000011000110011.
        This hash has thus an index of 0, and 1 leading zero.
        */

        testCounter.add(testItem1);
        assertEquals(1, testCounter.getRegister(0)); // test register is equal to the number of leading zeroes + 1.

        testCounter.add(testItem2);
        assertEquals(2, testCounter.getRegister(0)); // test register is equal to the number of leading zeroes + 1.
    }

    @Test
    public void testCopyFunction()
    {   
        final int NUMBER_OF_BITS = 5;
        
        final int testItem1 = 27;
        /*
        The hash of 27 is equal to 00000110111100100010100111011101.
        This hash has thus an index of 0, and 0 leading zeroes.
        */

        final int testItem2 = 38;
        /*
        The hash of 38 is equal to 00000010011010000000011000110011.
        This hash has thus an index of 0, and 1 leading zero.
        */
        
        
        HLLCounter counter1 = new HLLCounter(NUMBER_OF_BITS);
        counter1.add(testItem1);
        assertEquals(1, counter1.getRegister(0));

    
        HLLCounter copiedCounter = new HLLCounter(counter1);
        assertEquals(1, copiedCounter.getRegister(0));

        copiedCounter.add(testItem2);
        assertEquals(2, copiedCounter.getRegister(0)); // register should have been overwritten by bigger hash.
        assertEquals(1, counter1.getRegister(0));   // this should not have influenced the other counter.
    }

    @Test
    public void testInitialization(){
        int numberOfBits = 7;
        HLLCounter testCounter = new HLLCounter(numberOfBits);

        int expectedP = (int) Math.pow(2,numberOfBits);
        assertEquals(expectedP, testCounter.getNumberOfRegisters());
    }

    @Test 
    public void testUnionFunction(){
        final int NUMBER_OF_BITS = 5; 
        final HLLCounter counter1 = new HLLCounter(NUMBER_OF_BITS);
        final HLLCounter counter2 = new HLLCounter(NUMBER_OF_BITS);

        final int testItem1 = 27;
        /*
        The hash of 27 is equal to 00000110111100100010100111011101.
        This hash has thus an index of 0, and 0 leading zeroes.
        */

        final int testItem2 = 38;
        /*
        The hash of 38 is equal to 00000010011010000000011000110011.
        This hash has thus an index of 0, and 1 leading zero.
        */

        boolean changed = counter1.add(testItem1);
        assertTrue("Should have returned changed as true.", changed);

        counter2.add(testItem2);

        assertEquals(1, counter1.getRegister(0));
        assertEquals(2, counter2.getRegister(0));

        boolean hasChanged = counter1.union(counter2);
        assertTrue("Should have changed no union", hasChanged);
        assertEquals(2, counter1.getRegister(0));

    }

    public static void main(String[] args){
        for (int i = 0; i < 100; i++){
            int hash = NodeHasher.hash(i);
            String hashBinaryRepresentation = HLLCounter.intToBinaryRepresentation(hash);
            System.out.printf("Hash of int: %d      is: %s\n", i, hashBinaryRepresentation);
        }
        System.out.println(HLLCounter.intToBinaryRepresentation(NodeHasher.hash(27)));
        System.out.println(HLLCounter.intToBinaryRepresentation(NodeHasher.hash(38)));

    }
}
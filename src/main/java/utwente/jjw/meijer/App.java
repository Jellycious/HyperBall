package utwente.jjw.meijer;

import utwente.jjw.meijer.hll.HLLCounter;

/**
 * Hello world!
 *
 */
public class App 
{
    
    public static void main( String[] args )
    {
        int numberOfBits = 6;
        int pLong = (int) Math.pow(2, numberOfBits);
        System.out.printf("Number of registers %d. This is for %d bits\n", pLong, numberOfBits);

        HLLCounter counter = new HLLCounter(numberOfBits);
        System.out.printf("Counter size %d\n", counter.getNumberOfRegisters());

        long hash = 0b1010100000111111111111111111111111111111111111111111111111111111L; 
        System.out.printf("Hash: %s\n", HLLCounter.longToBinaryRepresentation(hash));

        System.out.println(counter.getRegisterIndex(hash));
        System.out.println(counter.getLeadingZeroes(hash));

    }
}

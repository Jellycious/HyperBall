package utwente.jjw.meijer.hll;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * HyperLogLog counter. Used for the HyperBall algorithm. HyperLogLog is capable
 * of counting large cardinalities while keeping its memory space to a minimum.
 * 
 * p is number of registers used. Note that hashes should be 64-bit values.
 * Hence the max number of leading zeroes is equal to 64 - b
 */
public class HLLCounter {


    private final byte[] counter; 
    private final int b;
    private final double a_m; //constant a_m for fixing multiplicative bias. 

    /**
     * Constructor: Calculates p from given number of bits. 
     * @param b Number of bits used for register identification.
     */
    public HLLCounter(int b) 
    {
        this.b = b;
        int p = getP();
        this.a_m = calculateAm(p); // calculate a_m constant. 
        this.counter = new byte[p]; // initialize byte array.
    }

    public void add(long hash){
        int index = getRegisterIndex(hash);
        int leadingzeroes = getLeadingZeroes(hash);
        byte val = (byte) (leadingzeroes + 1);
        this.counter[index] = (byte) Math.max(this.counter[index], val);
    }
    
    /**
     * Returns the value of p, based on the number of bits specified for indexing registers.
     * According to p=2^b
     * @return p
     */
    private int getP(){
        return (int) (Math.pow(2, b));
    }

    /**
     * Calculates a_m as specified by the paper HLL, analysis of near optimal cardinality algorithm. 
     * @param p the number of registers.
     * @return the constant a_m
     */
    public double calculateAm(int p){
        switch (p){
            case 16: 
                return 0.673;
            case 32:
                return 0.697;
            case 64:
                return 0.709;
            default:
                double denom = 1.079 / p;
                denom = denom + 1;
                return 0.7213 / denom;
        }
    }

    /**
     * Gets register value.
     * @param index of the register
     * @return register value at index.
     */
    public byte getRegister(int j){
        return counter[j];
    }

    /**
     * Returns p, the number of registers used by the counter.
     * @return Number of registers.
     */
    public int getNumberOfRegisters(){
        return getP();
    }

    /**
     * Calculates the result of the indicator function.
     * According to the formula given in the paper: in-core computation of large cardinalities.
     * @return Indicator value of the counter.
     */
    private BigDecimal getZ() 
    {

        //calculate scale of the reciprocal, this is equal to log10(2^p)
        int maxScale = (int) (Math.log10(Math.pow(2, getP())) + 4); // add 4 to stay safe.
        System.out.printf("Max scale of bigdecimal: %d\n", maxScale);


        BigDecimal registersum = new BigDecimal(0);
        BigDecimal one = new BigDecimal(1);
        BigDecimal two = new BigDecimal(2);

        // Iterate through registers. To calculate Z 
        for (int i = 0; i < counter.length; i++){
            int regval = this.counter[i];

            BigDecimal ival;
            ival = two.pow(regval);
            ival = one.divide(ival, maxScale, RoundingMode.HALF_UP);

            registersum = registersum.add(ival);
        }

        BigDecimal result;
        result = one.divide(registersum, maxScale, RoundingMode.HALF_UP);
        return result;
    }

    /**
     * Returns the estimated cardinality.
     * @return E the estimated cardinality of the counter. 
     */
    public long getSize()
    {
        BigDecimal Z = getZ();  // Z 
        System.out.printf("Z: %f\n", Z);
        BigDecimal a_m = new BigDecimal(this.a_m); // constant: a_m

        BigDecimal psqr = new BigDecimal(getP()); // number of registers: p
        psqr = psqr.pow(2); // p^2
        
        // Calculate a_m * p^2 * Z 
        BigDecimal result;
        result = a_m;
        result = result.multiply(psqr);
        result = result.multiply(Z); 

        return result.longValue(); // return as long. 
    }


    /**
     * Returns the counter index for the hash. The first numberOfBits bits are used to determine the index.
     * @param hash
     * @return Index of register associated with the first bits of the hash.
     */
    public int getRegisterIndex(long hash)
    {
        return (int) (hash >>> (64 - b)); //unsigned right shift. 
    }

    /**
     * Gets leading zeroes of the hash. It removes the bits, which are used for the index.
     * @param hash hash to calculate leading zeroes.
     * @return number of leading zeroes.
     */
    public byte getLeadingZeroes(long hash)
    {
        long importantBits = hash << b; 
        return (byte) Long.numberOfLeadingZeros(importantBits);
    }


    /**
     * Makes a union of two counters. 
     * NOTE: This changes this counter and does not change the argument counter.
     * @param other The HLLCounter to union with.
     * @return This HLLCounter, which has been unioned with other. 
     */
    public HLLCounter union(HLLCounter other)
    {
        for (int i = 0; i < counter.length; i++){
            counter[i] = (byte) Math.max(counter[i], other.getRegister(i));
        }
        return this;
    }


    /**
     * Creates a binary representation string from a long, with proper padding.
     * @param bitmask long to convert to padded binary representation.
     * @return String of binary representation.
     */
    public static String longToBinaryRepresentation(long binary){
        return String.format("%64s", Long.toBinaryString(binary)).replace(' ', '0');
    }
}


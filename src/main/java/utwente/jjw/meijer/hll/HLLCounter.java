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

    /**
     * Constructor: Calculates p from given number of bits. 
     * @param b Number of bits used for register identification.
     */
    public HLLCounter(int b) 
    {
        this.b = b;
        int p = (int) Math.pow(2, b); // Number of Registers is equal to 2^bits
        this.counter = new byte[p]; // initialize byte array.
    }

    /**
     * Creates a new counter from another counter.
     * @param toCopy HLLCounter which you want to copy.
     */
    public HLLCounter(HLLCounter toCopy)
    {
        this.b = toCopy.b;
        this.counter = toCopy.counter.clone();
    }

    /**
     * Adds a new hash to the counter. 
     * @param item item to add to the counter.
     * @return Whether the counter has changed.
     */
    public boolean add(int item){
        int hash = NodeHasher.hash(item);
        int index = getRegisterIndex(hash);
        int leadingzeroes = getLeadingZeroes(hash);
        byte val = (byte) (leadingzeroes + 1);
        if (val > this.counter[index]){

            this.counter[index] = val;
            return true;
        }

        return false;
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
        return this.counter.length;
    }

    /**
     * Calculates the result of the indicator function.
     * According to the formula given in the paper: in-core computation of large cardinalities.
     * @return Indicator value of the counter.
     */
    private BigDecimal getZ() 
    {

        //calculate scale of the reciprocal, this is equal to log10(2^p)
        int maxScale = (int) (Math.log10(Math.pow(2, getNumberOfRegisters())) + 4); // add 4 to stay safe.

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
    private double getE()
    {
        BigDecimal Z = getZ();  // Z 
        // a_m 
        double da_m = calculateAm(getNumberOfRegisters());
        BigDecimal a_m = new BigDecimal(da_m); // constant: a_m

        BigDecimal psqr = new BigDecimal(getNumberOfRegisters()); // number of registers: p
        psqr = psqr.pow(2); // p^2
        
        // Calculate a_m * p^2 * Z 
        BigDecimal result;
        result = a_m;
        result = result.multiply(psqr);
        result = result.multiply(Z); 

        return result.doubleValue(); // return as long. 
    }

    /** 
     * Returns the size of the counter. Including corrections on lower and upper bound.
     * These corrections are explained in discussion of HyperLogLog analysis of near optimal cardinality estimation algorithm. 
     * @return The size of the register.
     */
    public long getSize()
    {
        // Lower bound check.
        double E = getE(); // normal estimator.

        double lowerBound = (5.0 / 2) * getNumberOfRegisters();
        double upperBound = (1.0 / 30) * (Math.pow(2,32)); 
        // range corrections
        if (E < lowerBound){            // Small range correction

            double V = (double) emptyRegisters();

            if (V==0) return (long) E;  // prevents division by 0
            
            double m = (double) getNumberOfRegisters();
            double eStar = m * Math.log(m / V);
            return (long) eStar;

        } else if (E < upperBound) {    // No range correction        
            return (long) E;

        } else {                        // Large range correction
            double maxVal = Math.pow(2,32);
            double eStar = -1.0 * maxVal * Math.log(1 - (E/maxVal));
            return (long) eStar;
        }        
    }


    /**
     * Returns the counter index for the hash. The first numberOfBits bits are used to determine the index.
     * @param hash
     * @return Index of register associated with the first bits of the hash.
     */
    public int getRegisterIndex(int hash)
    {
        return (int) (hash >>> (32 - b)); //unsigned right shift. 
    }

    /**
     * Gets leading zeroes of the hash. It removes the bits, which are used for the index.
     * @param hash hash to calculate leading zeroes.
     * @return number of leading zeroes.
     */
    public byte getLeadingZeroes(int hash)
    {
        int importantBits = hash << b; 
        return (byte) Integer.numberOfLeadingZeros(importantBits);
    }

    /**
     * @return Number of empty registers.
     */
    private int emptyRegisters(){
        int emptyBuckets = 0;
        for (int i = 0; i < counter.length; i++){
            if (counter[i]==0) emptyBuckets++;
        }
        return emptyBuckets;
    }


    /**
     * Makes a union of two counters. 
     * NOTE: This changes this counter and does not change the argument counter.
     * Complexity: O(m)
     * @param other The HLLCounter to union with.
     * @return Whether this counter has been changed.
     */
    public boolean union(HLLCounter other)
    {
        boolean changed = false;

        for (int i = 0; i < counter.length; i++){
            byte otherVal = other.getRegister(i);
            byte val = this.counter[i];
            if (otherVal > val){
                this.counter[i] = otherVal;
                changed = true;
            }
        }

        return changed;
    }


    /**
     * Creates a binary representation string from an integer, with proper padding.
     * @param binary int to convert to padded binary representation.
     * @return String of binary representation.
     */
    public static String intToBinaryRepresentation(int binary){
        return String.format("%32s", Integer.toBinaryString(binary)).replace(' ', '0');
    }



    public static void main(String[] args){
        HLLCounter testCounter = new HLLCounter(5);
        System.out.println(testCounter.getE());

        double lowerBound = (5.0 / 2) * testCounter.getNumberOfRegisters();
        System.out.printf("lower bound: %f\n", lowerBound);
        System.out.printf("size: %d\n", testCounter.getSize());

        double V = (double) testCounter.emptyRegisters();
        double m = (double) testCounter.getNumberOfRegisters();
        double eStar = m * Math.log(m / V);
        System.out.printf("V: %f, m: %f, eStar: %f", V, m, eStar);
    }
}


package utwente.jjw.meijer.hll;


import com.google.common.hash.Hashing;
import com.google.common.hash.HashCode;;


public class NodeHasher
{
    /**
     * Hash an int using Google's MurmurHash function, which has an approximate uniform distribution.
     */
    public static int hash(int toHash)
    {
        HashCode hash = Hashing.murmur3_32().hashInt(toHash);
        return hash.asInt(); 
    }
}


package utwente.jjw.meijer;


import com.google.common.hash.Hashing;
import com.google.common.hash.HashCode;;


public class Hash
{
    /**
     * Hash a long number using Google's MurmurHash function, which has an approximate uniform distribution.
     */
    public static long hash(long toHash)
    {
        HashCode hash = Hashing.murmur3_128().hashLong(toHash);
        return hash.asLong(); // Returns the first 8 bytes of the 16-byte hash. 
    }
}


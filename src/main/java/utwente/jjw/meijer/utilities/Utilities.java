package utwente.jjw.meijer.utilities;


public class Utilities {

    public static void printMemoryUsage()
    {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc(); // run garbage collector

        final long MEGABYTE = 1024L * 1024L;

        long memory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        System.out.printf("Memory use at this moment (MegaByte): %d\n", (memory - freeMemory) / MEGABYTE);
    }

    public static long getMemoryUsage()
    {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc(); // run garbage collector

        final long MEGABYTE = 1024L * 1024L;

        long memory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        return (long) (memory - freeMemory) / MEGABYTE;
    }

}


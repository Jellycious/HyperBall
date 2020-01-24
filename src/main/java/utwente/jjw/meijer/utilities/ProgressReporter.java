package utwente.jjw.meijer.utilities;

/**
 * Thread that allows us to watch a progress.
 */
public class ProgressReporter extends Thread {
    
    private boolean done = false;

    private final boolean reportMemory;
    private final Progress toReport;
    private final int intervalSeconds;

    private long start;

    public ProgressReporter(Progress toReport, int intervalSeconds){
        this.toReport = toReport;
        this.intervalSeconds = intervalSeconds;
        this.reportMemory = false;
    }

    public ProgressReporter(Progress toReport, int intervalSeconds, boolean reportMemory){
        this.toReport = toReport;
        this.intervalSeconds = intervalSeconds;
        this.reportMemory = reportMemory;
    }

    public void run() {
        this.start = System.currentTimeMillis();
        while (!done){
            double progress = toReport.getProgress();
            double diffMinutes = (double) (System.currentTimeMillis() - start) / (1000);    // seconds
            diffMinutes = diffMinutes / 60;                                                 // minutes
            double minutesToGo = (100 - progress) / progress;
            minutesToGo = minutesToGo * diffMinutes;

            System.out.printf("Progress: %.2f%%\texpected minutes to go: %.0f\n", progress, minutesToGo);

            if (reportMemory) Utilities.printMemoryUsage();
            
            try {
                Thread.sleep(intervalSeconds * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                done = true;
            }

        }
    }

    public void finished(){
        this.done = true;
    }

}
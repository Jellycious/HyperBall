package utwente.jjw.meijer.utilities;

/**
 * Thread that allows us to watch a progress.
 */
public class ProgressReporter extends Thread {
    
    private boolean done = false;

    private final boolean reportMemory;
    private final Progress toReport;
    private final int intervalSeconds;

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
        while (!done){

            toReport.printProgress();
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
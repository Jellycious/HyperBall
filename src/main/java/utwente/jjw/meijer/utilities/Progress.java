package utwente.jjw.meijer.utilities;


/**
 * Allows for reporting on progress.
 * Not truly Thread safe, but for current implementation that doesn't really matter.
 */
public class Progress {

    private double progress;

    public Progress(){
        this.progress = 0;
    }

    public void updateProgress(double progress){
        this.progress = progress;
    }

    public void printProgress(){
        System.out.printf("Progress %10f\n", this.progress);
    }
}


package labwu.py;

import labwu.thread.Callback;
import labwu.thread.RFRunnable2;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RFDistance{
    public static double[][][] rfDists;
    public static double[][][] rfDistance(String[][] newicks, int cpuCount) throws InterruptedException {
        int length = newicks.length;
        rfDists = new double[length][][];
        Callback callback = (index, rfDist) -> rfDists[index] = rfDist;
        ExecutorService executorService = Executors.newFixedThreadPool(cpuCount);
        for (int i = 0; i< length; i++){
            RFRunnable2 task = new RFRunnable2(i, newicks[i], callback);
            executorService.execute(task);
        }
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        return rfDists;
    }


    public static void main(String[] args) throws InterruptedException {
/*        String[][] newicks = new String[2][2];
        newicks[0][0] = "((1,2),(3,4));";
        newicks[0][1] = "((1,3),(2,4));";
        newicks[1][0] = "((1,2),(3,4));";
        newicks[1][1] = "((1,3),(2,4));";
        double[][][] result = RFDistance.rfDistance(newicks, 10);
        for (int x=0; x<result.length; x++) {
            System.out.println();
            System.out.println("----------------");
            for (int y = 0; y < result[0].length; y++) {
                System.out.println();
                for (int z = 0; z < result[0].length; z++) {
                    System.out.print(result[x][y][z] + " ");
                }
            }
        }*/
    }
}

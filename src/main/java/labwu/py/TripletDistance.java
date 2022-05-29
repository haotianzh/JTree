package labwu.py;

import labwu.thread.Callback;
import labwu.thread.TripletDistanceRunnable;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TripletDistance{
    public static double[][][] tripletDists;
    public static double[][][] tripletDistance(String[][] newicks, int cpuCount) throws InterruptedException {
        int length = newicks.length;
        tripletDists = new double[length][][];
        Callback callback = (index, tripletDist) -> tripletDists[index] = tripletDist;
        ExecutorService executorService = Executors.newFixedThreadPool(cpuCount);
        for (int i = 0; i< length; i++){
            TripletDistanceRunnable task = new TripletDistanceRunnable(i, newicks[i], callback);
            executorService.execute(task);
        }
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        return tripletDists;
    }


    public static void main(String[] args) throws Exception {
//        String[][] newicks = new String[2][2];
//        newicks[0][0] = "((1,2),(3,4));";
//        newicks[0][1] = "((1,3),(2,4));";
//        newicks[1][0] = "((1,2),(3,4));";
//        newicks[1][1] = "((1,3),(2,4));";

        String[][] newicks = new String[1][50];
        BufferedReader reader = new BufferedReader(new FileReader("data/1.txt.trees"));
        String line = "";
        int i = 0;
        while ((line=reader.readLine())!=null){
            line = line.trim();
            newicks[0][i++] = line.split("\t")[1] + ";";
        }
        long start = System.currentTimeMillis();
        double[][][] result = TripletDistance.tripletDistance(newicks, 10);
        long end = System.currentTimeMillis();
        System.out.println("running time: " + (end-start));
        for (int x=0; x<result.length; x++) {
            System.out.println();
            System.out.println("----------------");
            for (int y = 0; y < result[0].length; y++) {
                System.out.println();
                for (int z = 0; z < result[0].length; z++) {
                    System.out.print(result[x][y][z] + " ");
                }
            }
        }
    }
}
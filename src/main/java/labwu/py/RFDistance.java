package labwu.py;

import labwu.thread.Callback;
import labwu.thread.RFRunnable2;
import labwu.thread.RFRunnable4BPWin;
import labwu.thread.RFRunnable4Locus;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
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

    public static double[][][] rfDistanceWindow(String[][] newicks, int windowSize, int cpuCount, String which) throws InterruptedException{
        double[][][] rfDists = new double[newicks.length][][];
        ExecutorService executorService = Executors.newFixedThreadPool(cpuCount);
        Callback callback = (index, rfDist) -> rfDists[index] = rfDist;
        for (int i = 0; i< newicks.length; i++){
            RFRunnable4Locus task = new RFRunnable4Locus(i, newicks[i], windowSize, callback, which);
            executorService.execute(task);
        }
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        return rfDists;
    }


    public static double[][][] rfDistanceBPWindow(String[][] newicks, int[][] positions, int windowSize, int stepSize, int cpuCount) throws InterruptedException {
        double[][][] rfDists = new double[newicks.length][][];
        ExecutorService executorService = Executors.newFixedThreadPool(cpuCount);
        Callback callback = (index, rfDist) -> rfDists[index] = rfDist;
        for (int i = 0; i< newicks.length; i++){
            RFRunnable4BPWin task = new RFRunnable4BPWin(i, newicks[i], positions[i], windowSize, stepSize, callback, false);
            executorService.execute(task);
        }
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        return rfDists;
    }


    public static void main(String[] args) throws Exception {
        int numSamples = 1;
        String[][] newicks = new String[numSamples][];
        int[][] positions = new int[numSamples][];
        ArrayList<String> newick = new ArrayList<>();
        ArrayList<Integer> position = new ArrayList<>();
        String fileName = "data/2.txt.trees";
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line=reader.readLine())!=null){
            String treeStr = line.trim().split("\t")[1]+";";
            String pos = line.trim().split("\t")[0];
            position.add(Integer.valueOf(pos));
            newick.add(treeStr);
        }
        for(int i=0; i<numSamples; i++){
            newicks[i] = newick.toArray(new String[0]);
            positions[i] = position.stream().mapToInt(k -> k).toArray();
        }
        long start;
        long end;
        start = System.currentTimeMillis();
        double[][][] result = RFDistance.rfDistanceBPWindow(newicks, positions, 100, 2, 10);

        // for (int i =0; i < result[0].length; i++){
        //     System.out.print(result[][i] + " ");
        // }
        end = System.currentTimeMillis();
        System.out.println("total runtime: "+ (end-start) + " ms"); 
        // System.out.println(result.length + " " + result[0].length + " " + result[0][0].length);
        // start = System.currentTimeMillis();
        // double[][][] result2 = RFDistance.rfDistanceWindow(newicks, 50, 20, "slow");
        // end = System.currentTimeMillis();
        // System.out.println("total runtime: "+ (end-start) + " ms");
        System.out.println(result.length + " " + result[0].length + " " + result[0][0].length);
        // boolean equal = true;
        // for (int x=0; x<result.length; x++) {
        //     // System.out.println();
        //     // System.out.println("----------------");
        //     for (int y = 0; y < result[0].length; y++) {
        //         // System.out.println();
        //         for (int z = 0; z < result[0][0].length; z++) {
        //             // System.out.print(result[x][y][z] + " ");
        //             if (result[x][y][z] != result2[x][y][z])
        //                 equal = false;
        //         }
        //     }
        // }
        // System.out.println("equal? " + equal);
    }
}

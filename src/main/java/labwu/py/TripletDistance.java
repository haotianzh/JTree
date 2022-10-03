package labwu.py;

import labwu.object.Tree;
import labwu.thread.Callback;
import labwu.thread.Callback2;
import labwu.thread.TripletDistanceRunnable;
import labwu.thread.TripletRunnable4Locus;
import labwu.util.TreeUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TripletDistance{
    public static double[][][] tripletDistance(String[][] newicks, int cpuCount) throws InterruptedException {
        double[][][] tripletDists;
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

//    public static double[][] tripletDistsLoci;
    public static double[][] tripletDistsLoci(String[] newicks, int windowSize, int stepSize, int cpuCount) throws InterruptedException {
        double[][] tripletDistsLoci;
        int length = newicks.length;
        tripletDistsLoci = new double[length][windowSize];
        Callback2 callback = new Callback2() {
            @Override
            public void onFinish(int index, double[] values) {
                tripletDistsLoci[index] = values;
            }
        };
        // read data
        Tree<Integer>[] trees = new Tree[length];
        for (int i = 0; i < length; i++){
            Tree<Integer> tree = TreeUtils.readFromNewick(newicks[i]);
            trees[i] = tree;
        }
        // calculate LCA
        int[][][] lca = new int[trees.length][][];
        for (int i=0; i<trees.length; i++){
            try {
                lca[i] = TreeUtils.pairwiseLCA(trees[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // execute parallel computing
        ExecutorService executorService = Executors.newFixedThreadPool(cpuCount);
        for (int i = 0; i< length; i++){
            TripletRunnable4Locus task = new TripletRunnable4Locus(i, windowSize, lca, callback);
            executorService.execute(task);
        }
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        return tripletDistsLoci;
    }


    public static double[][][] tripletDistsOverlap(String[] newicks, int windowSize, int stepSize, int cpuCount) throws InterruptedException {
        double[][] tripletDistsLoci = tripletDistsLoci(newicks, windowSize, stepSize, cpuCount);
        int length = newicks.length;
        int numWins = (length - windowSize + 1) % stepSize == 0? (length - windowSize + 1) / stepSize : (length - windowSize + 1) / stepSize + 1;
        double[][][] tripletDists = new double[numWins][windowSize][windowSize];
        int count = 0;
        for (int i=0; i<length-windowSize+1; i+=stepSize){
            for (int p=0; p<windowSize; p++){
                for (int q=p+1; q<windowSize; q++){
                    tripletDists[count][p][q] = tripletDistsLoci[i+p][q-p];
                    tripletDists[count][q][p] = tripletDistsLoci[i+p][q-p];
                }
            }
            count ++;
        }
        return tripletDists;
    }



    public static void main(String[] args) throws Exception {
//        String[][] newicks = new String[2][2];
//        newicks[0][0] = "((1,2),(3,4));";
//        newicks[0][1] = "((1,3),(2,4));";
//        newicks[1][0] = "((1,2),(3,4));";
//        newicks[1][1] = "((1,3),(2,4));";
//        String[] newicks = new String[4];
//        newicks[0] = "((1,2),(3,4));";
//        newicks[1] = "((1,3),(2,4));";
//        newicks[2] = "((1,2),(3,4));";
//        newicks[3] = "((1,3),(2,4));";
        System.out.println(String.format("本计算机的核数：%d", Runtime.getRuntime().availableProcessors()));

        String[] newicks = new String[65];
        BufferedReader reader = new BufferedReader(new FileReader("data/1.txt.trees"));
        String line = "";
        int i = 0;
        while ((line=reader.readLine())!=null){
            line = line.trim();
            newicks[i++] = line.split("\t")[1] + ";";
        }
        long start = System.currentTimeMillis();
//        double[][] result = TripletDistance.tripletDistsLoci(newicks, 3, 1, 10);
        double[][][] result1 = TripletDistance.tripletDistsOverlap(newicks, 50, 1, 16);
        long end = System.currentTimeMillis();
        System.out.println("running time: " + (end-start));

//        start = System.currentTimeMillis();
//        String[][] nn = new String[1][];
//        nn[0] = newicks;
//        double[][][] result2 = TripletDistance.tripletDistance(nn, 10);
//        end = System.currentTimeMillis();
//
//        System.out.println("running time: " + (end-start));

        double[][] result = result1[15];
        for (int x=0; x<result.length; x++) {
            System.out.println();
            for (int y = 0; y < result[0].length; y++) {
                System.out.print(result[x][y] + " ");
            }
        }
    }
}
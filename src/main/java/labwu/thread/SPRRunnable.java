package labwu.thread;

import labwu.object.Tree;
import labwu.util.TreeUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SPRRunnable implements Runnable {
    private String[] newicks;
    private int index;
    private Callback callback;
    private String method;
    private String[] methods = {"mean", "min", "max"};
    private HashSet<String> availableMethods = new HashSet<>(Arrays.asList(methods));
    public SPRRunnable(int index, String[] newicks, String method, Callback callback) throws Exception {
        this.newicks = newicks;
        this.index = index;
        this.callback = callback;
        if (availableMethods.contains(method))
            this.method = method;
        else
            throw new Exception("no such method");
    }

    public double distance(Tree tree1, Tree tree2) throws Exception {
        Tree[] trees = TreeUtils.contractTrees(tree1, tree2);
        tree1 = trees[0];
        tree2 = trees[1];
        double dist1 = TreeUtils.sprLowerBound(tree1, tree2);
        double dist2 = TreeUtils.sprLowerBound(tree2, tree1);
        switch (method){
            case "mean":
                return (dist1 + dist2) / 2;
            case "min":
                return Math.min(dist1, dist2);
            case "max":
                return Math.max(dist1, dist2);
            default:
                return -1;
        }
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        Tree<Integer>[] trees = new Tree[newicks.length];
        String preNewick = "";
        ArrayList<Tree<Integer>> treesList = new ArrayList<>();
        for (int i=0; i<newicks.length; i++){
            Tree<Integer> tree = TreeUtils.readFromNewick(newicks[i]);
            trees[i] = tree;
        }
        double[][] sprLowerBounds = new double[newicks.length][newicks.length];

        for(int i=0; i<(trees.length-1); i++) {
            for (int j = i + 1; j < trees.length; j++) {
                System.out.println(i + " " + j);
                double dist = 0;
                try {
                    dist = distance(trees[i], trees[j]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sprLowerBounds[i][j] = dist;
                sprLowerBounds[j][i] = dist;
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(end -start);
        callback.onFinish(index, sprLowerBounds);
    }

    public static double[][] dists;
    public static void main(String[] args) throws Exception {
//        String dirName = "data";
//        int cpuCount = 100;
//        try {
//            long start=System.currentTimeMillis();
//            ExecutorService executorService = Executors.newFixedThreadPool(cpuCount);
//            File dir = new File(dirName);
//            int id = 0;
//            for (File f: dir.listFiles(new FilenameFilter() {
//                @Override
//                public boolean accept(File dir, String name) {
//                    return name.toLowerCase().endsWith(".trees");
//                }
//            })){
//                FileReader fileReader = new FileReader(f);
//                BufferedReader reader =  new BufferedReader(fileReader);
//                String[] newicks = new String[50];
//                String line = "";
//                int i = 0;
//                while ((line=reader.readLine()) != null){
//                    newicks[i] = line.trim().split("\t")[1] + ";";
//                    i++;
//                }
//                Callback callback = (index, sprDist) -> dists = sprDist;
//
//                SPRRunnable thread = new SPRRunnable(id++, newicks, "max", callback);
//                executorService.execute(thread);
//            }
//            executorService.shutdown();
//            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
//            long end=System.currentTimeMillis();
//            System.out.println("ComputeSPR " + dirName +  " runtime: "+(end-start)+"ms");
//        }
//        catch (IOException | InterruptedException e){
//            System.out.println(e.getMessage());
//        }


        FileReader fileReader = new FileReader("data/1.txt.trees");
        BufferedReader reader =  new BufferedReader(fileReader);
        String[] newicks = new String[50];
        String line = "";
        int i = 0;
        while ((line=reader.readLine()) != null){
            newicks[i] = line.trim().split("\t")[1] + ";";
            i++;
        }
        Callback callback = (index, sprDist) -> dists = sprDist;
        SPRRunnable thread = new SPRRunnable(0, newicks, "max", callback);
        new Thread(thread).start();

//        new Thread(new RFRunnable2(0, newicks, callback)).start();
//        System.out.println(dists);
//        for (i=0; i<50; i++){
//            System.out.println(newicks[i]);
//        }

        // something for speeding up
//        long start = System.currentTimeMillis();
//        int[][] compares = new int[50][100*99*98/6];
//        double sum = 1;
//        for (int p=0; p<49; p++)
//            for (int q=p+1; q<50; q++)
//                for (int j=0; j<100*99*98/6; j++){
//                  if (compares[p][j]==compares[q][j]){
//                      for (int c=0; c<100; c++){
//                          sum ++;
//                      }
//                  }
//                }
//        long end = System.currentTimeMillis();
//        System.out.println(sum+ " " + (end-start));


    }

}
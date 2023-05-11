package labwu.thread;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import labwu.object.Tree;
import labwu.util.TreeUtils;

/*
 *  
 * 
 */

public class RFRunnable4BPWin implements Runnable {

    private int index;
    private String[] newicks;
    private int[] positions;        
    private Callback callback;
    private int windowSize;
    private int stepSize;
    private boolean interpolate;


    public RFRunnable4BPWin(int index, String[] newicks, int[] positions, int windowSize, int stepSize, Callback callback, boolean interpolate){
        this.index = index;
        this.newicks = newicks;
        this.positions = positions;
        this.windowSize = windowSize;
        this.stepSize = stepSize;
        this.callback = callback;
        //TODO: try distance interpolation by linearly combining distances of two nearby SNPs.
        this.interpolate = interpolate;
    }



    public SortedMap<Integer, Integer> linearInterpolation(int index){
        int focalPos = positions[index];
        int halfWindow = windowSize / 2;

        SortedMap<Integer, Integer> map = new TreeMap<>();
        // foward
        int cursor = focalPos;
        int i = index;
        while (focalPos - cursor <= halfWindow){
            // i am not sure if more than 1 snp will be skipped.
            while(i>0 && cursor <= positions[i-1])
                i--;
            if (i<=0){
                map.put(cursor, positions[0]);
            }else{
                if (positions[i]-cursor <= cursor-positions[i-1])
                    map.put(cursor, positions[i]);
                else
                    map.put(cursor, positions[i-1]); 

            }
            cursor -= stepSize;
        }
        // backward
        cursor = focalPos;
        i = index;
        while (cursor-focalPos <= halfWindow){
            // i am not sure if more than 1 snp will be skipped.
            while(i+1<positions.length && cursor >= positions[i+1])
                i++;
            if (i>=positions.length-1){
                map.put(cursor, positions[positions.length-1]);
            }else{
                if (cursor-positions[i] <= positions[i+1]-cursor)
                    map.put(cursor, positions[i]);
                else
                    map.put(cursor, positions[i+1]); 
            }
            cursor += stepSize;
        }
        return map;
    }


    public double[][] method(){
        double[][] dists = new double[newicks.length][];
        HashSet<BitSet>[] splits = new HashSet[newicks.length];
        int i = 0;
        int halfWindow = windowSize / 2;
        for (String newick: newicks){
            splits[i++] = TreeUtils.getHashedSplits(TreeUtils.readFromNewick(newick));
        }
        
        // Map<Set<Integer>, Double> temp = new HashMap<>();
        for(i=0; i<newicks.length; i++){
            int step = 0;
            Map<Integer, Double> map = new HashMap<>();
            while ((i-step >= 0) && (Math.abs(positions[i-step]-positions[i])<=halfWindow)){
                map.put(positions[i-step], TreeUtils.robinsonFoulds(splits[i], splits[i-step]));
                step++;
            }
            // go one more step
            if (i-step>=0) 
                map.put(positions[i-step], TreeUtils.robinsonFoulds(splits[i], splits[i-step]));
            step = 0;
            while ((i+step < positions.length) && (Math.abs(positions[i+step]-positions[i])<=halfWindow)){
                map.put(positions[i+step], TreeUtils.robinsonFoulds(splits[i], splits[i+step]));
                step++;
            }
            // go one more step
            if (i+step<positions.length) 
                map.put(positions[i+step], TreeUtils.robinsonFoulds(splits[i], splits[i+step]));
            SortedMap<Integer, Integer> li = linearInterpolation(i);
            double[] dist = new double[li.size()];
            int z = 0;
            // System.out.println(li);
            for (int key : li.keySet()){
                // System.out.println(key + " " + li.get(key));
                dist[z++] = map.get(li.get(key));
            }
            dists[i] = dist;
        }
        
        // callback function
        if(callback != null){
            callback.onFinish(index, dists);
        }
        return dists;
    }

    @Override
    public void run(){
        method();
    }


    public static void main(String[] args) throws NumberFormatException, IOException {
        // ArrayList<String> newick = new ArrayList<>();
        // ArrayList<Integer> position = new ArrayList<>();
        // String fileName = "data/2.txt.trees";
        // BufferedReader reader = new BufferedReader(new FileReader(fileName));
        // String line;
        // while ((line=reader.readLine())!=null){
        //     String treeStr = line.trim().split("\t")[1]+";";
        //     String pos = line.trim().split("\t")[0];
        //     position.add(Integer.valueOf(pos));
        //     newick.add(treeStr);
        // }

        // String[] newicks = newick.toArray(new String[0]);
        // int[] positions = position.stream().mapToInt(k -> k).toArray();
        
        // long start;
        // long end;
        // start = System.currentTimeMillis();
        // RFRunnable4BPWin runnbale = new RFRunnable4BPWin(0, newicks, positions, 100, 10, null);
        // double[][] result = runnbale.method();
        // for (int i =0; i < result[0].length; i++){
        //     System.out.print(result[result.length-1][i] + " ");
        // }
        // end = System.currentTimeMillis();
        // System.out.println("total runtime: "+ (end-start) + " ms"); 
    }
    
}


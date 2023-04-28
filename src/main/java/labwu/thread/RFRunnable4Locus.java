package labwu.thread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;

import labwu.object.Tree;
import labwu.util.TreeUtils;



public class RFRunnable4Locus implements Runnable {

    private int index;
    private String[] newicks;
    private Callback callback;
    private int windowSize;
    private String which;

    public RFRunnable4Locus(int index, String[] newicks, int windowSize, Callback callback, String which){
        this.index = index;
        this.newicks = newicks;
        this.windowSize = windowSize;
        this.callback = callback;
        this.which = which;
    }
    // TODO: can be modified here for speeding up hash splitting.
    public void method1(){
        double[][] dist = new double[newicks.length][2*windowSize+1];
        HashSet<BitSet>[] all = new HashSet[newicks.length];
        int i = 0;
        // long start = System.currentTimeMillis();
        for (String newick: newicks){
            all[i++] = TreeUtils.getHashedSplits(TreeUtils.readFromNewick(newick));
        }
        // long end = System.currentTimeMillis();
        // System.out.println("hashed splits: " + (end-start));
        for(i=0; i<newicks.length-1; i++){
            // executorService.execute(new RFParallelRunnable(all, i));
            for(int j=i-windowSize; j<i; j++){
                if (j<0)
                    dist[i][j-i+windowSize] = -1;
                else
                    dist[i][j-i+windowSize] = dist[j][windowSize+i-j];
            }
            for(int j=i+1; j<=i+windowSize; j++) {
                if (j >= newicks.length)
                    dist[i][j-i+windowSize] = -1;
                else 
                    dist[i][j-i+windowSize] = TreeUtils.robinsonFoulds(all[i], all[j]);
            }
        }
        // callback function
        callback.onFinish(index, dist);
    }

    public void method2(){
        double[][] dist = new double[newicks.length][2*windowSize+1];
        HashSet<BitSet>[] all = new HashSet[newicks.length];
        int i = 0;
        for (String newick: newicks){
            all[i++] = TreeUtils.getHashedSplits(TreeUtils.readFromNewick(newick));
        }

        for(i=0; i<newicks.length-1; i++){
            // executorService.execute(new RFParallelRunnable(all, i));
            for(int j=Math.max(0, i-windowSize); j<=Math.min(i+windowSize, newicks.length-1); j++) {
                double dis = TreeUtils.robinsonFoulds(all[i], all[j]);
                dist[i][j-i+windowSize] = dis;
            }
        }
        // callback function
        callback.onFinish(index, dist);
    }

    @Override
    public void run(){
        switch(which){
            case "fast": 
                method1();
                break;
            case "slow":
                method2();
                break;
        }
    }




    public static void main(String[] args) {

    }
    
}

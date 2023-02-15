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

    public void method1(){
        double[][] dist = new double[newicks.length][2*windowSize+1];
        HashSet<BitSet>[] all = new HashSet[newicks.length];
        int i = 0;
        for (String newick: newicks){
            all[i++] = TreeUtils.getHashedSplits(TreeUtils.readFromNewick(newick));
        }

        for(i=0; i<newicks.length-1; i++){
            // executorService.execute(new RFParallelRunnable(all, i));
            for(int j=Math.max(0, i-windowSize); j<i; j++){
                dist[i][j-i+windowSize] = dist[j][windowSize+i-j];
            }
            for(int j=i+1; j<=Math.min(i+windowSize, newicks.length-1); j++) {
                double dis = TreeUtils.robinsonFoulds(all[i], all[j]);
                dist[i][j-i+windowSize] = dis;
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
        ArrayList<String>[] a = new ArrayList[2];
        String[] s1 = new String[]{"a", "b"};
        ArrayList<String> a1 = new ArrayList<>(Arrays.asList(s1));
        String[] s2 = new String[]{"a", "b", "c"};
        ArrayList<String> a2 = new ArrayList<>(Arrays.asList(s2));
        a[0] = a1;
        a[1] = a2;
        System.out.println(a);
    }
    
}

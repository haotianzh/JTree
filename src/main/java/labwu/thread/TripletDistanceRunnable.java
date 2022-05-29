package labwu.thread;

import labwu.object.Tree;
import labwu.util.TreeUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;


public class TripletDistanceRunnable implements Runnable {
    private String[] newicks;
    private int index;
    private Callback callback;

    public TripletDistanceRunnable(int index, String[] newicks, Callback callback) {
        this.newicks = newicks;
        this.index = index;
        this.callback = callback;
    }
    @Override
    public void run() {
        Tree<Integer>[] trees = new Tree[newicks.length];
        for (int i=0; i<newicks.length; i++){
//            System.out.println(newicks[i]);
            Tree<Integer> tree = TreeUtils.readFromNewick(newicks[i]);
            trees[i] = tree;
        }
        int[][][] all = new int[trees.length][][];
        for (int i=0; i<trees.length; i++){
            try {
                all[i] = TreeUtils.pairwiseLCA(trees[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        double[][] tripletDist = new double[newicks.length][newicks.length];
        for(int i=0; i<(all.length-1); i++) {
            for (int j = i + 1; j < all.length; j++) {
                double dis = TreeUtils.tripletDistance(all[i], all[j]);
                tripletDist[i][j] = dis;
                tripletDist[j][i] = dis;
            }
        }
        callback.onFinish(index, tripletDist);
    }
}
package labwu.thread;

import labwu.object.Tree;
import labwu.util.TreeUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;


public class RFRunnable2 implements Runnable {
    private String[] newicks;
    private int index;
    private Callback callback;

    public RFRunnable2(int index, String[] newicks, Callback callback) {
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
        HashSet<BitSet>[] all = new HashSet[trees.length];
        for (int i=0; i<trees.length; i++){
            all[i] = TreeUtils.getHashedSplits(trees[i]);
        }
        double[][] rfDist = new double[newicks.length][newicks.length];
        for(int i=0; i<(all.length-1); i++) {
            for (int j = i + 1; j < all.length; j++) {
                double dis = TreeUtils.robinsonFoulds(all[i], all[j]);
                rfDist[i][j] = dis;
                rfDist[j][i] = dis;
            }
        }
        callback.onFinish(index, rfDist);
    }
}
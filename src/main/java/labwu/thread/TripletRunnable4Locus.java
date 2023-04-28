package labwu.thread;

import labwu.object.Tree;
import labwu.util.TreeUtils;

public class TripletRunnable4Locus implements Runnable {

    private int index;
//    private String[] newicks;
    private Callback2 callback;
//    private Tree[] trees;
    private int[][][] lca;
    private int windowSize;

    public TripletRunnable4Locus(int index, int windowSize, int[][][] lca, Callback2 callback){
        this.index = index;
        this.lca = lca;
        this.callback = callback;
        this.windowSize = windowSize;
    }


    @Override
    public void run(){
        double[] tripletDist = new double[windowSize];
        for (int i=1; i<windowSize; i++){
            if (index+i < lca.length) {
                double dis = TreeUtils.tripletDistance(lca[index+i], lca[index]);
                tripletDist[i] = dis;
            }else{
                tripletDist[i] = -1;
            }
        }
        callback.onFinish(index, tripletDist);
    }
}

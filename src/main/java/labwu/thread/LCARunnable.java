package labwu.thread;

import labwu.object.Tree;
import labwu.util.TreeUtils;


public class LCARunnable implements Runnable {
    private String newick;
    private int index;
    private LCACallback callback;
    public LCARunnable(int index, String newick, LCACallback callback) throws Exception {
        this.index = index;
        this.newick = newick;
        this.callback = callback;
    }
    @Override
    public void run() {
        int[][] lca;
        try {
            Tree<Integer> tree = TreeUtils.readFromNewick(newick);
            lca = TreeUtils.pairwiseLCA(tree);
            callback.onFinish(index, lca);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

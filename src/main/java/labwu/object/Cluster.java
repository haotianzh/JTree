package labwu.object;

import labwu.util.TreeUtils;

public class Cluster {
    private static int MAXSIZE = 200;
    public int[] left;
    public int[] right;
    public int[] relabeled;
    private Tree tree;
    private int size;

    public Cluster(Tree tree){
        if (!tree.isTraverse()){
            tree.traverse();
        }
        int size = tree.getExternalNodes().size();
        init(tree, size);
        build();
    }

    private void init(Tree tree, int size){
        this.tree = tree;
        this.size= size;
        left = new int[size+1];
        right= new int[size+1];
        relabeled = new int[size+1];
    }

    public int encode(int node){
        return relabeled[node];
    }

    private void build(){
        int labelIndex = 0;
        Tree node = TreeUtils.postOrderNext(tree);
        int r = 0;
        int l = 0;
        while (node != tree){
            if (node.isLeaf()){
                labelIndex ++;
                relabeled[(int) node.getRoot()] = labelIndex;
                r = labelIndex;
                node = TreeUtils.postOrderNext(node);
            }else{
                l = r - node.getExternalNodes().size() + 1;
                node = TreeUtils.postOrderNext(node);
                int loc;
                if (node.isLeaf()) loc = r; else loc = l;
                left[loc] = l;
                right[loc] = r;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("labwu.object.Cluster:\n");
        sb.append("i").append("\t");
        sb.append("left").append("\t");
        sb.append("right").append("\t");
        sb.append("encode").append("\n");
        for (int i=1; i<=size; i++){
            sb.append(i).append("\t");
            sb.append(left[i]).append("\t");
            sb.append(right[i]).append("\t");
            sb.append(relabeled[i]).append("\n");
        }
        return sb.toString();
    }
}

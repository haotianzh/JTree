import labwu.object.Tree;
import labwu.util.TreeUtils;

import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashSet;

public class RFTest {

    public static void method2(String fileName){
        File file = new File(fileName);
        Tree<Integer>[] trees = new Tree[0];
        try {
            trees = TreeUtils.readFromNewick(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HashSet<BitSet>[] all = new HashSet[trees.length];
        for (int i=0; i<trees.length; i++){
            all[i] = TreeUtils.getHashedSplits(trees[i]);
        }
        for(int i=0; i<(all.length-1); i++){
            for(int j=i+1; j<all.length; j++) {
                double dis = TreeUtils.robinsonFoulds(all[i], all[j]);
//                System.out.print(dis + " ");
            }
//            System.out.println();
        }
    }

    public static void main(String[] args) {
        long start=System.currentTimeMillis();
        method2(args[0]);
        long end = System.currentTimeMillis();
        System.out.println((end - start));
    }
}

import labwu.object.Tree;
import labwu.util.TreeUtils;

import javax.swing.plaf.TreeUI;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ComputeSPRLBO{

    public static long timeInterval = 0;


    public static void main(String[] args) throws Exception {

        String treeNewick1 = "((((63,79),46),((48,81),((62,95),47))),83);";
        String treeNewick2 = "((((46,79),63),(((62,81),48),(95,47))),83);";
        treeNewick2 = treeNewick1;
//        treeNewick1 = "(((((((((((((26,8),(38,44)),(59,77)),92),50),((4,56),83)),(86,9)),5),((((((13,33),95),((36,80),(91,94))),28),(31,78)),(11,45))),(((((((((3,71),(74,79)),(19,87)),1),58),63),15),((100,65),17)),(((((((49,55),61),(64,7)),42),47),(46,66)),75))),24),((((((14,2),(16,20)),(70,98)),76),((85,99),43)),((((25,54),69),89),27))),((((((((34,37),51),(22,93)),(41,67)),(((21,82),48),81)),(32,40)),(((((10,6),(35,68)),23),(((18,29),(30,57)),((73,90),(96,97)))),12)),((((39,72),84),(52,88)),((53,60),62))));";
//        treeNewick2 = "(((((((((((((((26,8),(38,44)),(59,77)),92),50),((4,56),83)),(86,9)),5),((((((13,33),95),((36,80),(91,94))),28),(31,78)),(11,45))),((((((((3,71),(74,79)),(19,87)),1),58),63),15),((100,65),17))),(((((((49,55),61),(64,7)),42),47),(46,66)),75)),24),((((((14,2),(16,20)),(70,98)),76),((85,99),43)),((((25,54),69),89),27))),((((53,60),62),(52,88)),((39,72),84))),(((((((34,37),51),(22,93)),(41,67)),(((21,82),48),81)),(32,40)),(((((10,6),(35,68)),23),(((18,29),(30,57)),((73,90),(96,97)))),12)));";
        Tree<Integer> tree1 = TreeUtils.readFromNewick(treeNewick1);
        Tree<Integer> tree2 = TreeUtils.readFromNewick(treeNewick2);
        tree1.traverse();
        tree2.traverse();
        int dist1 = 0;
        int dist2 = 0;
//        int dist1 = calculate(treeNewick2, treeNewick1);
//        int dist2 = calculate(treeNewick1, treeNewick2);

        long start = System.currentTimeMillis();

        Tree[] contracted = TreeUtils.contractTrees(tree1, tree2);
        List<List<Integer>> list = TreeUtils.getIncompatibleTriples(contracted[0], contracted[1]);

        System.out.println(TreeUtils.sprLowerBound(list, contracted[1]));
        System.out.println(TreeUtils.sprLowerBound(list, contracted[0]));
        long end = System.currentTimeMillis();
//        List<List<Integer>> list = TreeUtils.getIncompatibleTriples(contracted[0], contracted[1]);
//        for (List<Integer> trees : list){
//            System.out.println(trees.get(0) + " " + trees.get(1) + " " + trees.get(2));
//        }


//        System.out.println(TreeUtils.sprLowerBound(tree1, tree2));

//        System.out.println(list.size());

//        System.out.println(TreeUtils.getFirstCoalescenceOfTriple(tree2.findNodeByValue(1), tree2.findNodeByValue(22), tree2.findNodeByValue(93)));
//
//        HashSet<BitSet> a1 = TreeUtils.getHashedSplits(tree1);
//        HashSet<BitSet> a2 = TreeUtils.getHashedSplits(tree2);
//        System.out.println(TreeUtils.robinsonFoulds(a1, a2));


        System.out.println("# triples:" + dist1 + " " + dist2+ " running time:" + (end-start)+"ms");


    }
}


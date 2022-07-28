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
//        String treeNewick1 = "((((63,79),46),((48,81),((62,95),47))),83);";
//        String treeNewick2 = "((((46,79),63),(((62,81),48),(95,47))),83);";
//        treeNewick1 = "(((((((((((((26,8),(38,44)),(59,77)),92),50),((4,56),83)),(86,9)),5),((((((13,33),95),((36,80),(91,94))),28),(31,78)),(11,45))),(((((((((3,71),(74,79)),(19,87)),1),58),63),15),((100,65),17)),(((((((49,55),61),(64,7)),42),47),(46,66)),75))),24),((((((14,2),(16,20)),(70,98)),76),((85,99),43)),((((25,54),69),89),27))),((((((((34,37),51),(22,93)),(41,67)),(((21,82),48),81)),(32,40)),(((((10,6),(35,68)),23),(((18,29),(30,57)),((73,90),(96,97)))),12)),((((39,72),84),(52,88)),((53,60),62))));";
//        treeNewick2 = "(((((((((((((((12,8),(38,44)),(59,77)),92),50),((4,56),83)),(86,9)),5),((((((13,33),95),((36,80),(91,94))),28),(31,78)),(11,45))),((((((((3,71),(74,79)),(19,87)),1),58),63),15),((100,65),17))),(((((((49,55),61),(64,7)),42),47),(46,66)),75)),24),((((((14,2),(16,20)),(70,98)),76),((85,99),43)),((((25,54),69),89),27))),((((53,60),62),(52,88)),((39,72),84))),(((((((34,37),51),(22,93)),(41,67)),(((21,82),48),81)),(32,40)),(((((10,6),(35,68)),23),(((18,29),(30,57)),((73,90),(96,97)))),26)));";
        String filename = args[0];

        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = "";
        ArrayList<Tree> trees = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            Tree tree = TreeUtils.readFromNewick(line);
            tree.traverse();
            trees.add(tree);
        }

        long start = System.currentTimeMillis();
        for (int i = 0; i < trees.size() - 1; i++){
            for (int j = i + 1; j < trees.size(); j++) {
                System.out.print(TreeUtils.sprLowerBound(trees.get(i), trees.get(j)) + " ");
            }
            System.out.println();
        }
        long end = System.currentTimeMillis();
        System.out.println((end-start)+"ms");
    }
}


import labwu.object.Tree;
import labwu.util.TreeUtils;

import java.util.*;

public class SPRLBO {

    public static <T> HashMap<T, Map<T, Integer>> getAncestorOfPairs(Tree<T> tree){
        if (!tree.isTraverse()) tree.traverse();
        HashMap<T, Map<T, Integer>> table = new HashMap<>();
        ArrayList<Tree<T>> extNodes = tree.getExternalNodes();
        for (Tree<T> leaf: extNodes) table.put(leaf.getRoot(), new LinkedHashMap<>());
        Tree<T> node = tree;
        boolean isFinish = false;
        while ((node=TreeUtils.postOrderNext(node))!=null && !isFinish){
            if (node.isRoot()) isFinish = true;
            if (node.isLeaf()) {
                continue;
            }
            Tree<T> left = node.getChildren().get(0);
            Tree<T> right = node.getChildren().get(1);
            for (Tree<T> child1 : left.getExternalNodes()) {
                for (Tree<T> child2: right.getExternalNodes()){
                    table.get(child1.getRoot()).put(child2.getRoot(), node.getLevel());
                    table.get(child2.getRoot()).put(child1.getRoot(), node.getLevel());
                }
            }
        }
        return table;
    }

    public static Map<String, List<Integer>> findInversions(Set<List<Integer>> triples, int prefix, List<Integer> array, List<Integer> index){
        if (array.size() == 1) {
            Map<String, List<Integer>> map = new HashMap<>();
            map.put("array", array);
            map.put("index", index);
            return map;
        }
        int mid = array.size() / 2;
        Map<String, List<Integer>> left = findInversions(triples, prefix, array.subList(0, mid), index.subList(0, mid));
        Map<String, List<Integer>> right = findInversions(triples, prefix, array.subList(mid, array.size()), index.subList(mid, array.size()));
        ArrayList<Integer> mergedArray = new ArrayList<>();
        ArrayList<Integer> mergedIndex = new ArrayList<>();
        int i = 0;
        int j = 0;

        while (i<left.get("array").size() && j< right.get("array").size()){
            if (left.get("array").get(i) >= right.get("array").get(j)){
                mergedArray.add(left.get("array").get(i));
                mergedIndex.add(left.get("index").get(i));
                i++;
            }else{
                mergedArray.add(right.get("array").get(j));
                mergedIndex.add(right.get("index").get(j));
                // print or store all inversions
                for (int k=i; k<left.get("array").size(); k++)
                {

                    List<Integer> triple = new ArrayList<>();
                    triple.add(prefix);
                    triple.add(left.get("index").get(k));
                    triple.add(right.get("index").get(j));
                    triple.sort(null);
//                    if (!triples.contains(triple))
                    triples.add(triple);
//                    System.out.println(left.get("index").get(k) + " " + right.get("index").get(j));
                }
                j++;
            }
        }

        while (j<right.get("array").size()){
            mergedArray.add(right.get("array").get(j));
            mergedIndex.add(right.get("index").get(j));
            j++;
        }
        while (i<left.get("array").size()){
            mergedArray.add(left.get("array").get(i));
            mergedIndex.add(left.get("index").get(i));
            i++;
        }


        Map<String, List<Integer>> map = new HashMap<>();
        map.put("array", mergedArray);
        map.put("index", mergedIndex);
        return map;
    }


    public static <T> List<List<Integer>> findAllIncompatibleTriples(HashMap<Integer, Map<Integer, Integer>> table1, HashMap<Integer, Map<Integer, Integer>> table2){
        Set<List<Integer>> triples = new HashSet<>();
        for (Iterator<Integer> it = table1.keySet().iterator(); it.hasNext(); ) {
            int label = it.next();
            //for (int i=0; i<tree.getExternalNodes().size(); i++) {
//            Tree node = (Tree) tree.getExternalNodes().get(i);
//            int label = (int) node.getRoot();
            ArrayList<Integer> array = new ArrayList<>();
            ArrayList<Integer> index = new ArrayList<>();
            Iterator<Integer> iterator = table2.get(label).keySet().iterator();
            while(iterator.hasNext()){
                int next = iterator.next();
                array.add(table1.get(label).get(next));
                index.add(next);
            }
            findInversions(triples, label, array, index);
//            System.out.println("--------------");
        }
        List<List<Integer>> result = new ArrayList<>();
        result.addAll(triples);
        return result;
    }


    public static List<List<Integer>> findAllIncompatibleTriples(Tree tree1, Tree tree2){
        if (!tree1.isTraverse()) tree1.traverse();
        if (!tree2.isTraverse()) tree2.traverse();
        HashMap<Integer, Map<Integer, Integer>> table1 = getAncestorOfPairs(tree1);
        HashMap<Integer, Map<Integer, Integer>> table2 = getAncestorOfPairs(tree2);
        Set<List<Integer>> triples = new HashSet<>();
        for (int i=1; i<tree1.getExternalNodes().size(); i++){
            ArrayList<Integer> array = new ArrayList<>();
            ArrayList<Integer> index = new ArrayList<>();
            Tree node = (Tree) tree1.getExternalNodes().get(i);
            int label = (int) node.getRoot();
            Iterator<Integer> iterator = table2.get(label).keySet().iterator();
            while(iterator.hasNext()){
                int next = iterator.next();
                array.add(table1.get(label).get(next));
                index.add(next);
            }
            findInversions(triples, label, array, index);
        }
        List<List<Integer>> result = new ArrayList<>();
        result.addAll(triples);
        return result;
    }

    // have to ensure that trees have exactly the same leaves
    public static <T> Map<Set<T>, Tree<T>> buildTripleRoot(Tree<T> tree) throws Exception {
        Map<Set<T>, Tree<T>> result = new HashMap<>();
        ArrayList<T> labels = new ArrayList<>();
        for (Tree<T> node: tree.getExternalNodes())
            labels.add(node.getRoot());
        labels.sort(null);
        for (int i=0; i<labels.size()-2; i++)
            for (int j=i+1; j<labels.size()-1; j++) {
                for (int t=j+1; t < labels.size(); t++) {
                    Tree<T> root = null;
                    int type = -1;
                    Tree<T> a = tree.findNodeByValue(labels.get(i));
                    Tree<T> b = tree.findNodeByValue(labels.get(j));
                    Tree<T> c = tree.findNodeByValue(labels.get(t));
                    Tree<T> ab = tree.getMrcaOfPair(a,b);
                    Tree<T> bc = tree.getMrcaOfPair(c,b);
                    Tree<T> ac = tree.getMrcaOfPair(a,c);
                    if (ab == bc && ab.getLevel() < ac.getLevel()){
                        root = ab;
                    }
                    else if (ab == ac && ab.getLevel() < bc.getLevel()){
                        root = ab;
                    }
                    else if (bc == ac && bc.getLevel() < ab.getLevel()){
                        root = bc;
                    }
                    Set<T> set = new HashSet<>();
                    set.add(a.getRoot());
                    set.add(b.getRoot());
                    set.add(c.getRoot());
                    result.put(set, root);
                }
            }
        return result;
    }

    public static int encodeTriple(Tree a, Tree b, Tree c){
        int aa = (int) a.getRoot();
        int bb = (int) b.getRoot();
        int cc = (int) c.getRoot();
        int key = aa * 100 * 100 + bb * 100 + cc;
        return key;
    }

    // have to ensure that trees have exactly the same leaves
    public static <T> Map<Integer, Integer> buildTripleIndex(Tree<T> tree) throws Exception {
        Map<Integer, Integer> result = new HashMap<>();
        ArrayList<T> labels = new ArrayList<>();
        for (Tree<T> node: tree.getExternalNodes())
            labels.add(node.getRoot());
        labels.sort(null);
        for (int i=0; i<labels.size()-2; i++)
            for (int j=i+1; j<labels.size()-1; j++) {
                for (int t=j+1; t < labels.size(); t++) {
                    Tree<T> root = null;
                    int type = -1;
                    Tree<T> a = tree.findNodeByValue(labels.get(i));
                    Tree<T> b = tree.findNodeByValue(labels.get(j));
                    Tree<T> c = tree.findNodeByValue(labels.get(t));
                    Tree<T> ab = tree.getMrcaOfPair(a,b);
                    Tree<T> bc = tree.getMrcaOfPair(c,b);
                    Tree<T> ac = tree.getMrcaOfPair(a,c);
                    if (ab == bc && ab.getLevel() < ac.getLevel()){
                        root = ab;
                        type = 0;
                    }
                    else if (ab == ac && ab.getLevel() < bc.getLevel()){
                        root = ab;
                        type = 2;
                    }
                    else if (bc == ac && bc.getLevel() < ab.getLevel()){
                        root = bc;
                        type = 1;
                    }
                    int key = encodeTriple(a,b,c);
                    result.put(key, type);
                }
            }
        return result;
    }

    public static void main(String[] args) throws Exception {
        long start, end;
        String treeNewick1 = "(((((((((((((70,96),((23,8),54)),34),6),79),((((((16,64),15),(60,76)),3),1),31)),69),10),2),63),27),86),95);";
        String treeNewick2 = "((((((((((((8,96),54),((23,70),34)),6),79),(((((16,76),3),((60,64),15)),1),31)),69),10),2),63),27),(86,95));";
//         treeNewick1 = "((((63,79),46),((48,81),((62,95),47))),83);";
//         treeNewick2 = "((((46,79),63),(((62,81),48),(95,47))),83);";
//        treeNewick2 = treeNewick1;
        treeNewick1 = "(((((((((((((((25,54),69),(32,40)),((3,71),(74,79))),(15,27)),(31,78)),(((((13,33),95),((36,80),(91,94))),((100,65),81)),((19,87),50))),23),92),((((26,8),(38,44)),(59,77)),((46,66),45))),58),((56,64),76)),47),((((((((((((85,99),42),89),((34,37),51)),((86,9),28)),12),(41,67)),11),62),((21,82),(52,88))),((((10,6),(35,68)),((22,93),1)),((49,55),61))),(((((((18,29),(30,57)),((73,90),(96,97))),75),(((17,4),43),(53,60))),63),(((((14,2),(16,20)),(70,98)),83),48)))),(((24,7),5),((39,72),84)));";
        treeNewick2 = "(((((((((((((((25,54),69),(32,40)),((3,71),(74,79))),(15,27)),(31,78)),(((((13,33),95),((36,80),(91,94))),((100,65),81)),((19,87),50))),23),92),((((26,8),(38,44)),(59,77)),((46,66),45))),58),47),((56,64),76)),(((((((((((((85,99),42),89),((34,37),51)),((86,9),28)),12),(41,67)),11),62),((21,82),(52,88))),63),((((10,6),(35,68)),((22,93),1)),((49,55),61))),((((((14,2),(16,20)),(70,98)),83),48),(((((18,29),(30,57)),((73,90),(96,97))),75),(((17,4),43),(53,60)))))),(((24,7),5),((39,72),84)));";
        Tree<Integer> tree1 = TreeUtils.readFromNewick(treeNewick1);
        Tree<Integer> tree2 = TreeUtils.readFromNewick(treeNewick2);
        tree1.traverse();
        tree2.traverse();


//        System.out.println(encodeTriple(tree1.findNodeByValue(1), tree1.findNodeByValue(2), tree1.findNodeByValue(3)));
//        start = System.currentTimeMillis();
//        Map<Integer, Integer> tripleIndex1 = buildTripleIndex(tree1);
//        Map<Integer, Integer> tripleIndex2= buildTripleIndex(tree2);
////        System.out.println(tripleIndex1);
////        Map<Set<Integer>, Integer> tripleIndex2 = buildTripleIndex(tree2);
////        Map<Set<Integer>, Tree<Integer>> tripleRoot = buildTripleRoot(tree1);
////        System.out.println(tripleIndex1.size());
////        System.out.println(tripleRoot);


//
//        long end1 = System.currentTimeMillis();
//        System.out.println(end1 -start);
//        Iterator<Integer> iterator = tripleIndex1.keySet().iterator();
//        int count = 0;
//        while (iterator.hasNext()){
//            Integer next = iterator.next();
//            if (tripleIndex2.get(next) != tripleIndex1.get(next)){
//                count ++;
//            }
//        }
//        end = System.currentTimeMillis();
//        System.out.println(count +" "+ (end-end1));



        int[][] p1 = TreeUtils.pairwiseLCA(tree1);
        int[][] p2 = TreeUtils.pairwiseLCA(tree2);
        start = System.currentTimeMillis();
        System.out.println(TreeUtils.tripletDistance(p1, p2));
        end = System.currentTimeMillis();
        System.out.println((end-start));
        start = System.currentTimeMillis();
        System.out.println(TreeUtils.getIncompatibleTriples(tree2, tree1).size());
        end = System.currentTimeMillis();
        System.out.println((end-start));
        System.out.println("---------------------------");















//        HashMap<Integer, Map<Integer, Integer>> table1 = getAncestorOfPairs(tree1);
//        HashMap<Integer, Map<Integer, Integer>> table2 = getAncestorOfPairs(tree2);
//        System.out.println(table1.get(2).keySet());
//        System.out.println(table2.get(2).keySet());
        List<List<Integer>> triples;
        double dist;

//        start = System.currentTimeMillis();
//        triples = TreeUtils.getIncompatibleTriples(tree1, tree2);
//        dist = TreeUtils.sprLowerBound(triples, tree1);
//        System.out.println(dist);
//        end = System.currentTimeMillis();
//        System.out.println("simple enumerate (running time(ms)):" + (end-start));


//        start = System.currentTimeMillis();
//        triples = findAllIncompatibleTriples(tree1, tree2);
//        dist = TreeUtils.sprLowerBound(triples, tree1);
//        dist = TreeUtils.sprLowerBound(triples, tree2);
////        System.out.println(dist);
//        end = System.currentTimeMillis();
//        System.out.println("merge sort: (running time(ms)):" + (end-start));

//        start = System.currentTimeMillis();
//        HashMap<Integer, Map<Integer, Integer>> table1 = getAncestorOfPairs(tree1);
//        HashMap<Integer, Map<Integer, Integer>> table2 = getAncestorOfPairs(tree2);
//        triples = findAllIncompatibleTriples(table1, table2);
////        System.out.println(System.currentTimeMillis() - start);
//        dist = TreeUtils.sprLowerBound(triples, tree1);
////        System.out.println(dist);
//        dist = TreeUtils.sprLowerBound(triples, tree2);
////        System.out.println(dist);
//        end = System.currentTimeMillis();
//        System.out.println(triples.size() + " " + (end-start) + "ms");




    }
}


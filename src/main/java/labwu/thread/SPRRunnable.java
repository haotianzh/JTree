package labwu.thread;
import labwu.object.Tree;
import labwu.util.TreeUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


class Utils{
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
    public static <T> Map<Tree<T>, List<Set<T>>> buildTripleRoot(Tree<T> tree) throws Exception {
        Map<Tree<T>, List<Set<T>>> result = new HashMap<>();
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
                    if (ab == bc && ab.getLevel() > ac.getLevel()){
                        root = ab;

                    }
                    else if (ab == ac && ab.getLevel() > bc.getLevel()){
                        root = ab;

                    }
                    else if (bc == ac && bc.getLevel() > ab.getLevel()){
                        root = bc;

                    }
                    Set<T> set = new HashSet<>();
                    set.add(a.getRoot());
                    set.add(b.getRoot());
                    set.add(c.getRoot());
                    if (result.containsKey(root)){
                        result.get(root).add(set);
                    }else{
                        result.put(root, new ArrayList<>());
                    }
                }
            }
        return result;
    }

    // have to ensure that trees have exactly the same leaves
    public static <T> Map<Set<T>, Integer> buildTripleIndex(Tree<T> tree) throws Exception {
        Map<Set<T>, Integer> result = new HashMap<>();
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
                    if (ab == bc && ab.getLevel() > ac.getLevel()){
                        root = ab;
                        type = 0;
                    }
                    else if (ab == ac && ab.getLevel() > bc.getLevel()){
                        root = ab;
                        type = 2;
                    }
                    else if (bc == ac && bc.getLevel() > ab.getLevel()){
                        root = bc;
                        type = 1;
                    }
                    Set<T> set = new HashSet<>();
                    set.add(a.getRoot());
                    set.add(b.getRoot());
                    set.add(c.getRoot());
                    result.put(set, type);
                }
            }
        return result;
    }

    public static void buildTriples(Tree[] trees){

    }

}

public class SPRRunnable implements Runnable {
    private String[] newicks;
    private int index;
    private Callback callback;
    private String method;
    private String[] methods = {"mean", "min", "max"};
    private HashSet<String> availableMethods = new HashSet<>(Arrays.asList(methods));
    public SPRRunnable(int index, String[] newicks, String method, Callback callback) throws Exception {
        this.newicks = newicks;
        this.index = index;
        this.callback = callback;
        if (availableMethods.contains(method))
            this.method = method;
        else
            throw new Exception("no such method");
    }

    public double distance(Tree tree1, Tree tree2, HashMap<Integer, Map<Integer, Integer>> table1, HashMap<Integer, Map<Integer, Integer>> table2) throws Exception {
        Tree[] trees = TreeUtils.contractTrees(tree1, tree2);
        tree1 = trees[0];
        tree2 = trees[1];
        double dist1 = 0;
        double dist2 = 0;
        List<List<Integer>> incompatibleTriples = null;
        try {
//            incompatibleTriples = Utils.findAllIncompatibleTriples(table1, table2);
            incompatibleTriples = Utils.findAllIncompatibleTriples(tree1, tree2);
//            incompatibleTriples = TreeUtils.getIncompatibleTriples(tree1, tree2);
            dist1 = TreeUtils.sprLowerBound(incompatibleTriples, tree2);
            dist2 = TreeUtils.sprLowerBound(incompatibleTriples, tree1);
        }
        catch(Exception e){
            System.out.println(incompatibleTriples);
            System.out.println(tree1.newick());
            System.out.println(tree2.newick());
            e.printStackTrace();
            System.exit(0);
        }
        switch (method){
            case "mean":
                return (dist1 + dist2) / 2;
            case "min":
                return Math.min(dist1, dist2);
            case "max":
                return Math.max(dist1, dist2);
            default:
                return -1;
        }
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        Tree<Integer>[] trees = new Tree[newicks.length];
        HashMap<Integer, Map<Integer, Integer>>[] ancestorTables = new HashMap[newicks.length];

        for (int i=0; i<newicks.length; i++){
            Tree<Integer> tree = TreeUtils.readFromNewick(newicks[i]);
            trees[i] = tree;
            ancestorTables[i] = Utils.getAncestorOfPairs(tree);
        }
        double[][] sprLowerBounds = new double[newicks.length][newicks.length];

        for(int i=0; i<(trees.length-1); i++) {
            for (int j = i + 1; j < trees.length; j++) {
                double dist = 0;
                try {
                    dist = distance(trees[i], trees[j], ancestorTables[i], ancestorTables[j]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sprLowerBounds[i][j] = dist;
                sprLowerBounds[j][i] = dist;
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(index + " " + (end-start));
        callback.onFinish(index, sprLowerBounds);
    }




    public static double[][] dists;


    public static void main(String[] args) throws Exception {
        String dirName = "data";
        int cpuCount = 20;
        try {
            long start=System.currentTimeMillis();
            ExecutorService executorService = Executors.newFixedThreadPool(cpuCount);
            File dir = new File(dirName);
            int id = 0;
            for (File f: dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".trees");
                }
            })){
                FileReader fileReader = new FileReader(f);
                BufferedReader reader =  new BufferedReader(fileReader);
                String[] newicks = new String[50];
                String line = "";
                int i = 0;
                while ((line=reader.readLine()) != null){
                    newicks[i] = line.trim().split("\t")[1] + ";";
                    i++;
                }
                Callback callback = (index, sprDist) -> dists = sprDist;
                SPRRunnable thread = new SPRRunnable(id++, newicks, "max", callback);
                executorService.execute(thread);
            }
            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
            long end=System.currentTimeMillis();
            System.out.println("ComputeSPR " + dirName +  " runtime: "+(end-start)+"ms");
        }
        catch (IOException | InterruptedException e){
            System.out.println(e.getMessage());
        }

        /**

        FileReader fileReader = new FileReader("data/1.txt.trees");
        BufferedReader reader =  new BufferedReader(fileReader);
        String[] newicks = new String[50];
        String line = "";
        int i = 0;
        while ((line=reader.readLine()) != null){
            newicks[i] = line.trim().split("\t")[1] + ";";
            i++;
        }
        Callback callback = (index, sprDist) -> dists = sprDist;
        SPRRunnable thread = new SPRRunnable(0, newicks, "max", callback);
        new Thread(thread).start();
        **/


    }

}
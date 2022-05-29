package labwu.util;

import labwu.object.Cluster;
import labwu.object.Tree;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TreeUtils {

    public static Tree postOrderNext(Tree node){
        Tree current = null;
        Tree parent = node.getParent();
        if (node.isRoot()){
            current = node;
        }else{
            if (parent.getChildren().get(parent.getChildrenSize()-1) == node){
                return parent;
            }
            for (int i=0;i< parent.getChildrenSize();i++){
                if (parent.getChildren().get(i) == node){
                    current = (Tree) parent.getChildren().get(i+1);
                    break;
                }
            }
        }
        if (current != null){
            while (current.getChildrenSize() > 0){
                current = (Tree) current.getChildren().get(0);
            }
        }
        return current;
    }

    public static Cluster buildClusters(Tree tree){
        return new Cluster(tree);
    }

    // only for complete binary tree, two trees should have same leaves.
    public static double robinsonFoulds(Cluster cluster, Tree tree){
        Stack<int[]> stack = new Stack<>();
        Tree node = postOrderNext(tree);
        int common = 0;
        while (node != tree){
            if (node.isLeaf()){
                int encode = cluster.encode((Integer) node.getRoot());
                int[] tuple = new int[]{encode, encode, 1};
                stack.push(tuple);
//                System.out.println(node);
            }else{
                int[] tuple1 = stack.pop();
//                System.out.println(stack.size());
                int[] tuple2 = stack.pop();
//                System.out.println(stack.size());
                int left = Math.min(tuple1[0], tuple2[0]);
                int right = Math.max(tuple1[1], tuple2[1]);
                int N =  tuple1[2] + tuple2[2];
                int[] tuple = new int[]{left, right, N};
                stack.push(tuple);
                if (right - left + 1 == N){
                    // check if [left, right] is indeed in cluster.
                    boolean flag = false;
                    if (cluster.left[left] == left && cluster.right[left] == right) flag = true;
                    if (cluster.left[right] == left && cluster.right[right] == right) flag = true;
                    if (flag){
                        common ++;
                    }
                }
            }
            node = TreeUtils.postOrderNext(node);
        }
        return 2 * (tree.getInternalNodes().size() - 1) - 2 * common;
    }

    public static double robinsonFoulds(HashSet<BitSet> splits1, HashSet<BitSet> splits2){
        double common = 0;
        for (BitSet set : splits2){
            if (splits1.contains(set)) common++;
        }
        return splits1.size() + splits2.size() - 2 * common;
    }

    public static HashSet<BitSet> getHashedSplits(Tree tree) {
        if (!tree.isTraverse()){
            tree.traverse();
        }
        int nodeCounts = tree.getExternalNodes().size();
        HashSet<BitSet> splits = new HashSet<>();
        ArrayList<Tree> internalNodes = tree.getInternalNodes();

        for (Tree inter : internalNodes){
            // Skip getting trivial splits for the root.
            if (inter == tree){
                continue;
            }
            // Skip the child of the root if the other child is leaf.
            BitSet set = new BitSet();
            ArrayList<Tree> externalNodes = inter.getExternalNodes();
            for (Tree node : externalNodes){
                int id = (int) node.getRoot();
                set.set(id-1, true);
            }
            if (!set.get(0)){
                set.flip(0, nodeCounts);
            }
            int count = (int) set.stream().count();
            if (count != nodeCounts-1 && count != 1) splits.add(set);
        }

        return splits;
    }


    public static <T> int[][] pairwiseLCA(Tree<T> tree) throws Exception {
        // tree traverse
        if (!tree.isTraverse()) tree.traverse();
        // get labels of taxa
        ArrayList<T> labels = new ArrayList<>();
        for (Tree<T> node: tree.getExternalNodes())
            labels.add(node.getRoot());
        labels.sort(null);
        int numLeaves = labels.size();
        int[][] pattern = new int[numLeaves][numLeaves];
        for (int i=0; i<numLeaves-1; i++){
            for (int j=i+1; j<numLeaves; j++){
                Tree<T> lca = tree.getMrcaOfPair(tree.findNodeByValue(labels.get(i)), tree.findNodeByValue(labels.get(j)));
                pattern[i][j] = lca.getLevel();
                pattern[j][i] = lca.getLevel();
            }
        }
        return pattern;
    }

    public static <T> double tripletDistance(Tree<T> tree1, Tree<T> tree2){
        int[][] lcaTree1 = new int[0][];
        try {
            lcaTree1 = pairwiseLCA(tree1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int[][] lcaTree2 = new int[0][];
        try {
            lcaTree2 = pairwiseLCA(tree2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tripletDistance(lcaTree1, lcaTree2);
    }


    public static <T> double tripletDistance(int[][] lcaTree1, int[][] lcaTree2){
        double dist = 0;
        int n = lcaTree1.length;
        for (int i=0; i<n; i++){
            Map<Integer, Map<Integer, Integer>> temp = new HashMap<>();
            for (int j=0; j<n; j++){
                if (i==j) continue;
                if (!temp.containsKey(lcaTree1[i][j])){
                    temp.put(lcaTree1[i][j], new HashMap<>());
                }
                if (!temp.get(lcaTree1[i][j]).containsKey(lcaTree2[i][j])){
                    temp.get(lcaTree1[i][j]).put(lcaTree2[i][j], 1);
                }else{
                    int count = temp.get(lcaTree1[i][j]).get(lcaTree2[i][j]);
                    temp.get(lcaTree1[i][j]).put(lcaTree2[i][j], ++count);
                }
            }
            Iterator<Integer> iter1 = temp.keySet().iterator();
            Iterator<Integer> iter2;
            while (iter1.hasNext()){
                int l1 = iter1.next();
                iter2 = temp.get(l1).keySet().iterator();
                while (iter2.hasNext()){
                    int l2 = iter2.next();
                    int count = temp.get(l1).get(l2);
                    if (temp.get(l1).get(l2) > 1){
                        dist += (double) count * (count-1) / 2;
                    }
                }
            }
        }
        dist = (double) n*(n-1)*(n-2)/6 - dist;
        return dist;
    }


    public static <T> Tree<T>[] contractTrees(Tree<T> tree1, Tree<T> tree2) throws Exception {
        boolean canContract = true;
        tree1 = TreeUtils.copy(tree1);
        tree2 = TreeUtils.copy(tree2);
        ArrayList<HashSet<T>> list1 = new ArrayList<>();
        ArrayList<HashSet<T>> list2 = new ArrayList<>();
        while(canContract){
            list1.clear();
            list2.clear();
            if (!tree1.isTraverse()) tree1.traverse();
            if (!tree2.isTraverse()) tree2.traverse();
            ArrayList<Tree<T>> internalNodes1_temp = tree1.getInternalNodes();
            ArrayList<Tree<T>> internalNodes2_temp = tree2.getInternalNodes();
            ArrayList<Tree<T>> internalNodes1 = new ArrayList<>();
            ArrayList<Tree<T>> internalNodes2 = new ArrayList<>();
            for (Tree<T> node : internalNodes1_temp){
                if (node.getExternalNodes().size() == 2){
                    internalNodes1.add(node);
                    HashSet<T> hashset = new HashSet<>();
                    for (Tree<T> n : node.getExternalNodes()){
                        hashset.add(n.getRoot());
                    }
                    list1.add(hashset);
                }
            }
            for (Tree<T> node : internalNodes2_temp){
                if (node.getExternalNodes().size() == 2){
                    internalNodes2.add(node);
                    HashSet<T> hashset = new HashSet<>();
                    for (Tree<T> n : node.getExternalNodes()){
                        hashset.add(n.getRoot());
                    }
                    list2.add(hashset);
                }
            }
            // update
            boolean flag = false;
            for (int i=0; i<list1.size(); i++){
                if (list2.contains(list1.get(i))){
                    flag = true;
                    T label = (T) list1.get(i).iterator().next();
                    int index = list2.indexOf(list1.get(i));
                    Tree<T> parent1 = internalNodes1.get(i).getParent();
                    Tree<T> parent2 = internalNodes2.get(index).getParent();
                    if (parent1 == null && parent2 == null){
                        Tree<T>[] trees = new Tree[]{new Tree<T>(label), new Tree<T>(label)};
                        return trees;
                    }
                    parent1.delete(internalNodes1.get(i));
                    parent2.delete(internalNodes2.get(index));
                    parent1.addChild(new Tree<T>(label));
                    parent2.addChild(new Tree<T>(label));
                }
            }
            canContract = flag;
        }
        Tree<T>[] trees = new Tree[]{tree1, tree2};
        return trees;
    }

    public static double sprLowerBound(String reference, String query) throws Exception {
        Tree<Integer> tree = TreeUtils.readFromNewick(query);
        Tree<Integer> refer = TreeUtils.readFromNewick(reference);
        return sprLowerBound(refer, tree);
    }

    public static <T> double sprLowerBound(Tree refer, Tree tree) throws Exception {
        List<List<T>> incompatibleTriples = TreeUtils.getIncompatibleTriples(refer, tree);
        return sprLowerBound(incompatibleTriples, tree);
    }

    public static <T> double sprLowerBound(List<List<T>> incompatibleTriples, Tree tree) throws Exception {
        // post-traverse the tree and perform dynamic programming.
//        if (!refer.isTraverse()) refer.traverse();
        if (!tree.isTraverse()) tree.traverse();
//        Tree[] trees = TreeUtils.contractTrees(refer, tree);
//        refer = trees[0];
//        tree = trees[1];
        // get incompatible triples.
//        List<Tree[]> incompatibleTriples = TreeUtils.getIncompatibleTriples(refer, tree);
        if (incompatibleTriples.size() == 0)
            return 0;
        Map<Tree<T>, Integer> M = new HashMap<>();
        Map<Tree<T>, Map<Tree<T>, Integer>> M1 = new HashMap<>();
        Map<Tree<T>, Map<Tree<T>, Map<Tree<T>, Integer>>> M2 = new HashMap<>();
        Tree<T> node = TreeUtils.postOrderNext(tree);
        while(true){
            ArrayList<Tree<T>> extNodes = node.getExternalNodes();
            Set<Tree<T>> extNodesSet = node.getExternalNodesSet();
            if (extNodes.size() < 3){
                M.put(node, 0);
                for (Tree<T> leaf: extNodes){
                    if (!M1.containsKey(node)) M1.put(node, new HashMap<Tree<T>, Integer>());
                    M1.get(node).put(leaf, 0);
                }
                for (Tree<T> leaf1: extNodes) {
                    for (Tree<T> leaf2 : extNodes) {
                        Map<Tree<T>, Integer> map2 = new HashMap<>();
                        Map<Tree<T>, Map<Tree<T>, Integer>> map1 = new HashMap<>();
                        if (!M2.containsKey(node)) M2.put(node, map1);
                        if (!M2.get(node).containsKey(leaf1)) M2.get(node).put(leaf1, map2);
                        M2.get(node).get(leaf1).put(leaf2, 0);
                    }
                }
            }else{
                // update M1.
                Tree<T> left = node.getChildren().get(0);
                Tree<T> right = node.getChildren().get(1);
                Set<Tree<T>> leftSet = left.getExternalNodesSet();
                Set<Tree<T>> rightSet = right.getExternalNodesSet();
                Tree<T> child1 = null;
                Tree<T> child2 = null;
                for (Tree<T> leaf: extNodes){
//                    if (left.getExternalNodes().contains(leaf)){
                    if (leftSet.contains(leaf)){
                        child1 = left;
                        child2 = right;
                    }
                    else{
                        child1 = right;
                        child2 = left;
                    }
                    int value = M1.get(child1).get(leaf) + M.get(child2);
                    if (!M1.containsKey(node)) M1.put(node, new HashMap<Tree<T>, Integer>());
                    M1.get(node).put(leaf, value);
                }
                // update M2.
                Tree<T> vi = null;
                Tree<T> vj = null;
                for (Tree<T> leaf1: extNodes) {
                    for (Tree<T> leaf2 : extNodes) {
//                        if (left.getExternalNodes().contains(leaf1)) vi = left; else vi = right;
//                        if (left.getExternalNodes().contains(leaf2)) vj = left; else vj = right;
                        if (leftSet.contains(leaf1)) vi = left; else vi = right;
                        if (leftSet.contains(leaf2)) vj = left; else vj = right;
                        int value;
                        Tree<T> sibling;
                        if (vi == vj){
                            if (vi == left) sibling = right; else sibling = left;
                            value = M2.get(vi).get(leaf1).get(leaf2)+ M.get(sibling);
                        }else{
                            value = M1.get(vi).get(leaf1) + M1.get(vj).get(leaf2);
                        }
                        Map<Tree<T>, Integer> map2 = new HashMap<>();
                        Map<Tree<T>, Map<Tree<T>, Integer>> map1 = new HashMap<>();
                        if (!M2.containsKey(node)) M2.put(node, map1);
                        if (!M2.get(node).containsKey(leaf1)) M2.get(node).put(leaf1, map2);
                        M2.get(node).get(leaf1).put(leaf2, value);
                    }
                }
                // update M.
                int maxValue = Integer.MIN_VALUE;
                for (List<T> triple: incompatibleTriples){
                    Tree<T> a = tree.findNodeByValue(triple.get(0));
                    Tree<T> b = tree.findNodeByValue(triple.get(1));
                    Tree<T> c = tree.findNodeByValue(triple.get(2));
//                    if (!extNodes.contains(a) || !extNodes.contains(b) || !extNodes.contains(c))
                    if (!extNodesSet.contains(a) || !extNodesSet.contains(b) || !extNodesSet.contains(c))
                        continue;
//                    Tree<T> va = left.getExternalNodes().contains(a)? left: right;
//                    Tree<T> vb = left.getExternalNodes().contains(b)? left: right;
//                    Tree<T> vc = left.getExternalNodes().contains(c)? left: right;
                    Tree<T> va = leftSet.contains(a)? left: right;
                    Tree<T> vb = leftSet.contains(b)? left: right;
                    Tree<T> vc = leftSet.contains(c)? left: right;
                    int value = 0;
                    if (va == vb && va == vc){
                        continue;
                    }else {
                        if (va == vb) {
                            value = M2.get(va).get(a).get(b) + M1.get(vc).get(c) + 1;
                        }
                        if (vb == vc) {
                            value = M2.get(vb).get(b).get(c) + M1.get(va).get(a) + 1;
                        }
                        if (va == vc) {
                            value = M2.get(va).get(a).get(c) + M1.get(vb).get(b) + 1;
                        }
                    }
                    maxValue = Math.max(maxValue, value);
                }
                maxValue = Math.max(maxValue, M.get(left) + M.get(right));
                M.put(node, maxValue);
            }
            if (node == tree) break;
            node = TreeUtils.postOrderNext(node);
        }
        return Collections.max(M.values());
    }


    public static <T> List<List<T>> getIncompatibleTriples(Tree<T> reference, Tree<T> query) throws Exception {
        if (!reference.isTraverse()) reference.traverse();
        if (!query.isTraverse()) query.traverse();
        if (query.getExternalNodes().size() < 3)
            return new ArrayList<>();
        // ensure all leaves are identical.
        ArrayList<Tree<T>> extRef = reference.getExternalNodes();
        ArrayList<Tree<T>> extQuery = query.getExternalNodes();
        HashSet<T> leafLabelRef = new HashSet<>();
        HashSet<T> leafLabelQuery = new HashSet<>();
        for (Tree<T> node: extRef)
            leafLabelRef.add(node.getRoot());
        for (Tree<T> node: extQuery)
            leafLabelQuery.add(node.getRoot());
//        extRef.stream().forEach((x) -> leafLabelRef.add(x.getRoot()));
//        extQuery.stream().forEach((x) -> leafLabelQuery.add(x.getRoot()));
        if (!leafLabelQuery.equals(leafLabelRef))
            throw new Exception("Two trees should have same leaves.");
        T[] labels = (T[]) leafLabelQuery.toArray();
        List<List<T>> incompatibleTriples = new ArrayList<>();
        for (int i=0; i<labels.length-2; i++)
            for (int j=i+1; j<labels.length-1; j++) {
                for (int t = j + 1; t < labels.length; t++) {
                    Tree<T> aRef = reference.findNodeByValue(labels[i]);
                    Tree<T> bRef = reference.findNodeByValue(labels[j]);
                    Tree<T> cRef = reference.findNodeByValue(labels[t]);
                    Tree<T> aQuery = query.findNodeByValue(labels[i]);
                    Tree<T> bQuery = query.findNodeByValue(labels[j]);
                    Tree<T> cQuery = query.findNodeByValue(labels[t]);
                    HashSet<T> firstCoalRef = getFirstCoalescenceOfTriple(aRef, bRef, cRef);
                    HashSet<T> firstCoalQuery = getFirstCoalescenceOfTriple(aQuery, bQuery, cQuery);
                    if (!firstCoalQuery.equals(firstCoalRef)) {
                        List<T> array = new ArrayList<>();
                        array.add(labels[i]);
                        array.add(labels[j]);
                        array.add(labels[t]);
                        incompatibleTriples.add(array);
                    }
                }
            }
        return incompatibleTriples;
    }

    public static <T> HashSet<T> getFirstCoalescenceOfTriple(Tree<T> leaf1, Tree<T> leaf2, Tree<T> leaf3) throws Exception {
        // ensure leaves are coming from the same tree.
        if (leaf1.getFurthestAncestor() != leaf2.getFurthestAncestor() || leaf2.getFurthestAncestor() != leaf3.getFurthestAncestor())
            throw new Exception("Triples should be coming from the same tree.");
        HashSet<T> firstCoal = new HashSet<>();
        // two leaves are enough
        Tree<T>[] leaves = new Tree[]{leaf1, leaf2};
        for (Tree<T> leaf : leaves) {
            Tree<T> node = leaf;
            while (node != null) {
                firstCoal.clear();
                if (node.getExternalNodes().contains(leaf1)) firstCoal.add(leaf1.getRoot());
                if (node.getExternalNodes().contains(leaf2)) firstCoal.add(leaf2.getRoot());
                if (node.getExternalNodes().contains(leaf3)) firstCoal.add(leaf3.getRoot());
                if (firstCoal.size() == 2) return firstCoal;;
                node = node.getParent();
            }
        }
        return firstCoal;
    }

    // A much slower implementation for finding location of the first coalescent event.
    public static <T> HashSet<T> getFirstCoalescenceOfTriple1(Tree<T> leaf1, Tree<T> leaf2, Tree<T> leaf3) throws Exception {
        // ensure leaves are coming from the same tree.
        if (leaf1.getFurthestAncestor() != leaf2.getFurthestAncestor() || leaf2.getFurthestAncestor() != leaf3.getFurthestAncestor())
            throw new Exception("Triples should be coming from the same tree.");
        HashSet<T> firstCoal = new HashSet<>();
        Tree node = TreeUtils.postOrderNext(leaf1.getFurthestAncestor());
        while (node != leaf1.getFurthestAncestor()) {
            firstCoal.clear();
            if (node.getExternalNodes().contains(leaf1)) firstCoal.add(leaf1.getRoot());
            if (node.getExternalNodes().contains(leaf2)) firstCoal.add(leaf2.getRoot());
            if (node.getExternalNodes().contains(leaf3)) firstCoal.add(leaf3.getRoot());
            if (firstCoal.size() == 2) break;
            node = TreeUtils.postOrderNext(node);
        }
        return firstCoal;
    }


    //    public static <T> Tree<T> getAncestorOfTriple(Tree<T> leaf1, Tree<T> leaf2, Tree<T> leaf3) throws Exception {
//        // ensure leaves are coming from the same tree.
//        if (leaf1.getFurthestAncestor() != leaf2.getFurthestAncestor() || leaf2.getFurthestAncestor() != leaf3.getFurthestAncestor())
//            throw new Exception("Triple should be coming from the same tree.");
//
//    }


    /**
     *  Remove current node and reroot the tree as its parent.
     *  Input: A node within the tree (shouldn't be root)
     *  Result: A re-rooted tree
     * **/
    public static Tree<Integer> removeAndReroot(Tree<Integer> node) throws Exception {
        if (node.isRoot()){
            return null;
        }
        Tree<Integer> root = node.getParent();
        Tree<Integer> parent;
        Tree<Integer> previous = node;
        Tree<Integer> currentNode = node.getParent();
        while ((parent = currentNode.getParent()) != null){
            currentNode.delete(previous);
            if (previous == node){
                currentNode.setParent(null);
            }else{
                previous.addChild(currentNode);
            }
            previous = currentNode;
            currentNode = parent;
        }
        currentNode.delete(previous);
        Tree<Integer> remainTrees = currentNode.getChildren().get(0);
        previous.addChild(remainTrees);
        root.traverse();
        return root;
    }


    /**
     *  Only for constructing binary trees.
     *  Input: RENT+ output
     *  Result: An array of Tree
     * **/
    public static Tree<Integer>[] readFromNewick(File file) throws IOException {
        ArrayList<Tree<Integer>> trees = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null){
            if (!line.trim().equals("")) {
                Tree<Integer> tree = readFromNewick(line.trim().split("\t")[1]+";");
                trees.add(tree);
            }
        }
        return trees.toArray(new Tree[0]);
    }


    public static Tree<Integer> readFromNewick(String newick){
        Stack<Tree<Integer>> treeStack = new Stack<>();
        int index = 0;
        char currentChar;
        while ((currentChar = newick.charAt(index)) != ';'){
            if (currentChar >= '0' && currentChar <= '9') {
                StringBuilder name = new StringBuilder();
                char next;
                while ((next = newick.charAt(index)) >= '0' && next <= '9'){
                    name.append(next);
                    index++;
                }
                int id = Integer.parseInt(name.toString());
                Tree<Integer> tree = new Tree<>(id);
                treeStack.push(tree);
            }
            else if (currentChar == ')'){
                Tree<Integer> right = treeStack.pop();
                Tree<Integer> left = treeStack.pop();
                Tree<Integer> tree = new Tree<>(-1);
                left.setParent(tree);
                right.setParent(tree);
                tree.addChild(left);
                tree.addChild(right);
                treeStack.push(tree);
                index ++;
            }
            else {
                index ++;
            }
        }
        return treeStack.pop();
    }

    public static <T> Tree<T> copy(Tree<T> tree){
        if (tree.isLeaf())
            return new Tree<T>(tree.getRoot());
        Tree<T> root = new Tree<>(tree.getRoot());
        for (Tree<T> child: tree.getChildren()){
            root.addChild(TreeUtils.copy(child));
        }
        return root;
    }

}


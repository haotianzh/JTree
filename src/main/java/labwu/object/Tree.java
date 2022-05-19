package labwu.object;

import labwu.util.TreeUtils;

import java.util.*;

public class Tree<T> implements Cloneable{
    private T root;
    private int postID;
    private boolean ISTRAERSE;
    private int level;
    private Tree<T> parent;
    private ArrayList<Tree<T>> externalNodes;
    private ArrayList<Tree<T>> internalNodes;
    private Set<Tree<T>> externalNodesSet;
    private Set<Tree<T>> internalNodesSet;
    private ArrayList<Tree<T>> children;
    private HashMap<T, Tree<T>> leafTable;

    public Tree(T root){
        ISTRAERSE = false;
        this.root = root;
        this.parent = null;
        this.level = 0;
        children = new ArrayList<>();
    }

    private void setTraverse(boolean ISTRAERSE) {
        this.ISTRAERSE = ISTRAERSE;
    }

    public boolean isTraverse() {
        return ISTRAERSE;
    }

    public T getRoot(){
        return root;
    }

    public void update(){
        traverse();
    }

    public int getNodeCount(){
        return internalNodes.size() + externalNodes.size();
    }

    public ArrayList<Tree<T>> getExternalNodes() { return externalNodes; }

    public ArrayList<Tree<T>> getInternalNodes() {
        return internalNodes;
    }

    public Set<Tree<T>> getExternalNodesSet(){
        if (externalNodesSet != null){
            return externalNodesSet;
        }
        Set<Tree<T>> hashSet = null;
        if (externalNodes != null){
            System.out.println("sjdshfkshe");
            hashSet = new HashSet<>();
            hashSet.addAll(externalNodes);
            externalNodesSet = hashSet;
        }
        return externalNodesSet;
    }

    private void setExternalNodes(ArrayList<Tree<T>> externalNodes) {
        if (this.externalNodes == null){
            this.externalNodes = new ArrayList<>();
        }
        if (this.externalNodes.size() > 0){
            this.externalNodes.clear();
        }
        this.externalNodes.addAll(externalNodes);
    }

    private void setInternalNodes(ArrayList<Tree<T>> internalNodes) {
        if (this.internalNodes == null){
            this.internalNodes = new ArrayList<>();
        }
        if (this.internalNodes.size() > 0){
            this.internalNodes.clear();
        }
        this.internalNodes.addAll(internalNodes);
    }

    public int getLevel(){
        return level;
    }

    private void setLevel(int level) {
        this.level = level;
    }

    private void traverseLevel(){
        // bfs: O(n)
        Queue<Tree<T>> queue = new LinkedList<>();
        Queue<Integer> index = new LinkedList<>();
        queue.add(this);
        index.add(0);
        while (!queue.isEmpty()){
            Tree<T> current = queue.remove();
            int ind = index.remove();
//            System.out.println(ind);
            current.setLevel(ind);
            for (Tree<T> child : current.getChildren()){
                queue.add(child);
                index.add(ind+1);
            }
        }
    }

    public void traverse(){
        int id = 0;
        externalNodes = new ArrayList<>();
        internalNodes = new ArrayList<>();
        leafTable = new HashMap<>();
        // traverse for computing tree depth
        traverseLevel();
        Tree<T> node = TreeUtils.postOrderNext(this);
        while (node != this){
            if (node.isLeaf()){
                externalNodes.add(node);
                leafTable.put(node.getRoot(), node);
                ArrayList<Tree<T>> temp1 = new ArrayList<>();
                node.setInternalNodes(temp1);
                ArrayList<Tree<T>> temp2 = new ArrayList<>();
                temp2.add(node);
                node.setExternalNodes(temp2);
            }else{
                internalNodes.add(node);
                ArrayList<Tree<T>> temp1 = new ArrayList<>();
                ArrayList<Tree<T>> temp2 = new ArrayList<>();
                for (Tree<T> child : node.getChildren()){
                    temp1.addAll(child.getExternalNodes());
                    temp2.addAll(child.getInternalNodes());
                }
                temp2.add(node);
                node.setExternalNodes(temp1);
                node.setInternalNodes(temp2);
            }
            node.setID(id++);
            node.setTraverse(true);
            node = TreeUtils.postOrderNext(node);
        }
        setTraverse(true);
        setID(id);
        internalNodes.add(this);
    }


    public Tree<T> getMrcaOfTriple(Tree<T> leaf1, Tree<T> leaf2, Tree<T> leaf3) throws Exception {
        ArrayList<Tree<T>> leaves = new ArrayList<>();
        leaves.add(leaf1);
        leaves.add(leaf2);
        leaves.add(leaf3);
        return getMrca(leaves);
    }


    public Tree<T> getMrcaOfPair(Tree<T> leaf1, Tree<T> leaf2) throws Exception {
        ArrayList<Tree<T>> leaves = new ArrayList<>();
        leaves.add(leaf1);
        leaves.add(leaf2);
        return getMrca(leaves);
    }

    public Tree<T> getMrca(ArrayList<Tree<T>> leaves){
        boolean FLAG_EXIST = true;
        boolean FLAG_MRCA;
        Tree<T> mrca = TreeUtils.postOrderNext(leaves.get(0));
        for (Tree<T> leaf: leaves)
            if (!externalNodes.contains(leaf)) { FLAG_EXIST = false; break; }
        if (FLAG_EXIST){
            while (true){
                FLAG_MRCA = true;
                for (Tree<T> leaf: leaves)
                    if (!mrca.externalNodes.contains(leaf)) { FLAG_MRCA = false; break; }
                if (FLAG_MRCA || mrca==this) break;
                mrca = TreeUtils.postOrderNext(mrca);
            }
        }
        return mrca;
    }

    // get maximum number of none overlapping triples under a node v, pairwise triples are disjoint
    public int getMaxNumberOfNoneOverlappingTriples(){
        Tree<T> tree = this;
        Map<Tree<T>, Integer> M = new HashMap<>();
        Map<Tree<T>, Map<Tree<T>, Integer>> M1 = new HashMap<>();
        Map<Tree<T>, Map<Tree<T>, Map<Tree<T>, Integer>>> M2 = new HashMap<>();
        // post-traverse the tree and perform dynamic programming.
        if (!tree.isTraverse()) tree.traverse();
        Tree<T> node = TreeUtils.postOrderNext(tree);
        while(true){
            ArrayList<Tree<T>> extNodes = node.getExternalNodes();
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
                Tree<T> child1 = null;
                Tree<T> child2 = null;
                for (Tree<T> leaf: extNodes){
                    if (left.getExternalNodes().contains(leaf)){
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
                        if (left.getExternalNodes().contains(leaf1)) vi = left; else vi = right;
                        if (left.getExternalNodes().contains(leaf2)) vj = left; else vj = right;
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
                for (int p=0; p<extNodes.size()-2; p++)
                    for (int q=p+1; q<extNodes.size()-1; q++)
                        for (int r=q+1; r<extNodes.size(); r++){
                            Tree<T> a = extNodes.get(p);
                            Tree<T> b = extNodes.get(q);
                            Tree<T> c = extNodes.get(r);
                            Tree<T> va = left.getExternalNodes().contains(a)? left: right;
                            Tree<T> vb = left.getExternalNodes().contains(b)? left: right;
                            Tree<T> vc = left.getExternalNodes().contains(c)? left: right;
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

    public Tree<T> findNodeByValue(T value) throws Exception {
        if (!leafTable.containsKey(value)){
            throw new Exception("Leaf not found.");
        }
        return leafTable.get(value);
    }

    public HashMap<T, Tree<T>> getLeafTable(){
        return leafTable;
    }

    public void setParent(Tree<T> parent){
        this.parent = parent;
    }

    public Tree<T> getParent(){
        return parent;
    }

    public void setID(int postID){
        this.postID = postID;
    }

    public int getID(){
        return postID;
    }

    public Tree<T> getFurthestAncestor(){
        Tree<T> ancestor = this;
        while (ancestor.getParent() != null){
            ancestor = ancestor.getParent();
        }
        return ancestor;
    }

    public ArrayList<Tree<T>> getChildren(){
        return children;
    }

    public int getChildrenSize(){
        return children.size();
    }

    public void invalidateTraverse(){
        Tree<T> parent = this;
        while (parent != null){
            parent.setTraverse(false);
            parent = parent.getParent();
        }
    }

    public void addChild(Tree<T> child){
//        setTraverse(false);
        invalidateTraverse();
        children.add(child);
        child.setParent(this);
    }

    public boolean isRoot(){
        return parent == null;
    }

    public boolean isLeaf(){
        return children.size() == 0;
    }

    public void delete(Tree<T> child) throws Exception {
        if (!children.contains(child)){
            throw new Exception("Child not found.");
        }else{
            invalidateTraverse();
            child.setParent(null);
            child.setLevel(0);
            children.remove(child);
        }
    }


    public String newick(){
        return newick(0);
    }

    // For example, reindex taxa from 1..10 to 0..9
    public String newick(int offset){
        StringBuilder sb = new StringBuilder();
        if (isLeaf()){
            String s = String.valueOf(getRoot());
            return String.valueOf(Integer.parseInt(s)-1);
        }
        sb.append("(");
        for (Tree child : getChildren()){
            sb.append(child.newick());
            sb.append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append(")");
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("");
        output.append(root);
        output.append(": {");
        for(Tree<T> child: children){
            output.append(child.toString()).append(",");
        }
        output.setCharAt(output.length()-1, '}');
        return (getChildrenSize()==0)? ""+root: output.toString();
    }
}

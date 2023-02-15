import labwu.object.Cluster;
import labwu.object.Tree;
import labwu.thread.RFRunnable;
import labwu.util.TreeUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


class RFParallelRunnable implements Runnable{
    private HashSet<BitSet>[] all;
    private int from;
    private File file;
    public RFParallelRunnable(File file){
        this.file = file;
    }
    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            Test.rfRuntime(file);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}

public class Test {
    public void reIndex(File file, int offset) throws IOException {
        Tree<Integer>[] trees = new Tree[0];
        trees = TreeUtils.readFromNewick(file);
        FileWriter writer = new FileWriter(new File(file.getAbsolutePath()+".reindex"));
        for (Tree tree : trees){
            writer.write(tree.newick(offset));
            writer.write("\n");
        }
        writer.close();
    }

    public void method1(String fileName) throws Exception {
        File file = new File(fileName);
        Tree<Integer>[] trees = new Tree[0];
        try {
            trees = TreeUtils.readFromNewick(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Tree<Integer>[] rerooted = new Tree[trees.length];
        Cluster[] clusters = new Cluster[trees.length];
        for (int i=0; i<trees.length; i++){
            Tree last = null;
            trees[i].traverse();
            for (Tree node: trees[i].getExternalNodes()){
                if ((int)node.getRoot() == trees[i].getExternalNodes().size()){
                    last = node;
                    break;
                }
            }
            rerooted[i] = TreeUtils.removeAndReroot(last);
            clusters[i] = TreeUtils.buildClusters(rerooted[i]);

        }
        for (int i=0; i< trees.length-1; i++){
            for (int j=i+1; j< trees.length; j++){
                double dis = TreeUtils.robinsonFoulds(clusters[i], rerooted[j]);
                System.out.print(dis + " ");
            }
            System.out.println();
        }
    }


    public void method2(String fileName){
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
                System.out.print(dis + " ");
            }
            System.out.println();
        }
    }

    public void method3(String fileName) throws IOException, InterruptedException {
        File file = new File(fileName);
        Thread thread = new Thread(new RFRunnable(file, false));
        thread.start();
        thread.join();
    }

    public static void rfRuntime(File file) throws InterruptedException{
        long start = System.currentTimeMillis();
        // File file = new File(fileName);
        try {
            Tree<Integer>[] trees;
            trees = TreeUtils.readFromNewick(file);
            HashSet<BitSet>[] all = new HashSet[trees.length];
            for (int i=0; i<trees.length; i++){
                all[i] = TreeUtils.getHashedSplits(trees[i]);
            }
            for(int i=0; i<(all.length-1); i++){
                // executorService.execute(new RFParallelRunnable(all, i));
                for(int j=i+1; j<Math.min(i+50, all.length); j++) {
                    double dis = TreeUtils.robinsonFoulds(all[i], all[j]);
                //     // System.out.print(dis + " ");
                }
                // System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // ExecutorService executorService = Executors.newFixedThreadPool(2);


        // executorService.shutdown();
        // executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);


        // long end  = System.currentTimeMillis();
        // System.out.println("runtime: " + (end-start));
    }



    public static void main(String[] args) throws Exception {

        ArrayList<String[]> arr = new ArrayList<>();
        arr.add(new String[]{"1","2"});
        arr.add(new String[]{"1","2","3"});
        

    //     long start = System.currentTimeMillis();
    //     String fileName = "JTree/data/1.txt.trees";
    //     File file = new File(fileName);
    //     Test test = new Test();
        
    //     ExecutorService executorService = Executors.newFixedThreadPool(24);
    //     executorService.execute(new RFParallelRunnable(file));
    //     executorService.execute(new RFParallelRunnable(file));
    //     executorService.execute(new RFParallelRunnable(file));
    //     executorService.execute(new RFParallelRunnable(file));
    //     executorService.execute(new RFParallelRunnable(file));
    //     executorService.execute(new RFParallelRunnable(file));
    //     executorService.execute(new RFParallelRunnable(file));
    //     executorService.execute(new RFParallelRunnable(file));
    //     executorService.execute(new RFParallelRunnable(file));
    //     executorService.execute(new RFParallelRunnable(file));
    //     executorService.execute(new RFParallelRunnable(file));
    //     executorService.execute(new RFParallelRunnable(file));
    //     executorService.execute(new RFParallelRunnable(file));
    //     executorService.execute(new RFParallelRunnable(file));
    //     executorService.execute(new RFParallelRunnable(file));
    //     executorService.execute(new RFParallelRunnable(file));
    //     executorService.execute(new RFParallelRunnable(file));
    //     executorService.execute(new RFParallelRunnable(file));
    //     executorService.execute(new RFParallelRunnable(file));

    //     executorService.shutdown();
    //     executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
    //    long end=System.currentTimeMillis();
    //    System.out.println("runtime: "+(end-start)+"ms");
//        long start=System.currentTimeMillis();
//        new Test().method1(fileName);
//        long end=System.currentTimeMillis();
//        System.out.println("runtime: "+(end-start)+"ms");
//        long start=System.currentTimeMillis();
//        new Test().method3(fileName);
//        long end=System.currentTimeMillis();
//        System.out.println("runtime: "+(end-start)+"ms");

//        new Test().method2(fileName);
//        end=System.currentTimeMillis();
//        System.out.println("runtime: "+(end-start)+"ms");
//        long start=System.currentTimeMillis();
//        Tree<Integer> tree1 = TreeUtils.readFromNewick("((1,2),(3,4));");
//        System.out.println(tree1.newick());
//        Tree<Integer> tree2 = TreeUtils.readFromNewick("((1,3),(2,4));");
//        tree1.traverse();
//        tree2.traverse();
//        HashSet<BitSet> split1 = TreeUtils.getHashedSplits(tree1);
//        HashSet<BitSet> split2 = TreeUtils.getHashedSplits(tree2);
//        double a = TreeUtils.robinsonFoulds(split1, split2);
//        long end=System.currentTimeMillis();
//        System.out.println(a);
//        System.out.println("runtime: "+(end-start)+"ms");
        // String dirName = args[0];
        // String fileFormat = "data/%s.txt.trees";
        // File dir = new File(dirName);
        // Test test = new Test();
        // for (File f: dir.listFiles(new FilenameFilter() {
        //     @Override
        //     public boolean accept(File dir, String name) {
        //         return name.toLowerCase().endsWith(".trees");
        //     }
        // })){
        //     test.rfRuntime(f);
        // }
//        File file = new File(String.format(fileFormat, 10));
//        new Test().reIndex(file, 1);
    }

}

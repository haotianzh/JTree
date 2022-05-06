import java.io.File;
import java.util.ArrayList;

import pal.tree.Tree;
import treecmp.metric.RFMetric;
import treecmp.io.TreeReader;

class MyThread1 extends Thread{
    private String fileName;
    public MyThread1(String fileName){
        this.fileName = fileName;
    }
    public void run(){
        TreeReader reader = new TreeReader(fileName);
        reader.open();
        ArrayList<Tree> trees = new ArrayList<Tree>();
        Tree tmp;
        while ((tmp = reader.readNextTree())!=null){
            trees.add(tmp);
        }
        reader.close();
        for(int i=0; i<trees.size()-1; i++){
            for(int j=i+1; j<trees.size(); j++) {
                double dis = RFMetric.getRFDistance(trees.get(i), trees.get(j));
//                System.out.print(dis + " ");
            }
//            System.out.println();
        }
        System.out.println(fileName + " Done!");
    }
}

public class ComputeRobinsonFoulds {
    public void run(String fileName){
        TreeReader reader = new TreeReader(fileName);
        reader.open();
        ArrayList<Tree> trees = new ArrayList<Tree>();
        Tree tmp;
        while ((tmp = reader.readNextTree())!=null){
            trees.add(tmp);
        }
        reader.close();
        for(int i=0; i<trees.size()-1; i++){
            for(int j=i+1; j<trees.size(); j++) {
                double dis = RFMetric.getRFDistance(trees.get(i), trees.get(j));
                System.out.print(dis + " ");
            }
            System.out.println();
        }
        System.out.println(fileName + " Done!");
    }
    public static void main(String[] args) throws InterruptedException {
        long start=System.currentTimeMillis();

//        String dirName = "data/";
//        File dir = new File(dirName);
//        for (File f: dir.listFiles()){
//            MyThread1 thread = new MyThread1(dirName + f.getName());
//            thread.start();
//            thread.join();
//        }
//        MyThread thread = new MyThread("data/test - 副本.txt");
//        thread.start();
        String fileName = "C:/Users/zht/Desktop/Files_on_unix/0.txt.trees.1";
        new ComputeRobinsonFoulds().run(fileName);
        long end=System.currentTimeMillis();
        System.out.println("runtime: "+(end-start)+"ms");
//        System.out.println(RFMetric.getRFDistance(trees.get(0), trees.get(0)));
    }
}

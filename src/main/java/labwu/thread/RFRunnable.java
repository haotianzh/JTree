package labwu.thread;

import labwu.object.Tree;
import labwu.util.TreeUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashSet;

public class RFRunnable implements Runnable {
    private File file;
    private FileWriter writer;
    private boolean ifWrite=true;
    public RFRunnable(File file, boolean ifWrite) throws IOException {
        this.file = file;
        this.ifWrite = ifWrite;
        if (ifWrite) writer = new FileWriter(new File(file.getAbsolutePath() + ".out"));

    }
    @Override
    public void run() {
        Tree<Integer>[] trees;
        try {
            trees = TreeUtils.readFromNewick(file);
            HashSet<BitSet>[] all = new HashSet[trees.length];
            for (int i=0; i<trees.length; i++){
                all[i] = TreeUtils.getHashedSplits(trees[i]);
            }
            for(int i=0; i<(all.length-1); i++){
                for(int j=i+1; j<all.length; j++) {
                    double dis = TreeUtils.robinsonFoulds(all[i], all[j]);
                    if (ifWrite) writer.write(dis + " "); else System.out.print(dis + " ");
                }
                if (ifWrite) writer.write("\n"); else System.out.println();
            }
            if (ifWrite) writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

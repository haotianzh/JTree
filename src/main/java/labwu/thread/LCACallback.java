package labwu.thread;

public interface LCACallback {
    // Used for calling back and storing values.
    void onFinish(int index, int[][] lca);
}

package labwu.thread;

public interface Callback {
    // Used for calling back and storing values.
    void onFinish(int index, double[][] dist);
}

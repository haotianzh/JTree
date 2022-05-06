import labwu.thread.RFRunnable;

import org.apache.commons.cli.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ComputeRF{
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("d", "dir", true, "directory of .trees files.");
        options.addOption("c", "cpu", true, "cpu counts for use.");
        options.addOption("w", "write", false, "if write to file.");
        HelpFormatter formatter = new HelpFormatter();
        CommandLineParser parser = null;
        CommandLine cmd = null;
        String dirName = "";
        boolean ifWrite = false;
        double OPTIMAL_CPU_USAGE = 0.75;
        int MAX_CPU_COUNT = Runtime.getRuntime().availableProcessors();
        int cpuCount = -1;
        try {
            parser = new DefaultParser();
            cmd = parser.parse(options, args);
            if (cmd.hasOption("dir")){
                dirName = cmd.getOptionValue("dir");
            }else{
                throw new Exception("No dir provided.");
            }
            if (cmd.hasOption("cpu")){
                int cpu = Integer.parseInt(cmd.getOptionValue("cpu"));
                cpuCount = Math.min(cpu, MAX_CPU_COUNT);
            }else{
                cpuCount = (int) (MAX_CPU_COUNT * OPTIMAL_CPU_USAGE);
            }
            if (cmd.hasOption("write")) ifWrite = true;
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            formatter.printHelp( "java -jar ComputeRF.jar -d [Dir] -c [CPUS]", options );
            System.exit(1);
        }

        try {
            long start=System.currentTimeMillis();
            ExecutorService executorService = Executors.newFixedThreadPool(cpuCount);
            File dir = new File(dirName);
            for (File f: dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".trees");
                }
            })){
                RFRunnable thread = new RFRunnable(f, ifWrite);
                executorService.execute(thread);
            }
            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
            long end=System.currentTimeMillis();
            System.out.println("ComputeRF " + dirName +  " runtime: "+(end-start)+"ms");
        }
        catch (IOException | InterruptedException e){
            System.out.println(e.getMessage());
        }
    }
}

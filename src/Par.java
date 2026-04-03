import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

public class Par {

    public static void runPar(String hash, String hash_type, String char_set, int pwd_length, JProgressBar progress, String PATH) throws NoSuchAlgorithmException, IOException {

        System.out.println("[input hash]: " + hash + " \n[hash_type]: " + hash_type + " \n[char_set]: " + char_set + " \n[length]: " + pwd_length + "\n");
        Pattern pattern = Pattern.compile("^" + char_set + "+$");
        progress.setString("Reading dictionary..");

        char[] char_set_arr = Functions.createCharSet(char_set); // ✓
        long possible_combs = (long) Math.pow(char_set_arr.length, pwd_length);

        long lines = 0; // kr rabim total lines of n length, ne vse
        try (BufferedReader reader = new BufferedReader(new FileReader(PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() == pwd_length && pattern.matcher(line).matches()) {
                    lines++;
                }
            }
        }

        // BUFFERED READER SET-UP
        BufferedReader rdr;
        try {
            rdr = new BufferedReader(new FileReader(PATH));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        progress.setString("Dictionary attack..");

        int attempts = 1;
        long t, t0 = System.currentTimeMillis();


        /// DICTIONARY ATTACK - Imma leave this one sequential
        String currentLine;
        long currentprogress = 0;
        while (true) {
            if (!((currentLine = rdr.readLine()) != null)) break; // break when reached end of dictionary
            if (currentLine.length() != pwd_length) continue; // skip candidates that are not the right length
            if (!pattern.matcher(currentLine).matches())
                continue; // skip candidates that dont fit the specified char set
            System.out.println("[" + attempts + "] " + "Dictionary entry: " + currentLine);
            String dict_hash = Functions.hash_it(currentLine, hash_type);
            if (dict_hash.equalsIgnoreCase(hash)) break; // gredol
            attempts++;
            currentprogress++;
            int percent = Functions.computeProgress(currentprogress, lines);
            SwingUtilities.invokeLater(() -> { // to update the progress bar in the gui
                progress.setValue(percent);
            });
        }

        t = System.currentTimeMillis() - t0;

         /// DICTIONARY ATTACK SUCCEEDS
        if (!((currentLine) == null)) {
            t = System.currentTimeMillis() - t0;
            progress.setValue(100);
            progress.setString("Success");
            System.out.println("[Dictionary attack] success.\n[pwd]: " + currentLine + " \n[time]: " + Functions.time(t) + " \n[attempts]: " + attempts);
            parGUI.enableButtons();

        } else {

            ///  PARALLEL
            // - create fixed number of threads with executor service
            // - divide workload into n
            // - use AtomicInteger to increment attempts -> progress
            //      - have one thread for gui only

            int cores = Runtime.getRuntime().availableProcessors()-1; // leaving 1 core for OS*
            ExecutorService pool = Executors.newFixedThreadPool(cores);

            AtomicLong attempts2 = new AtomicLong(attempts);
            AtomicBoolean found = new AtomicBoolean(false);
            //AtomicReference<String> resultPassword = new AtomicReference<>(null);

            // This is just to divide the char_set_arr (how bug the range is)
            // such that each threads gets about the same amount of work

            System.out.println("[cores]: " + cores + " [length]: " + char_set_arr.length);
            // this currently just returns the length of the chunk
            int[] chunk_size = Functions.divideChunk(cores, char_set_arr.length);
            // still need to define the actual chunks

//            System.out.print("The ranges are: ");
//            for (int i = 0; i < ranges.length; i++) {
//                System.out.print(ranges[i]+ " ");
//            }

            // ranges[x][] - the ranges; ranges[][0/1] - start/end char
            char[][] ranges = Functions.getRangeBounds(char_set_arr, chunk_size);
            for (int i = 0; i < ranges.length; i++) {
                System.out.println("Ranges: " + ranges[i][0] + " " + ranges[i][1]);
            }

            String pwd_and_attempt = "";

            /// BRUTE FORCE ATTACK
            System.out.println("[Dictionary attack] failed. [time]: " + Functions.time(t));
            System.out.println("[Brute force attack] started."); progress.setValue(0);
            progress.setString("Brute force attack.."); currentprogress = 0;
            System.out.println("[possible combinations]: " + possible_combs + ". Please be patient");
            /// ⚠️⚠⚠⚠
            AtomicReference<String> test = new AtomicReference<>(new String());
            for (int i = 0; i < cores; i++) {

                char startChar = ranges[i][0];
                char endChar = ranges[i][1];
                int j = i;

                pool.submit(() -> {
                    System.out.println("Thread[" + j + "] starting");
                    try {
                        test.set(Functions.parallelBruteForceGenerator(pwd_length, char_set_arr, startChar, endChar, hash, hash_type, attempts2, j, found, progress));
                        System.out.println("Thread[" + j + "] finished cooking");
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                });
            }
            while(!found.get()){
                SwingUtilities.invokeLater(() -> {//
                    int percent = Functions.computeProgress(attempts2.get(), possible_combs);
                    progress.setValue(percent);
                });
            }
            try {
                pool.awaitTermination(60, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                    throw new RuntimeException(e);
            }
            pool.shutdown();
            /// ⚠⚠⚠️
            t = System.currentTimeMillis() - t0;
            System.out.println("[Brute force attack] success.\n[pwd]: " + test + " \n[time]: " + Functions.time(t) + " \n[attempts]: " + attempts2.get());
            parGUI.enableButtons();

        }
    }

}

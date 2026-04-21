import javax.swing.*;
import mpi.*;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;


public class Distr {

    // THIS IS RAN BY ROOT
    public static void runDistr(String hash, String hash_type, String char_set, int pwd_length, JProgressBar progress, String PATH, int me, int nodes) throws NoSuchAlgorithmException, IOException{

        ///  Root has all the info
        ///  Dont forget DICTIONARY ATTACK [✔]
        ///  Needs to split char_set into ranges just like for parallel
        ///  Bcast each range to each worker
        ///     What needs to be bcasted?
        ///     Actually scatter
        ///  Send the range

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
        BufferedReader rdr;
        try {
            rdr = new BufferedReader(new FileReader(PATH));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        progress.setString("Dictionary attack..");

        int attempts = 1;
        long t, t0 = System.currentTimeMillis();

        /// DICTIONARY ATTACK - Sequential
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
            MPI.Finalize();
        } else {
            /// CHUNKS - RANGES
            int[] chunk_size = Functions.divideChunk(nodes, char_set_arr.length);
            char[][] ranges = Functions.getRangeBounds(char_set_arr, chunk_size);
            for (int i = 0; i < ranges.length; i++) {
                System.out.println("Ranges: " + ranges[i][0] + " " + ranges[i][1]);
            }

            /// MPJ

            // Broadcast  (thing, offset(prob 0), count, type MPI.INT, who's bcasting 0)
            // pwd_length, char_set, starChar, endChar, hash, hash_type, attempts?, found???, progressbar?????

            MPI.COMM_WORLD.Bcast(pwd_length, 0, 1, MPI.INT, 0); // ⬤
            MPI.COMM_WORLD.Bcast(char_set, 0, 1, MPI.INT, 0); // ⬤
            MPI.COMM_WORLD.Bcast(startChar, 0, 1, MPI.INT, 0); // ⬤
            MPI.COMM_WORLD.Bcast(endChar, 0, 1, MPI.INT, 0); // ⬤
            MPI.COMM_WORLD.Bcast(hash, 0, 1, MPI.INT, 0); // ⬤
            MPI.COMM_WORLD.Bcast(hash_type, 0, 1, MPI.INT, 0); // ⬤

            // Scatter?

            // Gather


        }




    }

    public static void compute(String hash, String hash_type, String char_set, int pwd_length, JProgressBar progress, String PATH, int me, int nodes){


        /// WORKER
        ///  needs: pwd_length, char_set, starChar, endChar, hash, hash_type, attempts?, found???, progressbar?????
        ///  Can I make it quit after any of the workera(or root) find the hash? Maybe while loop. Need it for Found

        public static String parallelBruteForceGenerator(int pwd_length, char[] char_set, char startChar, char endChar, String hash, String hash_type, AtomicLong
        attempts, int nThread, AtomicBoolean found, JProgressBar progress, long t, long t0) throws NoSuchAlgorithmException {

    }

}

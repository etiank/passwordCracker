import javax.swing.*;
import mpi.*;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.regex.Pattern;


public class Distr {

    // THIS IS RAN BY ROOT
    public static void runDistr(String hash, String hash_type, String char_set, int pwd_length, JProgressBar progress, String PATH, int me, int nodes) throws NoSuchAlgorithmException, IOException{

        ///  Root has all the info
        ///  Dont forget DICTIONARY ATTACK
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
        long t, t0 = System.currentTimeMillis(), t_total = System.currentTimeMillis();

        /// DICTIONARY ATTACK - Sequential
        String currentLine;
        long currentprogress = 0;
        while (true) {
            if (!((currentLine = rdr.readLine()) != null)) break; // break when reached end of dictionary
            if (currentLine.length() != pwd_length) continue; // skip candidates that are not the right length
            if (!pattern.matcher(currentLine).matches())
                continue; // skip candidates that dont fit the specified char set
//            System.out.println("[" + attempts + "] " + "Dictionary entry: " + currentLine);
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
            Main.enableButtons();
            MPI.Finalize();
        } else {
            /// CHUNKS - RANGES
            System.out.println("[Dictionary attack] failed. [time]: " + Functions.time(t));
            System.out.println("[Brute force attack] started."); progress.setValue(0);
            progress.setString("Brute force attack..");
            System.out.println("[possible combinations]: " + possible_combs + ". Please be patient");
            int[] chunk_size = Functions.divideChunk(nodes, char_set_arr.length);
            char[][] ranges = Functions.getRangeBounds(char_set_arr, chunk_size);
            for (int i = 0; i < ranges.length; i++) {
                System.out.println("Ranges: " + ranges[i][0] + " " + ranges[i][1]);
            }


//            System.out.println("ass:\n" + Arrays.toString(Functions.flattenMatrix(ranges)));


            /// MPJ

            // BROADCAST  (thing, offset(prob 0), count, type MPI.INT, who's bcasting 0)
            // pwd_length ✔, char_set ✔, range, hash ✔, hash_type ✔, attempts?, found???, progressbar?????
            // ONLY ARRAYS CAN BE SENT
            System.out.println("["+me+"] ROOT SENDING");
            int[] pwd_length_buffer = new int[1]; pwd_length_buffer[0] = pwd_length;
            MPI.COMM_WORLD.Bcast(pwd_length_buffer, 0, 1, MPI.INT, 0); // ⬤
            System.out.println("["+me+"] SENT pwd_length");

            int[] hash_length_buffer = new int[hash.length()]; hash_length_buffer[0] = hash.length();
            MPI.COMM_WORLD.Bcast(hash_length_buffer, 0, 1, MPI.INT, 0); // ⬤
            System.out.println("["+me+"] SENT hash_length");

            char[] hash_buffer = hash.toCharArray();
            MPI.COMM_WORLD.Bcast(hash_buffer, 0, hash.length(), MPI.CHAR, 0); // ⬤
            System.out.println("["+me+"] SENT hash: " + hash);

            int[] char_set_arr_length = new int[1]; char_set_arr_length[0] = char_set_arr.length;
            MPI.COMM_WORLD.Bcast(char_set_arr_length, 0, 1, MPI.INT, 0); // ⬤
            System.out.println("["+me+"] SENT char_set_length: " + char_set_arr.length);

            MPI.COMM_WORLD.Bcast(char_set_arr, 0, char_set_arr.length, MPI.CHAR, 0); // ⬤
            System.out.println("["+me+"] SENT char_set_arr: " + Arrays.toString(char_set_arr));

            int[] hash_type_length = new int[1]; hash_type_length[0] = hash_type.length();
            MPI.COMM_WORLD.Bcast(hash_type_length, 0, 1, MPI.INT, 0); // ⬤
            System.out.println("["+me+"] SENT hash_type_length: " + hash_type.length());

            char[] hash_type_buffer = hash_type.toCharArray();
            MPI.COMM_WORLD.Bcast(hash_type_buffer, 0, hash_type.length(), MPI.CHAR, 0); // ⬤
            System.out.println("["+me+"] SENT hash_type: " + Arrays.toString(hash_type_buffer));

            // SCATTER
            //send chunksize
            char[] recvBuffer = new char[2]; char[] sendBuff = Functions.flattenMatrix(ranges);
            MPI.COMM_WORLD.Scatter(
                    sendBuff, 0, 2, MPI.CHAR,
                    recvBuffer, 0, 2, MPI.CHAR, 0);  // ⬤
            System.out.println("["+me+"] SENT ranges: " + Arrays.toString(sendBuff));
            System.out.println("["+me+"] ROOT range from " + sendBuff[0] + " to " + sendBuff[1]);
            String[] result;

            System.out.println("["+me+"] ROOT TEST" );
            result = Functions.distributedBruteForceGeneratorRoot(pwd_length, char_set_arr, sendBuff[0], sendBuff[1], hash, hash_type, me, progress, nodes);
            t = System.currentTimeMillis() - t0;
            System.out.println("["+me+"] RESULT: " + result[0]);
            System.out.println("["+me+"] LOCAL ATTEMPTS: " + result[1]);
            SwingUtilities.invokeLater(() -> {//
                progress.setValue(100);
                progress.setString("Success");
            });

            // GATHER password & attempts

            long[] total_attempts = new long[nodes];  long[] root_attempts = new long[1]; root_attempts[0] = attempts + Long.parseLong(result[1]);

            MPI.COMM_WORLD.Reduce(
                    root_attempts, 0, total_attempts, 0, 1, MPI.LONG, MPI.SUM, 0
            );

            System.out.println("["+me+"] TOTAL ATTEMPTS: " + total_attempts[0]);
            System.out.println("┌──────────────────────────────────────────────┐");
            System.out.println("│ PASSWORD: " + result[0]);
            System.out.println("│ ATTEMPTS: " + total_attempts[0]);
            System.out.println("│ DISTRIBUTED TIME: " + t);
            System.out.println("│ TOTAL TIME:" + t);
            System.out.println("└──────────────────────────────────────────────┘");



        }
    }

    public static void compute(String hash, String hash_type, String char_set, int pwd_length, JProgressBar progress, String PATH, int me, int nodes){


        /// WORKER
        ///  needs: pwd_length, char_set, starChar, endChar, hash, hash_type, attempts?, found???, progressbar?????
        ///  Can I make it quit after any of the workera(or root) find the hash? Maybe while loop. Need it for Found

//        public static String parallelBruteForceGenerator(int pwd_length, char[] char_set, char startChar, char endChar, String hash, String hash_type, AtomicLong
//        attempts, int nThread, AtomicBoolean found, JProgressBar progress, long t, long t0)

    }

}

import mpi.MPI;
import mpi.Status;

import javax.swing.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Functions {

    /// Gets string to hash and the hashing alg to use, and returns the digest. ✓
    public static String hash_it(String hash, String hashType) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance(hashType); // initialize it
        //md.update(hash.getBytes()); // to update the msg digest
        byte[] digest = md.digest(hash.getBytes());
        //String output = new String(digest);
        Formatter formatter = new Formatter();  // ┐
        for (byte d : digest){                  // │
            formatter.format("%02x", d);        // │    gregor
        }                                       // ┘
        String output = formatter.toString();
        formatter.close();
        return output;
    }

    /// Just returns the progress in a percentage to be able to update the JProgressBar. ✓
    public static int computeProgress(long currentProgress, long lines){
        int output = (int) (currentProgress * 100 / lines);
        return output;
    }

    /// Just converts the time from simply ms to also seconds and minutes if applicable ✓
    public static String time(long ms) {
        if (ms < 1000) return ms + "ms.";

        long seconds = ms / 1000;
        if (seconds<60) return seconds + "s " + (ms % 1000) + "ms.";

        long minutes = seconds / 60;
        if (minutes < 60) return minutes + " min " + (seconds % 60) + "s " + (ms % 1000) + "ms";
        return "";
    }

    /// Gets the regex-style char set definition, expands it and returns a char array ✓
    public static char[] createCharSet(String input){ // ✓
        char[] char_set = new char[input.length()];
        List<Character> char_list = new ArrayList<>();
        String stripped_input = input.replaceAll("[\\[\\]]", "");
        //System.out.println("stripped input: " + stripped_input);

        if (input.equals(".")) {    // every character
            for (char c = 32; c < 127; c++) { // ASCII
                char_list.add(c);
                //System.out.print("test" + char_list.get(c-1));
            }
            return toCharArr(char_list);
        }

        for (int i = 0; i < stripped_input.length(); i++) {
            char c = stripped_input.charAt(i);

            if (i + 2 < stripped_input.length() && stripped_input.charAt(i + 1) == '-') {   // ┐
                char end = stripped_input.charAt(i + 2);                                    // │    gpt
                for (char c1 = c; c1 <= end; c1++) {                                        // │
                    char_list.add(c1);                                                      // │
                }                                                                           // │
                i += 2;                                                                     // │
            } else  char_list.add(c);                                                       // ┘
        }
        return toCharArr(char_list);
    }

    /// The function that converts a character list into a char array ✓
    public static char[] toCharArr(List<Character> input){ // ✓
        char[] char_set = new char[input.size()];
            for (int i = 0; i < input.size(); i++) {
                char_set[i] = input.get(i);
            }
            return char_set;
    }

    /// Needs to take in the pwd_length, char_set, original hash, hasy_type.
    /// Iteratively generate string, hash it and compare it to the orignal hash: andrej's predlog btw
    public static String bruteForceGenerator(int pwd_length, char[] char_set, String hash, String hash_type, long possible_combs, /*long attempts,*/ long currentProgress, JProgressBar progress) throws NoSuchAlgorithmException {
        long attempts = 0;
        int[] indices = new int[pwd_length]; // initializing
        char[] currentGuess = new char[pwd_length];
        Arrays.fill(currentGuess, char_set[0]); // fill the char arr with the first char in char_set
        String password = "";

        while (true){ // compare the current guess
            String strGuess = new String(currentGuess);
            //System.out.println("["+attempts + "] guess: " + strGuess + " total combs: " + possible_combs);
            if (hash_it(strGuess, hash_type).equalsIgnoreCase(hash)){
                password = strGuess; break;}

            // Iterate string
            int i = pwd_length - 1; // start at last char
            while (i >= 0){ // while password length is not 0
                indices[i]++; // increment the last (from left to right) char in the indices
                if (indices[i] < char_set.length){ // if last index didnt yet reach the end of the char_set
                    currentGuess[i] = char_set[indices[i]]; // update current guess' char with the new char
                    break;
                } else {
                    indices[i] = 0;
                    currentGuess[i] = char_set[0];
                    i--; // reset and move to the next digit
                }
            }
            attempts++; currentProgress++; // update progress bar

            int percent = Functions.computeProgress(currentProgress, possible_combs);
            SwingUtilities.invokeLater(() -> {
                progress.setValue(percent);
            });

            if(i < 0) break; // if this is reached we've been through all possible combinations7
        }

        return password + "\n" + attempts;
    }

    ///  PAR


    /// This function uses the number of available cores and amount of characters
    /// in the char_set_arr to compute how big the ranges that are given to each
    /// thread are.
    public static int[] divideChunk(int cores, int length){ //6, 26
        System.out.println("cores: " + cores + " length: " + length);
        int range = length / cores; // 4.3

        int remainder = length % cores;
        int[] rangeArr = new int[cores];
        //int[] remaindersArr = new int[remainder];

        for (int i = 0; i < cores; i++) {
            // every core gets remainder cores get an extra character
            rangeArr[i] = range + (i < remainder ? 1 : 0);
        }
        return rangeArr;
    }

    public static char[][] getRangeBounds(char[] charSet, int[] ranges) {
        // 0 - startChar, 1 - endChar
        char[][] bounds = new char[ranges.length][2];
        int currentIndex = 0;

        for (int i = 0; i < ranges.length; i++) {
            int currentRangeSize = ranges[i];

            // bounds[x][] - the ranges; ranges[][0/1] - start/end char
            bounds[i][0] = charSet[currentIndex];
            bounds[i][1] = charSet[currentIndex + currentRangeSize - 1];

            // move to next range
            currentIndex += currentRangeSize;
        }
        return bounds;
    }

    ///
    public static void guiUpdate(AtomicInteger progress){

        // what the frick am I gonna do with this lol
//        SwingUtilities.invokeLater(() -> {
//            //progress.setValue(percent);
//        });
    }

    // Change this to inlcude startChar & endChar

    /// Thank you Sever for the help
    public static String parallelBruteForceGeneratorLegacy(int pwd_length, char[] char_set, char startChar, char endChar, String hash, String hash_type, AtomicLong attempts, int nThread, AtomicBoolean found) throws NoSuchAlgorithmException {
        int[] index = new int[pwd_length]; // initializing
        System.out.println("[" + nThread + "] start " + startChar + " end " + endChar);
        index[0] = new String(char_set).indexOf(startChar);
        char[] currentGuess = new char[pwd_length];
        //System.out.println(char_set);
        currentGuess[0] = startChar; // dummy

        //System.out.println("Bruh 1");
        for (int i = 1; i < pwd_length; i++) { //
            index[i] = 0;
            currentGuess[i] = char_set[0];
        }

        String password = "";
        //System.out.println("Bruh 2");
        while (true){ // compare the current guess
            if(found.get() || Thread.currentThread().isInterrupted()){
                return "";
            }
            String strGuess = new String(currentGuess);
//            System.out.println("["+attempts + "] guess: " + strGuess);
            if (hash_it(strGuess, hash_type).equalsIgnoreCase(hash)){
                System.out.println("[FOUND] " + strGuess);
                password = strGuess; /*stop every thread*/;
                found.set(true);
            return password;
            }
            attempts.getAndIncrement();

            // Iterate string
            int i = pwd_length - 1; // start at last char
            while (i >= 0){ // while password length is not 0
                index[i]++; // increment the last (from left to right) char in the indices
//                System.out.println("[" + attempts + "][" + nThread + "]: " + Arrays.toString(currentGuess));

                if(i==0){
                    System.out.println("indices " + char_set[index[0]] + ", end char " + new String(char_set).indexOf(endChar));

                    if (index[0] < new String(char_set).indexOf(endChar)){ // if last index didnt yet reach the end of the char_set
                        currentGuess[0] = char_set[index[0]]; // update current guess' char with the new char
                        i = pwd_length -1;
//                        break;
                    } else {
                        return password.isEmpty() ? "Password is empty." : (password + "\n" + attempts);
                    }
                } else{
                    if(index[i] < char_set.length){
                        currentGuess[i] = char_set[index[i]];

//                        System.out.println("2: " + Arrays.toString(currentGuess));
                        break;
                    } else {
                        index[i] = 0;
                        currentGuess[i] = char_set[0];
                        i--; // reset and move to the next digit
                    }
                }
            }
            attempts.incrementAndGet(); // update progress bar
            if (i < 0) {
                System.out.println("[Been through all characters]");
                break; // if this is reached we've been through all possible combinations7
            }
        }
        return password + "\n" + attempts;
    }

    public static String parallelBruteForceGenerator(int pwd_length, char[] char_set, char startChar, char endChar, String hash, String hash_type, AtomicLong attempts, int nThread, AtomicBoolean found, JProgressBar progress, long t, long t0, long attempts_dic, long par_time) throws NoSuchAlgorithmException {
//        System.out.println("hash: " + hash);
        // find indices for the range
        int startIndex = -1;
        int endIndex = -1;
        for (int i = 0; i < char_set.length; i++) {
            if (char_set[i] == startChar) startIndex = i;
            if (char_set[i] == endChar) endIndex = i;
        }

        // initialize index & current guess
        int[] index = new int[pwd_length];
        char[] currentGuess = new char[pwd_length];
        index[0] = startIndex;
        currentGuess[0] = startChar;
        for (int i = 1; i < pwd_length; i++) {
            index[i] = 0;
            currentGuess[i] = char_set[0];
        }

        System.out.println("[" + nThread + "] start " + startChar + " end " + endChar);

        while (index[0] <= endIndex && !found.get()) {

            String strGuess = new String(currentGuess);

            if (hash_it(strGuess, hash_type).equalsIgnoreCase(hash)) {
                found.set(true);
                System.out.println("[FOUND BY THREAD " + nThread + "][password]: " + strGuess);
                t = System.currentTimeMillis() - t0;
                long tt = System.currentTimeMillis() - par_time;
//                System.out.println("[Brute force attack] success.\n[pwd]: " + strGuess + " \n[time]: " + Functions.time(t) + " \n[attempts]: " + attempts.get());
                System.out.println("┌──────────────────────────────────────────────┐");
                System.out.println("│ BRUTE FORCE ATTEMPTS: " + attempts.get());
                System.out.println("│ TOTAL ATTEMPTS: " + ( attempts.get() + attempts_dic ));
                System.out.println("│ PARALLEL TIME: " + Functions.time(tt));
                System.out.println("│ TOTAL TIME: " + Functions.time(t));
                System.out.println("├──────────────────────────────────────────────┤");
                System.out.println("│ PASSWORD: " + strGuess);
                System.out.println("└──────────────────────────────────────────────┘");
                parGUI.enableButtons();
                SwingUtilities.invokeLater(() -> {//
                    progress.setValue(100);
                    progress.setString("Success");
                });
                return strGuess;
            }
            attempts.getAndIncrement();

            int i = pwd_length - 1;
            while (i >= 0) {
                index[i]++;

                if (index[i] < char_set.length) {
                    // if still same char, update and stop carrying
                    currentGuess[i] = char_set[index[i]];
                    break;
                } else {
                    // if end of char_set, reset to 0 & increment to next position
                    if (i == 0) {
                        // gone through all chars from set
                        break;
                    }
                    index[i] = 0;
                    currentGuess[i] = char_set[0];
                    i--;
                }
            }
            // finished range
            if (index[0] > endIndex) {
                long tt = System.currentTimeMillis() - par_time;
                t = System.currentTimeMillis() - t0;
                System.out.println("[" + nThread + "] Password: [NOT FOUND]\n[TOT_TIME]: " + Functions.time(t) + " [PAR_TIME]: " + Functions.time(tt) + " [TOT_ATMPTS]: " + (attempts.get()+attempts_dic) + " [BR_FRC_ATMPTS]: " + attempts.get());
                break;
            }
        }
        return null;
    }

    ///  DISTR

    /// Matrix flattener to convert the ranges 2d array into a normal array for Scatter
    public static char[] flattenMatrix(char[][] matrix){

        int length = 0;
        for (char[] n : matrix) {
                length += n.length;
        }
        char[] arr = new char[length];
        int i = 0;
        for (char[] n : matrix) {
            for (char element : n) {
                arr[i++] = element;
            }

        }

        return arr;
    }

    // pwd_length ✔, char_set ✔, range, hash ✔, hash_type ✔
    public static String[] distributedBruteForceGenerator(int pwd_length, char[] char_set, char startChar, char endChar, String hash, String hash_type, int me, int nodes/*, AtomicLong attempts, AtomicBoolean found, JProgressBar progress, long t, long t0 */) throws NoSuchAlgorithmException {

        long attempts = 0;
//        System.out.println("[" + me + "] hash: " + hash);
        // find indices for the range
        int startIndex = -1;
        int endIndex = -1;
        for (int i = 0; i < char_set.length; i++) {
            if (char_set[i] == startChar) startIndex = i;
            if (char_set[i] == endChar) endIndex = i;
        }

        // initialize index & current guess
        int[] index = new int[pwd_length];
        char[] currentGuess = new char[pwd_length];
        index[0] = startIndex;
        currentGuess[0] = startChar;
        for (int i = 1; i < pwd_length; i++) {
            index[i] = 0;
            currentGuess[i] = char_set[0];
        }

        System.out.println("[" + me + "] start " + startChar + " end " + endChar);

        while (index[0] <= endIndex /*&& !found.get()*/) {
            String strGuess = new String(currentGuess);

            if (hash_it(strGuess, hash_type).equalsIgnoreCase(hash)) {
                System.out.println("[FOUND][" + me + "][password]: " + strGuess);
//                t = System.currentTimeMillis() - t0;
                for (int i = 0; i < nodes; i++) {
                    if (i != me) {
                        MPI.COMM_WORLD.Isend(new int[0], 0, 0, MPI.INT, i, 67);
                    }
                }
                System.out.println("[Brute force attack] success.\n[pwd]: " + strGuess /*+ " \n[time]: " + Functions.time(t) + " \n[attempts]: " + attempts.get()*/);
//                Main.enableButtons();

                String strAttempts = String.valueOf(attempts);
                String[] result = new String[2]; result[0] = strGuess; result[1] = strAttempts;
                if (result[0].isEmpty()) result[0] = " ";
            return result;
            }
//            attempts.getAndIncrement();
            attempts++;
            if(attempts % 1000 == 0){
                Status status = MPI.COMM_WORLD.Iprobe(MPI.ANY_SOURCE, 67);
                if (status != null) {
                    System.out.println("[" + me + "] STOPPING");
                    break;
                }
            }

            int i = pwd_length - 1;
            while (i >= 0) {
                index[i]++;

                if (index[i] < char_set.length) {
                    // if still same char, update and stop carrying
                    currentGuess[i] = char_set[index[i]];
                    break;
                } else {
                    // if end of char_set, reset to 0 & increment to next position
                    if (i == 0) {
                        // gone through all chars from set
                        break;
                    }
                    index[i] = 0;
                    currentGuess[i] = char_set[0];
                    i--;
                }
            }
            // finished range
            if (index[0] > endIndex) {
                System.out.println("[" + me + "] Finished range: NOT FOUND");
//                MPI.Finalize();
                break;
            }
        }
        String strAttempts = String.valueOf(attempts);
        String[] result = new String[2]; result[0] = ""; result[1] = strAttempts;
        return result;
    }

    public static String[] distributedBruteForceGeneratorRoot(int pwd_length, char[] char_set, char startChar, char endChar, String hash, String hash_type, int me, JProgressBar progress, int nodes/*, AtomicLong attempts, AtomicBoolean found, JProgressBar progress, long t, long t0 */) throws NoSuchAlgorithmException {
        long possible_combs = (long) (Math.pow(char_set.length, (pwd_length-1)) * ((int) endChar - (int) startChar));
        System.out.println("[" + me + "] Possible combs root: " + possible_combs);

        long attempts = 0;
//        System.out.println("[" + me + "] hash: " + hash);
        // find indices for the range
        int startIndex = -1;
        int endIndex = -1;
        for (int i = 0; i < char_set.length; i++) {
            if (char_set[i] == startChar) startIndex = i;
            if (char_set[i] == endChar) endIndex = i;
        }

        // initialize index & current guess
        int[] index = new int[pwd_length];
        char[] currentGuess = new char[pwd_length];
        index[0] = startIndex;
        currentGuess[0] = startChar;
        for (int i = 1; i < pwd_length; i++) {
            index[i] = 0;
            currentGuess[i] = char_set[0];
        }

        System.out.println("[" + me + "] start " + startChar + " end " + endChar);

        while (index[0] <= endIndex /*&& !found.get()*/) {
            String strGuess = new String(currentGuess);

            if (hash_it(strGuess, hash_type).equalsIgnoreCase(hash)) {
//                found.set(true);
                System.out.println("[FOUND] [" + me + "][password]: " + strGuess);
                for (int i = 0; i < nodes; i++) {
                    if (i != me) {
                        MPI.COMM_WORLD.Isend(new int[0], 0, 0, MPI.INT, i, 67);
                    }
                }
//                t = System.currentTimeMillis() - t0;
                System.out.println("[Brute force attack] success.\n[pwd]: " + strGuess /*+ " \n[time]: " + Functions.time(t) + " \n[attempts]: " + attempts.get()*/);
//                Main.enableButtons();

                SwingUtilities.invokeLater(() -> {//
                    progress.setValue(100);
                    progress.setString("Success");
                });
                String strAttempts = String.valueOf(attempts);
                String[] result = new String[2]; result[0] = strGuess; result[1] = strAttempts;
                if (result[0].isEmpty()) result[0] = "";
                return result;
            }
//            attempts.getAndIncrement();
            attempts++;
            if(attempts % 1000 == 0){
                Status status = MPI.COMM_WORLD.Iprobe(MPI.ANY_SOURCE, 67);
                if (status != null) {
                    System.out.println("[" + me + "] STOPPING");
                    break;
                }
            }
            int percent = Functions.computeProgress(attempts, possible_combs);
            SwingUtilities.invokeLater(() -> {
                progress.setValue(percent);
            });

            int i = pwd_length - 1;
            while (i >= 0) {
                index[i]++;

                if (index[i] < char_set.length) {
                    // if still same char, update and stop carrying
                    currentGuess[i] = char_set[index[i]];
                    break;
                } else {
                    // if end of char_set, reset to 0 & increment to next position
                    if (i == 0) {
                        // gone through all chars from set
                        break;
                    }
                    index[i] = 0;
                    currentGuess[i] = char_set[0];
                    i--;
                }
            }
            // finished range
            if (index[0] > endIndex) {
                System.out.println("[" + me + "] Finished range: NOT FOUND");
                break;
            }
        }
        String strAttempts = String.valueOf(attempts);
        String[] result = new String[2]; result[0] = ""; result[1] = strAttempts;
        return result;
    }

}
    // MessageDigest         https://www.baeldung.com/java-md5

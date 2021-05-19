package externalsorting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class ExternalSorting {

    public static final int MAX_TEMP_FILES = 1024;
    public static final int MAX_OBJ_OVERHEAD = 60;

    private static long estimateBestSizeOfBlock(long sizeOfFile, long maxMemory){
        long blockSize = sizeOfFile / MAX_TEMP_FILES + (sizeOfFile % MAX_TEMP_FILES == 0 ? 0 : 1);
        if (blockSize < maxMemory / 2) {
            blockSize = maxMemory / 2;
        }
        return blockSize;
    }

    public static long estimateAvailableMemory(){
        System.gc();
        Runtime r = Runtime.getRuntime();
        long allocatedMemory = r.totalMemory() - r.freeMemory();
        long presFreeMemory = r.maxMemory() - allocatedMemory;
        return presFreeMemory;
    }

    public static long estimatedSizeOfRow(String s){
        return (s.length() * 2) + MAX_OBJ_OVERHEAD;
    }

    public static void main(String[] args) throws IOException {

//        String currentDirectory = System.getProperty("user.dir");
//        System.out.println(currentDirectory);

        String relativePath = "/resources/";
        String inputName = "large_file.txt";

        Path path = Paths.get(System.getProperty("user.dir"), relativePath);
        File inputFile = Paths.get(path.toString(), inputName).toFile();
        File outputFile = Paths.get(path.toString(), "sorted_"+inputName).toFile();
        File tempFileStore = Paths.get(path.toString(), "/tmp").toFile(); //null

        //Splitter & Sort - divide file into chunks and sort it
        List<File> files = sortInBatch(inputFile, tempFileStore);

        //Merge sort - sort chunks
        mergeSortedFiles(files, outputFile);
    }

    public static List<File> sortInBatch(File file, File tmpDirectory)
            throws IOException {

        BufferedReader fbr = new BufferedReader(new FileReader(file));

        List<File> files = new ArrayList<>();
        List<String> tmpList = new ArrayList<>();

        long dataLength = file.length();
        long maxMemory = estimateAvailableMemory();
        long blockSize = estimateBestSizeOfBlock(dataLength, maxMemory);

        try{
            while (true) {
                long currentBlockSize = 0;
                String row = fbr.readLine();

                while ((currentBlockSize<blockSize) && (row != null)){
                    tmpList.add(row);
                    currentBlockSize += estimatedSizeOfRow(row);
                    row = fbr.readLine();
                }
                files.add(sortBatch(tmpList, tmpDirectory));
                tmpList.clear();
                if(row == null){
                    break;
                }
            }
        } catch (EOFException e){
            if(tmpList.size()>0){
                files.add(sortBatch(tmpList, tmpDirectory));
                tmpList.clear();
            }
        }
        fbr.close();
        return files;
    }

    public static File sortBatch(List<String> tmpList, File tmpDirectory)
            throws IOException {

        Collections.sort(tmpList, (s1, s2) -> s1.compareTo(s2));
        File tmpFile = File.createTempFile("temp_file_", "_batch", tmpDirectory);

        BufferedWriter fbw = new BufferedWriter(new FileWriter(tmpFile));

        try{
            for (String r: tmpList){
                fbw.write(r);
                fbw.newLine();
            }
        } finally {
            fbw.close();
        };

        tmpFile.deleteOnExit();
        return tmpFile;
    }

    public static void mergeSortedFiles(List<File> files, File outputFile)
            throws IOException {

        ArrayList<FileBuffer> buffers = new ArrayList<>();

        PriorityQueue<FileBuffer> pq = new PriorityQueue<>(
                (fb1, fb2) -> fb1.peek().compareTo(fb2.peek())
        );

        try{
            for (File f: files)
                buffers.add(new FileBuffer(new BufferedReader(new FileReader(f))));

            BufferedWriter fbw = new BufferedWriter(new FileWriter(outputFile));

            for (FileBuffer fb : buffers) {
                if (!fb.empty()) {
                    pq.add(fb);
                }
            }

            try {
                while (pq.size() > 0) {
                    FileBuffer fb = pq.poll();
                    fbw.write(fb.pop());
                    fbw.newLine();
                    if (fb.empty()) {
                        fb.close();
                    } else {
                        pq.add(fb); // add it back
                    }
                }
            } finally {
                fbw.close();
                for(FileBuffer fb: pq){
                    fb.close();
                }
            }

        } finally {
            for (File f : files) {
                f.delete();
            }
        }
    }

}

package externalsorting;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class TestExternalSorting {
    private static final String TEST_INPUT_SMALL_1 = "small_file_1.txt";
    private static final String TEST_INPUT_SMALL_2 = "small_file_2.txt";
    private static final String[] INPUT_SMALL_RESULTS = {
            "a", "a", "a", "a", "b", "b", "c", "c", "e", "e", "f", "f", "g", "g",
            "h", "h",
    };

    private static final String[] SAMPLE = { "f", "m", "b", "e", "i", "o", "u",
            "x", "a", "y", "z", "b", "u"
    };
    private static final String[] SAMPLE_SORT_RESULTS = { "a", "b", "b", "e", "f",
            "i", "m", "o", "u", "u", "x", "y", "z"
    };

    private static final String TEST_INPUT_LARGE = "large_file.txt";

    private ArrayList<File> fileList;
    private File smallFile1;
    private File smallFile2;
    private File outputSmallFile;
    private File outputLargeFile;
    private File largeFile;
    private File tempFileStore;

    @Before
    public void load() throws Exception {
        System.out.println("Test!!!");

        Path path = Paths.get(System.getProperty("user.dir"), "/resources/");
        System.out.println(path);
        this.fileList = new ArrayList<File>(3);
        this.smallFile1 = Paths.get(path.toString(), TEST_INPUT_SMALL_1).toFile();
        this.smallFile2 = Paths.get(path.toString(), TEST_INPUT_SMALL_2).toFile();
        this.largeFile = Paths.get(path.toString(), TEST_INPUT_LARGE).toFile();
        this.tempFileStore = Paths.get(this.smallFile1.getParent(), "/tmp").toFile();
        this.outputSmallFile = Paths.get(path.toString(), "sorted_small_files.txt").toFile();
        this.outputLargeFile = Paths.get(path.toString(), "sorted_large_files.txt").toFile();

        File tmpFile1 = new File(this.smallFile1.getPath().toString()+".tmp");
        File tmpFile2 = new File(this.smallFile2.getPath().toString()+".tmp");

        copyFile(this.smallFile1, tmpFile1);
        copyFile(this.smallFile2, tmpFile2);

        this.fileList.add(tmpFile1);
        this.fileList.add(tmpFile2);
    }

    @After
    public void clear() throws Exception {
        this.smallFile1 = null;
        this.smallFile2 = null;
        this.largeFile = null;
        this.tempFileStore = null;
        this.outputSmallFile.delete();
        this.outputSmallFile = null;
        this.outputLargeFile.delete();
        this.outputLargeFile = null;

        for(File f:this.fileList) {
            f.delete();
        }
        this.fileList.clear();
        this.fileList = null;
    }

    private static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        try (FileInputStream fis = new FileInputStream(sourceFile);
             FileChannel source = fis.getChannel();
             FileOutputStream fos = new FileOutputStream(destFile);
             FileChannel destination = fos.getChannel()) {
             destination.transferFrom(source, 0, source.size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test(expected = Test.None.class)
    public void processLargeFile() throws IOException {

        List<File> files = ExternalSorting.sortInBatch(this.largeFile, tempFileStore);
        ExternalSorting.mergeSortedFiles(files, this.outputLargeFile);
    }

    @Test
    public void returnOfTmpFiles() throws IOException {

        File batch = ExternalSorting.sortBatch(Arrays.asList(SAMPLE), this.tempFileStore);

        assertNotNull(batch);
        assertTrue(batch.exists());
        assertTrue(batch.length() > 0);
        List<String> batchList = new ArrayList<>();
        try(BufferedReader bf = new BufferedReader(new FileReader(batch))){
            String line;
            while ((line = bf.readLine()) != null){
                batchList.add(line);
            }
        }
        assertArrayEquals(Arrays.toString(batchList.toArray()), SAMPLE_SORT_RESULTS,
                batchList.toArray());
    }

    @Test
    public void testSortingOfBatchSmallFile() throws IOException {
        List<File> files = ExternalSorting.sortInBatch(this.smallFile1, tempFileStore);
        assertTrue(files.size()==1);
    }

    @Test
    public void testSortingOfBatchLargeFile() throws IOException {
        List<File> files = ExternalSorting.sortInBatch(this.largeFile, tempFileStore);
        assertTrue(files.size() >= 1);
        assertTrue(files.size() <= ExternalSorting.MAX_TEMP_FILES);
    }

    @Test
    public void testMergeFilesResult() throws IOException {

        ExternalSorting.mergeSortedFiles(this.fileList, this.outputSmallFile);
        List<String> resultList = new ArrayList<>();
        try(BufferedReader bf = new BufferedReader(new FileReader(this.outputSmallFile))){
            String line;
            while ((line = bf.readLine()) != null){
                resultList.add(line);
            }
        }
        assertArrayEquals(Arrays.toString(resultList.toArray()), INPUT_SMALL_RESULTS,
                resultList.toArray());
    }
}

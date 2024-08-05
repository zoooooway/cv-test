package org.zoooooway;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Transcoding test via JavaCV
 */
public class App {
    public static void main(String[] args) throws IOException {
        String inputFile = "D:\\development\\temp\\video\\file_example_AVI_1920_2_3MG.avi";
        String outputFile = "D:\\development\\temp\\video\\result\\file_example_AVI_1920_2_3MG.mp4";
        VideoUtil.transcode(Files.newInputStream(Paths.get(inputFile)), Files.newOutputStream(Paths.get(outputFile)), "mp4", "mp4");
    }

}

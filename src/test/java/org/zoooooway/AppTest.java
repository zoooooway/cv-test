package org.zoooooway;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Unit test for simple App.
 */
public class AppTest
        extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() throws Exception {
//        String inputURL = "https://archive.org/download/BigBuckBunny_124/Content/big_buck_bunny_720p_surround.mp4";
//        String inputURL = "https://merak.alicdn.com/video/understand-aliyun/for-incalculable-value.mp4";
        String inputURL = "https://download.samplelib.com/mp4/sample-5s.mp4";
        String outputFile = "D:\\development\\temp\\video\\result\\transcode.webm";

        URL url = new URI(inputURL).toURL();

        try (OutputStream os = Files.newOutputStream(Paths.get(outputFile))) {
//            HttpClient httpClient = HttpClient.newBuilder().build();
//            HttpRequest request
//                    = HttpRequest.newBuilder()
//                    .GET()
//                    .uri(url.toURI())
//                    .build();
//            HttpResponse<InputStream> res = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
//            try (InputStream is = res.body()) {
            try (InputStream is = Files.newInputStream(Paths.get("D:\\development\\temp\\video\\transcode.mp4"))) {
                VideoUtil.realtimeTrans(is, os, "mp4", "webm");
                Thread.sleep(60000);
            }
        }
    }

    public void testToMP4() throws Exception {
        VideoUtil.transcode(new File("D:\\development\\temp\\video\\sample-5s.mp4"), new File("D:\\development\\temp\\video\\transcode.webm"), "mp4", "webm");
    }
}

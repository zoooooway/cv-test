package org.zoooooway;

import com.google.common.io.CountingInputStream;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerScope;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegLogCallback;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

import static org.bytedeco.ffmpeg.global.avutil.AV_LOG_INFO;

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
        String outputFile = "D:\\development\\temp\\video\\result\\transcode.mp4";

        VideoUtil.transcode(new File("D:\\development\\temp\\video\\test-upload-2024-07-30SampleVideo_1280x720_30mb.mp4"), new File(outputFile), "mp4", "mp4");
        Thread.sleep(60000);
    }

    public void testToMP4() throws Exception {
        VideoUtil.transcode(new File("D:\\development\\temp\\video\\sample-5s.mp4"), new File("D:\\development\\temp\\video\\transcode.webm"), "mp4", "webm");
    }

    public void testLimitStream() throws Exception {
        FFmpegLogCallback.setLevel(AV_LOG_INFO);
        FFmpegLogCallback.set();

//        HttpClient client = HttpClient.newBuilder()
//                .connectTimeout(Duration.ofSeconds(15))
//                .build();
//
//        HttpRequest req = HttpRequest.newBuilder(URI.create("https://merak.alicdn.com/video/understand-aliyun/for-incalculable-value.mp4"))
//                .GET()
//                .build();
//
//        InputStream is = client.send(req, HttpResponse.BodyHandlers.ofInputStream()).body();

        InputStream is = Files.newInputStream(Paths.get("D:\\development\\temp\\video\\20240226111150-BB9863522-1__request"));

        CountingInputStream cis = new CountingInputStream(is);
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(cis, 0)) {
            // default 5000000
            grabber.setOption("probesize", "1024");
            grabber.start();
            System.out.println("read count: " + cis.getCount());
            int i;
            byte[] buffer = new byte[4096];
            do {
                i = cis.read(buffer);
            } while (i > 0);
            System.out.println("total: " + cis.getCount());
        }


    }

    public void testMemory() throws Exception {
        printMem();

        AtomicInteger seq = new AtomicInteger(0);

        Thread t1 = new Thread(() -> {
            int i = 50;
            while (i > 0) {
                try (PointerScope ps = new PointerScope()) {
                    VideoUtil.grabCover(new File("D:\\development\\temp\\video\\sample-5s.mp4"), "jpg", new File("D:\\development\\temp\\video\\result\\cover.jpg"));
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                i--;
                seq.incrementAndGet();
            }
        });
        t1.start();

        Thread t2 = new Thread(() -> {
            for (; ; ) {
                System.out.printf("第 [%s] 执行\n", seq.get());

                try {
//                    System.gc();
                    Thread.sleep(1500);
                    printMem();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        });
        t2.start();

        t1.join();

        Thread.sleep(10000);
        System.out.println("after gc......");

//        try (PointerScope ps = new PointerScope()) {
            VideoUtil.grabCover(new File("D:\\development\\temp\\video\\sample-5s.mp4"), "jpg", new File("D:\\development\\temp\\video\\result\\cover.jpg"));
//        }

        System.gc();
        System.out.println("after gc......");
        Thread.sleep(10000);
        System.gc();
        System.out.println("after gc......");
        Thread.sleep(10000);
        printMem();

    }

    void printMem() {
        System.out.printf("CV memory usage: %s MB \n", Pointer.physicalBytes() / 1024 / 1024);
        System.out.printf("VM memory:  max -> %s, total -> %s, free -> %s\n", Runtime.getRuntime().maxMemory() / 1024 / 1024, Runtime.getRuntime().totalMemory() / 1024 / 1024, Runtime.getRuntime().freeMemory() / 1024 / 1024);

    }
}

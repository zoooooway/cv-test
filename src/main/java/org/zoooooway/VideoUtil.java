package org.zoooooway;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

/**
 * @author hezhongwei6
 * @create 2024/8/2
 */
public class VideoUtil {


    /**
     * 通过流构建graber和recorder, 借由两者进行转码
     *
     * @param inputStream
     * @param outputStream
     * @param inputFormat
     * @param outputFormat
     */
    public static void transcode(InputStream inputStream, OutputStream outputStream, String inputFormat, String outputFormat) {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputStream)) {
            grabber.setFormat(inputFormat);

            try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputStream, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels())) {
                recorder.setVideoOption("threads", "4");
                recorder.setFormat(outputFormat);
                // https://github.com/fluent-ffmpeg/node-fluent-ffmpeg/issues/346
                recorder.setOption("movflags", "frag_keyframe+empty_moov");
                // webm sample rate
//                recorder.setSampleRate(48000);

                recorder.start();
                transcode(grabber, recorder);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void transcode(File input, File output, String inputFormat, String outputFormat) {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(input)) {
            grabber.setFormat(inputFormat);

            try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(output, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels())) {
                recorder.setVideoOption("threads", "4");
                recorder.setFormat(outputFormat);
                transcode(grabber, recorder);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void transcode(URL input, File output, String inputFormat, String outputFormat) {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(input)) {
            grabber.setFormat(inputFormat);

            try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(output, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels())) {
                recorder.setVideoOption("threads", "4");
                recorder.setFormat(outputFormat);
                transcode(grabber, recorder);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void transcode(FFmpegFrameGrabber grabber, FFmpegFrameRecorder recorder) throws IOException {
        grabber.start();
        recorder.start();
        while (true) {
            try (Frame capturedFrame = grabber.grabFrame()) {
                if (capturedFrame == null) {
                    break;
                }
                recorder.record(capturedFrame);
            }
        }
    }


    public static void realtimeTrans(InputStream sourceIs, OutputStream os, String inputFormat, String outputFormat) throws Exception {
        PipedInputStream pis = new PipedInputStream();
        PipedOutputStream pos = new PipedOutputStream(pis);

        Thread t = new Thread(() -> {
            transcode(pis, os, inputFormat, outputFormat);
        });
        t.start();


        byte[] buffer = new byte[2048];
        int read;
        while ((read = sourceIs.read(buffer)) != -1) {
            pos.write(buffer, 0, read);
        }
        pos.close();
    }

    public static void grabCover(URL url, String outputFormat, File output) {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(url)) {
            grabber.start();

            try (Frame frame = grabber.grabImage()) {
                try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
                    BufferedImage bufferedImage = converter.convert(frame);
                    ImageIO.write(bufferedImage, outputFormat, output);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void grabCover(File input, String outputFormat, File output) {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(input)) {
            grabber.start();

            try (Frame frame = grabber.grabKeyFrame()) {
                try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
                    BufferedImage bufferedImage = converter.convert(frame);
                    ImageIO.write(bufferedImage, outputFormat, output);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

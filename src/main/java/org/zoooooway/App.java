package org.zoooooway;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FFmpegLogCallback;
import org.bytedeco.javacv.Frame;

import static org.bytedeco.ffmpeg.global.avutil.AV_LOG_INFO;

/**
 * Transcoding test via JavaCV
 */
public class App {
    public static void main(String[] args) {
        String inputFile = "D:\\development\\temp\\video\\file_example_AVI_1920_2_3MG.avi";
        String outputFile = "D:\\development\\temp\\video\\result\\file_example_AVI_1920_2_3MG.mp4";
        avi2mp4(inputFile, outputFile);
    }

    static void avi2mp4(String inputFile, String outputFile) {
        FFmpegLogCallback.setLevel(AV_LOG_INFO);
        FFmpegLogCallback.set();

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile)) {
            grabber.start();

            try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels())) {
                recorder.setVideoOption("threads", "4");
                recorder.setFormat("mp4");
                recorder.start();
                Frame capturedFrame;
                while (true) {
                    capturedFrame = grabber.grabFrame();
                    if (capturedFrame == null) {
                        System.out.println("finish!");
                        recorder.stop();
                        recorder.release();
                        grabber.stop();
                        grabber.release();
                        break;
                    }
                    recorder.record(capturedFrame);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

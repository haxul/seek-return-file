package com.starodubov.seek.file.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    public static final String FILE_PATH = "/home/haxul/Development/seek-return-file/files/test.mp3";

    @RestController
    public static class FileController {

        private final ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();

        @GetMapping("/file")
        public SseEmitter seek() {
            SseEmitter emitter = new SseEmitter();
            pool.execute(() -> {
                try (var file = new RandomAccessFile(FILE_PATH, "r")) {
                    long length = file.length();
                    int offset =(int) length / 2;
                    file.seek(0);
                    System.out.println(length);
                    int l = ((int) length) - offset;
                    var buff = new byte[500_000];
                    file.read(buff);
                    emitter.send(buff, MediaType.APPLICATION_OCTET_STREAM);
                    emitter.complete();

                } catch (Exception e) {
                    System.out.println(e);
                    emitter.completeWithError(e);
                }
            });
            return emitter;
        }
    }
}

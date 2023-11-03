package com.starodubov.seek.file.example;

import lombok.SneakyThrows;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    public static final String FILE_PATH = "C:\\Users\\sssta\\dev\\seek-return-file\\files\\test2.mp3";

    @RestController
    public static class FileController {

        private final ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();
        private static final int CHUNK_SIZE = 1024 * 1024;

        // Тут получаем из скольки частей состоит файл
        @GetMapping("/offsets")
        public long getOffsets() {
            try (var file = new RandomAccessFile(FILE_PATH, "r")) {
                long l = file.length(); // тут можно из бд брать, из таблицы с метаданными
                System.out.println("file len: " + l);
                long offsets = l / CHUNK_SIZE;
                System.out.println(offsets);
                return offsets;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // тут получаем непосредственно кусок файла
        @GetMapping("/v1/file")
        public SseEmitter seek(@RequestParam Long offset) {
            var emitter = new SseEmitter(100000000L);
            pool.execute(() -> {
                try (var file = new RandomAccessFile(FILE_PATH, "r")) {
                    long len = file.length();
                    var ptr = CHUNK_SIZE * offset;
                    file.seek(ptr);
                    long s = ptr + CHUNK_SIZE > len ? len - ptr : CHUNK_SIZE;
                    var buff = new byte[(int) s];
                    file.read(buff);
                    emitter.send(buff, MediaType.APPLICATION_OCTET_STREAM);
                    emitter.complete();
                    System.out.println("file is done");
                } catch (Exception e) {
                    System.err.println(e);
                    emitter.completeWithError(e);
                }
            });
            System.out.println("method is down");
            return emitter;
        }

        @GetMapping(value = "/v2/file")
        public ResponseEntity<Object> seek2(@RequestParam Long offset) {
            try (var file = new RandomAccessFile(FILE_PATH, "r")) {
                long len = file.length();
                var ptr = CHUNK_SIZE * offset;
                if (ptr > len) {
                    return ResponseEntity
                            .status(400)
                            .build();
                }
                file.seek(ptr);
                long s = ptr + CHUNK_SIZE > len ? len - ptr : CHUNK_SIZE;
                var buff = new byte[(int) s];
                file.read(buff);
                return ResponseEntity.status(200)
                        .contentType(new MediaType("audio", "mpeg"))
                        .body(buff);
            } catch (Exception e) {
                System.err.println(e);
                return null;
            }
        }

        @GetMapping(value = "/v3/file")
        public ResponseEntity<Object> seek3(@RequestParam Long offset) {
            try (var file = new RandomAccessFile(FILE_PATH, "r")) {
                long len = file.length();
                var ptr = CHUNK_SIZE * offset;
                if (ptr > len) {
                    return ResponseEntity
                            .status(400)
                            .build();
                }
                file.seek(ptr);
                final FileChannel channel = file.getChannel();
                long s = ptr + CHUNK_SIZE > len ? len - ptr : CHUNK_SIZE;
                var buff = ByteBuffer.allocate((int) s);
                channel.read(buff);
                return ResponseEntity.status(200)
                        .contentType(new MediaType("audio", "mpeg"))
                        .body(buff.array());
            } catch (Exception e) {
                System.err.println(e);
                return null;
            }
        }
    }
}

package com.starodubov.seek.file.example;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class Benchmarks {
    public static final String FILE_PATH = "C:\\Users\\sssta\\dev\\seek-return-file\\files\\test.mp3";

    @Benchmark
    public void randomAccessFile1Mb(Blackhole hole) {
        int CHUNK_SIZE = 1024 * 1024;
        try (var file = new RandomAccessFile(FILE_PATH, "r")) {
            long len = file.length();
            var ptr = CHUNK_SIZE * 1;
            file.seek(ptr);
            long s = ptr + CHUNK_SIZE > len ? len - ptr : CHUNK_SIZE;
            var buff = new byte[(int) s];
            file.read(buff);
            hole.consume(buff);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    @Benchmark
    public void randomAccessFile2Mb(Blackhole hole) {
        int CHUNK_SIZE = 2 * 1024 * 1024;
        try (var file = new RandomAccessFile(FILE_PATH, "r")) {
            long len = file.length();
            var ptr = CHUNK_SIZE * 1;
            file.seek(ptr);
            long s = ptr + CHUNK_SIZE > len ? len - ptr : CHUNK_SIZE;
            var buff = new byte[(int) s];
            file.read(buff);
            hole.consume(buff);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    @Benchmark
    public void randomAccessFile4Mb(Blackhole hole) {
        int CHUNK_SIZE = 4 * 1024 * 1024;
        try (var file = new RandomAccessFile(FILE_PATH, "r")) {
            long len = file.length();
            var ptr = CHUNK_SIZE * 1;
            file.seek(ptr);
            long s = ptr + CHUNK_SIZE > len ? len - ptr : CHUNK_SIZE;
            var buff = new byte[(int) s];
            file.read(buff);
            hole.consume(buff);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    @Benchmark
    public void randomAccessFileWitchChannel(Blackhole hole) {
        int CHUNK_SIZE = 4 * 1024 * 1024;
        try (var file = new RandomAccessFile(FILE_PATH, "r")) {
            long len = file.length();
            var ptr = CHUNK_SIZE * 1;
            file.seek(ptr);
            final FileChannel channel = file.getChannel();
            long s = ptr + CHUNK_SIZE > len ? len - ptr : CHUNK_SIZE;
            var buff = ByteBuffer.allocate((int) s);
            channel.read(buff);
            hole.consume(buff);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    @Benchmark
    public void randomAccessFileWitchDirectChannel(Blackhole hole) {
        int CHUNK_SIZE = 4 * 1024 * 1024;
        try (var file = new RandomAccessFile(FILE_PATH, "r")) {
            long len = file.length();
            var ptr = CHUNK_SIZE * 1;
            file.seek(ptr);
            final FileChannel channel = file.getChannel();
            long s = ptr + CHUNK_SIZE > len ? len - ptr : CHUNK_SIZE;
            var buff = ByteBuffer.allocateDirect((int) s);
            channel.read(buff);
            hole.consume(buff);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(Benchmarks.class.getSimpleName())
                .warmupIterations(3)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}

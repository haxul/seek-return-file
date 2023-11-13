package com.starodubov.seek.file.example;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class Benchmarks {

    //ПЕРЕОПРЕДЕЛИТЬ
    public static final String FILE_PATH = "C:\\Users\\sssta\\dev\\seek-return-file\\files\\test";

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
    public void randomAccessFileWitchChannel4MB(Blackhole hole) {
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
    public void randomAccessFileWitchDirectChannel4Mb(Blackhole hole) {
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

    @Benchmark
    public void inputStreamWith1Mb(Blackhole hole) {
        int CHUNK_SIZE = 1 * 1024 * 1024;
        try (final InputStream inStream = new BufferedInputStream(new FileInputStream(FILE_PATH))) {
            var ptr = CHUNK_SIZE * 1;
            inStream.skipNBytes(ptr);
            var buff = new byte[CHUNK_SIZE];
            IOUtils.read(inStream, buff, 0, buff.length);
            hole.consume(buff);
        } catch (Exception e) {

        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(Benchmarks.class.getSimpleName())
                .warmupIterations(1)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}

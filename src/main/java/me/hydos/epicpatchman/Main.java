package me.hydos.epicpatchman;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws IOException {
        Path patchedSrc = Paths.get(args[0]);
        Path unpatchedSrc = Paths.get(args[1]);

        Set<Path> classpath = Arrays.stream(args).skip(2).map(Paths::get).collect(Collectors.toSet());

        System.out.println("Parsing...");
        Stream<Pair<CompilationUnit, CompilationUnit>> blessed = Files.walk(unpatchedSrc)
                .filter(Files::isRegularFile)
                .map(unpatchedSrc::relativize)
                .map(src -> {
                    Path a = unpatchedSrc.resolve(src);
                    Path b = patchedSrc.resolve(src);
                    if(Files.isRegularFile(a) && Files.isRegularFile(b)){
                        try {
                            return new Pair<>(StaticJavaParser.parse(a), StaticJavaParser.parse(b));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull);

    }

}

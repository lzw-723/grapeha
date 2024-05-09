package fun.lzwi.grapeha.library;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import fun.lzwi.grapeha.library.parser.EpubParser;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class Library {

  private static Logger logger = LoggerFactory.getLogger(Library.class);



  public static List<Book> scan(String path) {
    File libDir = new File(path);
    List<File> bookFiles = new ArrayList<>();
    List<Book> books = new ArrayList<>();
    Path startPath = libDir.toPath(); // 替换为你要搜索的起始路径
    findEpubFiles(startPath, bookFiles);
    fetchInfo(bookFiles, books);
    return books;
  }


  private static void fetchInfo(List<File> bookFiles, List<Book> books) {
    for (File file : bookFiles) {
      books.add(EpubParser.parser(file));
    }
  }

  private static void findEpubFiles(Path path, List<File> files) {
    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
      for (Path entry : directoryStream) {
        if (Files.isRegularFile(entry) && entry.toString().endsWith(".epub")) {
          System.out.println("Found epub file: " + entry);
          files.add(entry.toFile());
        } else if (Files.isDirectory(entry)) {
          // 递归搜索子目录
          findEpubFiles(entry, files);
        }
      }
    } catch (IOException e) {
      logger.error(String.format("扫描目录%s过程中出现错误！", path), e);
    }
  }
}

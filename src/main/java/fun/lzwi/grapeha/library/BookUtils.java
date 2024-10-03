package fun.lzwi.grapeha.library;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

import fun.lzwi.grapeha.config.ConfigUtils;
import fun.lzwi.grapeha.library.bean.Book;
import fun.lzwi.grapeha.library.parser.EpubParser;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

public class BookUtils {

  private static final Logger logger = LoggerFactory.getLogger(BookUtils.class);

  static boolean checkBook(String path) {
    return path.endsWith(".epub");
  }

  public static Book scanBook(String bookPath) {
    if (!checkBook(bookPath)) {
      return null;
    }
    logger.info("扫描图书文件：" + bookPath);
    try {
      return EpubParser.parser(new File(bookPath));
    } catch (ParserConfigurationException | IOException | SAXException e) {
      logger.warn("扫描图书文件：%s失败！".formatted(bookPath), e);
    }
    return null;
  }

  //  public static List<Book> scan(String path) {
  //    File libDir = new File(path);
  //    List<File> bookFiles = new ArrayList<>();
  //    List<Book> books = new ArrayList<>();
  //    Path startPath = libDir.toPath(); // 替换为你要搜索的起始路径
  //    findEpubFiles(startPath, bookFiles);
  //    fetchInfo(bookFiles, books);
  //    return books;
  //  }
  //
  //
  //  private static void fetchInfo(List<File> bookFiles, List<Book> books) {
  //    for (File file : bookFiles) {
  //      try {
  //        books.add(EpubParser.parser(file));
  //      } catch (ParserConfigurationException | IOException | SAXException e) {
  //        throw new RuntimeException(e);
  //      }
  //    }
  //  }
  //
  //  private static void findEpubFiles(Path path, List<File> files) {
  //    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
  //      for (Path entry : directoryStream) {
  //        if (Files.isRegularFile(entry) && entry.toString().endsWith(".epub")) {
  //          logger.info("Found epub file: " + entry);
  //          files.add(entry.toFile());
  //        } else if (Files.isDirectory(entry)) {
  //          // 递归搜索子目录
  //          findEpubFiles(entry, files);
  //        }
  //      }
  //    } catch (IOException e) {
  //      logger.error(String.format("扫描目录%s过程中出现错误！", path), e);
  //    }
  //  }

  public static boolean generateCover(String bookPath) {
    try {
      InputStream in = EpubParser.getCoverInputStream(bookPath);
      Path path =
          Paths.get(
              String.format(ConfigUtils.getCachePath() + "/cover/%s.jpg", bookPath.hashCode()));
      // 检查目录是否存在
      if (!Files.exists(path.getParent())) {
        Files.createDirectories(path.getParent());
      }
      Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
      logger.info("为%s生成封面。".formatted(bookPath));
    } catch (IOException | ParserConfigurationException | SAXException e) {
      logger.warn("为%s生成封面失败！".formatted(bookPath), e);
    }
    return true;
  }


}

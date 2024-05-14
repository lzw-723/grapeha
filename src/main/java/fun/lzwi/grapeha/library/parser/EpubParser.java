package fun.lzwi.grapeha.library.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import fun.lzwi.epubime.easy.EasyEpub;
import fun.lzwi.grapeha.library.Book;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class EpubParser {
  private static Logger logger = LoggerFactory.getLogger(EpubParser.class);

  public static Book parser(File file) {
    Book book = new Book();
    book.setPath(file.getPath());
    try {
      EasyEpub easyEpub = new EasyEpub(file);
      if (easyEpub.getTitle() != null) {
        book.setName(easyEpub.getTitle());
      } else {
        book.setName(file.getName());
      }

      book.setAuthor(easyEpub.getAuthor());
      book.setDate(easyEpub.getDate());
      book.setDescription(easyEpub.getDescription());
      String cover = easyEpub.getCover();
      book.setCover(cover);


      process(easyEpub, book);
    } catch (Exception e) {
      logger.warn(String.format("解析文件%s失败！", file.getName()), e);
    }
    return book;
  }

  private static void process(EasyEpub easyEpub, Book book) {
    try {
      if (book.getCover() != null) {
        InputStream in = easyEpub.getResource(book.getCover()).getInputStream();
        // 定义目标文件的路径
        Path destinationPath = Paths.get(String.format("./config/cache/%s.jpg", book.getPath().hashCode()));
//        Files.createDirectories(destinationPath);
//        if (Files.exists(destinationPath)) Files.delete(destinationPath);
        Files.copy(in, destinationPath, StandardCopyOption.REPLACE_EXISTING);
      }
    } catch (IOException e) {
      logger.warn(String.format("处理图书%s失败！", book.getName()), e);
    }
  }
}

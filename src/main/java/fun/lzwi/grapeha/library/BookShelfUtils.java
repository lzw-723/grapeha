package fun.lzwi.grapeha.library;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BookShelfUtils {
  public static void scanBookshelf(String dir) {

  }

  public static List<File> listBookFiles(String dir) {
    List<File> files = new ArrayList<>();
    // 列出目录下所有文件
    // 遍历文件列表，判断每个文件是否为图书文件
    for (File file : Objects.requireNonNull(new File(dir).listFiles())) {
      if (file.isFile() && BookUtils.checkBook(file.getName())) {
        // 如果是图书文件，则将其添加到图书列表中
        files.add(file);
      } else if (file.isDirectory()) {
        // 如果是目录，则递归调用本方法，将目录下的图书文件添加到图书列表中
        files.addAll(listBookFiles(file.getPath()));
      }
    }
    return files;
  }

  public static void generateCover(String path) {
  }
}

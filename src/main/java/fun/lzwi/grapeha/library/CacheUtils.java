package fun.lzwi.grapeha.library;

import fun.lzwi.grapeha.config.ConfigUtils;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.CRC32;

public class CacheUtils {
  private static Logger logger = LoggerFactory.getLogger(CacheUtils.class);

  public static String getBookCoverPath(String bookPath) {
    return String.format(ConfigUtils.getCachePath() + "/cover/%s.jpg", bookPath.hashCode());
  }

  public static boolean isExist(String filePath) {
    File file = new File(filePath);
    return file.exists();
  }

  private static void deleteAll(File file) {
    File[] files = file.listFiles();
    if (files != null) {
      for (File f : files) {
        if (f.isDirectory()) {
          deleteAll(f);
        } else {
          f.delete();
        }
      }
    }
    file.delete();
  }

  // 清除所有缓存
  public static void clean() {
    File cacheDir = new File(ConfigUtils.getCachePath());
    deleteAll(cacheDir);
  }

  // 校验crc32
  public static long getCrc32(String filePath) {
    try {
      File file = new File(filePath);
      FileInputStream fileInputStream = new FileInputStream(file);
      CRC32 crc32 = new CRC32();
      byte[] buffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = fileInputStream.read(buffer)) != -1) {
        crc32.update(buffer, 0, bytesRead);
      }
      fileInputStream.close();

      // CRC32校验码
      long checksum = crc32.getValue();
      return checksum;
    } catch (IOException e) {
      logger.warn("计算文件%s的crc32失败！".formatted(filePath), e);
    }
    return 0;
  }
}

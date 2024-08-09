package fun.lzwi.grapeha.config;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ConfigUtils {

  private static final Logger logger = LoggerFactory.getLogger(ConfigUtils.class);

  public static boolean isDev() {
    return "dev".equals(System.getenv("GRAPEHA_ENV".toLowerCase()));
  }

  /**
   * 获取配置文件的路径。 这个方法首先检查名为"GRAPEHA_CONFIG_PATH"的环境变量是否存在。 如果环境变量存在，它的值将被用作配置文件的路径。
   * 如果环境变量不存在，那么将使用默认路径"./config"。
   *
   * @return String, 配置文件的路径
   */
  public static String getConfigPath() {
    // 读取环境变量
    String path = System.getenv("GRAPEHA_CONFIG_PATH");
    if (path == null) {
      path = "./config";
    }
    return path;
  }

  /**
   * 初始化配置目录。 这个方法首先使用getConfigPath()方法获取配置文件的路径，如果不存在会创建这个目录。
   * 接着，它会检查在这个目录下是否存在"data"和"cache"子目录。如果不存在会创建这些子目录。
   */
  public static void init() throws URISyntaxException {
    Path configPath = Paths.get(getConfigPath());
    // 不存在就新建目录
    if (!configPath.toFile().exists()) {
      configPath.toFile().mkdirs();
    }
    // 如果不存在就新建子目录data,cache,webui
    if (!configPath.resolve("data").toFile().exists()) {
      configPath.resolve("data").toFile().mkdirs();
    }
    if (!configPath.resolve("cache").toFile().exists()) {
      configPath.resolve("cache").toFile().mkdirs();
    }
    if (!configPath.resolve("data").resolve("webui").toFile().exists()) {
      configPath.resolve("data").resolve("webui").toFile().mkdirs();
    }
    File target = new File(ConfigUtils.getWebuiPath());
    try {
      if (!target.exists() || Files.list(target.toPath()).findAny().isEmpty()) {
        // 解压webui文件到data/webui
        logger.warn("复制webui文件到data/webui");
        URL webui = ConfigUtils.class.getClassLoader().getResource("webui.zip");

        Files.deleteIfExists(target.toPath());
        if (webui != null) {
          unzipFile(webui.openStream(), target);
        }
      }
    } catch (IOException e) {
      logger.warn("复制webui文件到data/webui失败", e);
    }
  }

  private static void unzipFile(InputStream inputStream, File directory) {
    try {
      if (!directory.exists()) {
        directory.mkdirs();
      }

      ZipInputStream zipIn = new ZipInputStream(inputStream);
      ZipEntry entry;

      // 循环读取ZIP文件中的每个条目
      while ((entry = zipIn.getNextEntry()) != null) {
        String entryName = entry.getName();
        // 忽略目录条目，只处理文件条目
        if (!entry.isDirectory()) {
          File targetFile = new File(directory, entryName);
          // 创建父目录（如果需要）
          targetFile.getParentFile().mkdirs();

          try {
            FileOutputStream fos = new FileOutputStream(targetFile);
            int length;
            byte[] buffer = new byte[4096];
            while ((length = zipIn.read(buffer)) > 0) {
              fos.write(buffer, 0, length);
            }
            fos.close();
            logger.info("成功解压文件: " + entryName);
          } catch (Exception e) {
            logger.warn("解压文件中%s时出错".formatted(targetFile.getPath()), e);
          }
        }
        zipIn.closeEntry();
      }

      zipIn.close();
      logger.info("解压文件完成");
    } catch (Exception e) {
      logger.warn("解压文件到%s出错".formatted(directory.getPath()), e);
    }
  }

  public static String getCachePath() {
    Path configPath = Paths.get(getConfigPath());
    return configPath.resolve("cache").toString();
  }

  public static String getDataPath() {
    Path configPath = Paths.get(getConfigPath());
    return configPath.resolve("data").toString();
  }

  public static String getWebuiPath() {
    Path dataConfig = Paths.get(getDataPath());
    return dataConfig.resolve("webui").toString();
  }
}

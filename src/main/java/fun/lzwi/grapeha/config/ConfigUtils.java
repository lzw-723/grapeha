package fun.lzwi.grapeha.config;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigUtils {
  public static boolean isDev() {
    return "dev".equals(System.getenv("GRAPEHA_ENV".toLowerCase()));
  }

  /**
   * 获取配置文件的路径。
   * 这个方法首先检查名为"GRAPEHA_CONFIG_PATH"的环境变量是否存在。
   * 如果环境变量存在，它的值将被用作配置文件的路径。
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
   * 初始化配置目录。
   * 这个方法首先使用getConfigPath()方法获取配置文件的路径，如果不存在会创建这个目录。
   * 接着，它会检查在这个目录下是否存在"data"和"cache"子目录。如果不存在会创建这些子目录。
   */
  public static void init() {
    Path configPath = Paths.get(getConfigPath());
    // 不存在就新建目录
    if (!configPath.toFile().exists()) {
      configPath.toFile().mkdirs();
    }
    // 如果不存在就新建子目录data,cache
    if (!configPath.resolve("data").toFile().exists()) {
      configPath.resolve("data").toFile().mkdirs();
    }
    if (!configPath.resolve("cache").toFile().exists()) {
      configPath.resolve("cache").toFile().mkdirs();
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
}

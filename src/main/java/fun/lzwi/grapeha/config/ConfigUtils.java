package fun.lzwi.grapeha.config;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigUtils {
  public static String getConfigPath() {
    // 读取环境变量
    String path = System.getenv("GRAPEHA_CONFIG_PATH");
    if (path == null) {
      path = "./config";
    }
    return path;
  }

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
}

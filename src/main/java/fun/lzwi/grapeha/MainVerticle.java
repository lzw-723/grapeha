package fun.lzwi.grapeha;

import fun.lzwi.grapeha.config.ConfigUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.ThreadingModel;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Logger logger = LoggerFactory.getLogger(MainVerticle.class);
    ConfigUtils.init();

    Future<String> lib = vertx.deployVerticle(new LibraryVerticle(), new DeploymentOptions().setThreadingModel(ThreadingModel.WORKER)).onFailure(e -> {
      logger.error("LibraryVerticle启动失败！", e);
    }).onSuccess(s -> {
      logger.info("LibraryVerticle启动成功。");
    });
    Future<String> web = vertx.deployVerticle(new WebVerticle()).onFailure(e -> {
      logger.error("WebVerticle启动失败！", e);
    }).onSuccess(s -> {
      logger.info("WebVerticle启动成功。");
    });
    // Future.all(lib, web).

  }
}

package fun.lzwi.grapeha;

import fun.lzwi.grapeha.config.ConfigUtils;
import fun.lzwi.grapeha.verticle.DatabaseVerticle;
import fun.lzwi.grapeha.verticle.LibraryVerticle;
import fun.lzwi.grapeha.verticle.TaskerVerticle;
import fun.lzwi.grapeha.verticle.WebVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Logger logger = LoggerFactory.getLogger(MainVerticle.class);
    ConfigUtils.init();

    Future<String> database = vertx.deployVerticle(new DatabaseVerticle()).onFailure(Throwable::printStackTrace);
    Future<String> library = vertx.deployVerticle(new LibraryVerticle()).onFailure(Throwable::printStackTrace);
    Future<String> tasker = vertx.deployVerticle(new TaskerVerticle()).onFailure(Throwable::printStackTrace);
    Future<String> web = vertx.deployVerticle(new WebVerticle()).onFailure(Throwable::printStackTrace);

  }
}

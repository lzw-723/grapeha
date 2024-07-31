package fun.lzwi.grapeha.verticle;

import fun.lzwi.grapeha.db.HSQLDB;
import fun.lzwi.grapeha.db.repository.BookRepository;
import fun.lzwi.grapeha.db.repository.BookshelfRepository;
import fun.lzwi.grapeha.db.repository.UserRepository;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class DatabaseVerticle extends AbstractVerticle {
  static final String EVENT_STARTED = "verticle.database.started";
  Logger logger = LoggerFactory.getLogger(DatabaseVerticle.class);

  @Override
  public void start() throws Exception {
    super.start();
    HSQLDB.init(vertx).onSuccess(pool -> {
      BookRepository.getInstance().init(pool);
      UserRepository.getInstance().init(pool);
      BookshelfRepository.getInstance().init(pool);
      vertx.eventBus().publish(EVENT_STARTED, "");
    });
  }
}

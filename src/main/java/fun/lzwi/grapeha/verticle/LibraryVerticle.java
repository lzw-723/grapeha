package fun.lzwi.grapeha.verticle;

import fun.lzwi.grapeha.config.ConfigUtils;
import fun.lzwi.grapeha.db.repository.BookRepository;
import fun.lzwi.grapeha.db.repository.BookshelfRepository;
import fun.lzwi.grapeha.db.repository.UserRepository;
import fun.lzwi.grapeha.library.Bookshelf;
import fun.lzwi.grapeha.library.User;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LibraryVerticle extends AbstractVerticle {
  static final String EVENT_STARTED = "verticle.library.started";

  Logger logger = LoggerFactory.getLogger(LibraryVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    super.start(startPromise);

    EventBus eventBus = vertx.eventBus();
    MessageConsumer<String> consumer = eventBus.consumer("verticle.database.started");
    consumer.handler(path -> devSetup().compose(r -> UserRepository.getInstance().count().compose(count -> {
      logger.info("检查用户数量");
      if (count == 0) {
        logger.warn("当前没有任何用户，添加默认用户");
        User user = new User();
        user.setUsername("admin");
        user.setPassword("adminadmin");
        logger.warn("添加默认用户：\n\t用户名：" + user.getUsername() + "\n\t密码：" + user.getPassword());
        return UserRepository.getInstance().save(user);
      }

      return Future.succeededFuture();
    })).compose(r -> {
      // 扫描书架
      logger.info("扫描书架");
      BookshelfRepository.getInstance().findAll().onSuccess(h -> h.forEach(bookshelf -> eventBus.publish("scan.book", bookshelf.getPath())));
      return Future.succeededFuture();
    }));

    vertx.eventBus().publish(EVENT_STARTED, "");
  }

  private Future<Void> devSetup() {
    Future<Void> future = Future.succeededFuture();
    if (ConfigUtils.isDev()) {
      logger.warn("开发环境，清除所有数据");
      future = BookRepository.getInstance().deleteAll().compose(r -> BookshelfRepository.getInstance().deleteAll()).compose(r -> UserRepository.getInstance().deleteAll());

//      UserRepository.getInstance().deleteAll().onFailure(event -> {
//        logger.error("清除所有数据失败：" + event.getMessage());
//      }).onSuccess(h -> {
//        logger.warn("清除所有数据成功");
//      });
      // 读取./data目录下的文件夹，并将文件夹名称作为书架名称，并创建书架
      future.map(v -> {
        try {
          Files.list(Path.of("./data")).forEach(dir -> {
            if (!dir.toFile().isDirectory()) return;
            Bookshelf bookshelf = new Bookshelf();
            bookshelf.setTitle(dir.getFileName().toString());
            bookshelf.setPath(dir.toString());
            BookshelfRepository.getInstance().save(bookshelf);
            logger.info("添加书架：" + bookshelf.getTitle());
          });
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
        return v;
      });
    }
    return future;
  }


}

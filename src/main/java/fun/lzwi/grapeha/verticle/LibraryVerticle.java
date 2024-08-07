package fun.lzwi.grapeha.verticle;

import fun.lzwi.grapeha.config.ConfigUtils;
import fun.lzwi.grapeha.db.repository.BookRepository;
import fun.lzwi.grapeha.db.repository.BookshelfRepository;
import fun.lzwi.grapeha.db.repository.UserRepository;
import fun.lzwi.grapeha.library.CacheUtils;
import fun.lzwi.grapeha.library.bean.Bookshelf;
import fun.lzwi.grapeha.library.bean.User;
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

// 设置、操作数据库，发布任务
public class LibraryVerticle extends AbstractVerticle {

  Logger logger = LoggerFactory.getLogger(LibraryVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    super.start(startPromise);

    EventBus eventBus = vertx.eventBus();
    MessageConsumer<String> consumer = eventBus.consumer("verticle.database.started");
    consumer.handler(
        path ->
            devSetup()
                .compose(
                    r ->
                        UserRepository.getInstance()
                            .count()
                            .compose(
                                count -> {
                                  logger.info("检查用户数量");
                                  if (count == 0) {
                                    logger.warn("当前没有任何用户，添加默认用户");
                                    User user = new User();
                                    user.setUsername("admin");
                                    user.setPassword("adminadmin");
                                    logger.warn(
                                        "添加默认用户：\n\t用户名："
                                            + user.getUsername()
                                            + "\n\t密码："
                                            + user.getPassword());
                                    return UserRepository.getInstance().save(user);
                                  }

                                  return Future.succeededFuture();
                                }))
                .compose(
                    r -> {
                      // 扫描书架
                      logger.info("扫描书架");
                      return freshBookshelfBooks(eventBus)
                          .onComplete(h -> freshBookshelfBookCovers(eventBus));
                    }));

    vertx.setPeriodic(
        1000L * 60 * 60 * 6,
        h -> {
          logger.info("计划任务：扫描书架");
          freshBookshelfBooks(eventBus).onComplete(a -> freshBookshelfBookCovers(eventBus));
        });
  }

  /**
   * 这个方法是用来在开发环境中设置应用。它会在开发环境下进行一系列初始化操作，包括清除数据库中所有数据，然后读取./data目录下的文件夹，将每个文件夹名称作为书架名称创建新的书架，最后保存到数据库中。
   * 如果在任何步骤中发生错误，将会抛出一个RuntimeException。
   *
   * @return Future对象，表示异步操作的结果。如果操作成功，Future将成功结束；如果操作失败，Future将失败并包含异常信息。
   */
  private Future<Void> devSetup() {
    Future<Void> future = Future.succeededFuture();
    if (ConfigUtils.isDev()) {
      logger.warn("开发环境，清除所有数据");
      future =
          BookRepository.getInstance()
              .deleteAll()
              .compose(r -> BookshelfRepository.getInstance().deleteAll())
              .compose(r -> UserRepository.getInstance().deleteAll())
              .map(
                  a -> {
                    CacheUtils.clean();
                    return null;
                  });
      // 读取./data目录下的文件夹，并将文件夹名称作为书架名称，并创建书架
      future.map(
          v -> {
            try {
              Files.list(Path.of("./data"))
                  .forEach(
                      dir -> {
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

  private Future<Void> freshBookshelfBooks(EventBus eventBus) {
    return BookshelfRepository.getInstance()
        .findAll()
        .map(
            bookshelves -> {
              bookshelves.forEach(
                  bookshelf ->
                      eventBus.publish(
                          TaskerVerticle.ACTION_SCAN_BOOKS_IN_BOOKSHELF, bookshelf.getPath()));
              return null;
            });
  }

  private Future<Void> freshBookshelfBookCovers(EventBus eventBus) {
    return BookshelfRepository.getInstance()
        .findAll()
        .map(
            bookshelves -> {
              bookshelves.forEach(
                  bookshelf ->
                      eventBus.publish(
                          TaskerVerticle.ACTION_GENERATE_BOOK_COVER_IN_BOOKSHELF,
                          bookshelf.getPath()));
              return null;
            });
  }
}

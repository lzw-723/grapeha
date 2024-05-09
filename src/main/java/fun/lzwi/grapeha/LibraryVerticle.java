package fun.lzwi.grapeha;

import fun.lzwi.grapeha.db.HSQLDB;
import fun.lzwi.grapeha.db.repository.BookRepository;
import fun.lzwi.grapeha.db.repository.UserRepository;
import fun.lzwi.grapeha.library.Book;
import fun.lzwi.grapeha.library.Library;
import fun.lzwi.grapeha.library.User;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.util.List;

public class LibraryVerticle extends AbstractVerticle {
  Logger logger = LoggerFactory.getLogger(LibraryVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    super.start(startPromise);

    HSQLDB.init(vertx).onSuccess(pool -> {
      BookRepository.getInstance().init(pool);
      UserRepository.getInstance().init(pool);
      UserRepository.getInstance().count().compose(count -> {
        if (count == 0) {
          User user = new User();
          user.setUsername("admin");
          user.setPassword("adminadmin");
          logger.warn("添加默认用户：\n\t用户名：" + user.getUsername() + "\n\t密码：" + user.getPassword());
          return UserRepository.getInstance().save(user);
        }
        return Future.succeededFuture();
      });
    });

    EventBus eventBus = vertx.eventBus();
    MessageConsumer<String> consumer = eventBus.consumer("scan.book");
    consumer.handler(path -> {
      List<Book> books = Library.scan(path.body());
      BookRepository.getInstance().deleteAll().compose(v -> BookRepository.getInstance().saveAll(books));
    });

    vertx.setTimer(1000 * 10, h -> eventBus.publish("scan.book", "D:/Downloads"));
  }
}

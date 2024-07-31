package fun.lzwi.grapeha;

import fun.lzwi.grapeha.config.ConfigUtils;
import fun.lzwi.grapeha.db.HSQLDB;
import fun.lzwi.grapeha.db.repository.BookRepository;
import fun.lzwi.grapeha.db.repository.BookshelfRepository;
import fun.lzwi.grapeha.db.repository.UserRepository;
import fun.lzwi.grapeha.library.Book;
import fun.lzwi.grapeha.library.Bookshelf;
import fun.lzwi.grapeha.library.LibraryUtils;
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
import java.util.List;

public class LibraryVerticle extends AbstractVerticle {
  Logger logger = LoggerFactory.getLogger(LibraryVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    super.start(startPromise);

    HSQLDB.init(vertx).onSuccess(pool -> {
      BookRepository.getInstance().init(pool);
      UserRepository.getInstance().init(pool);
      BookshelfRepository.getInstance().init(pool);
      if (ConfigUtils.isDev()) {
        // 开发环境，清除所有数据
        BookRepository.getInstance().deleteAll();
        BookshelfRepository.getInstance().deleteAll();
//        UserRepository.getInstance().deleteAll();
        // 读取./data目录下的文件夹，并将文件夹名称作为书架名称，并创建书架
        try {
          Files.list(Path.of("./data")).forEach(dir -> {
            if (!dir.toFile().isDirectory()) return;
            Bookshelf bookshelf = new Bookshelf();
            bookshelf.setTitle(dir.getFileName().toString());
            bookshelf.setPath(dir.toString());
            BookshelfRepository.getInstance().save(bookshelf);
          });
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
      EventBus eventBus = vertx.eventBus();
      MessageConsumer<String> consumer = eventBus.consumer("scan.book");
      consumer.handler(path -> {
        List<Book> books = LibraryUtils.scan(path.body());
        BookRepository.getInstance().saveAll(books);
      });
      BookshelfRepository.getInstance().findAll().onSuccess(h -> {
        h.forEach(bookshelf -> {
          eventBus.publish("scan.book", bookshelf.getPath());
        });
      });
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


  }

}

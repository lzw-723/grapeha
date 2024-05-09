package fun.lzwi.grapeha;

import fun.lzwi.grapeha.db.HSQLDB;
import fun.lzwi.grapeha.db.repository.BookRepository;
import fun.lzwi.grapeha.library.Book;
import fun.lzwi.grapeha.library.Library;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;

import java.util.List;

public class LibraryVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    super.start(startPromise);

    HSQLDB.init(vertx).onSuccess(pool -> {
      BookRepository.getInstance().init(pool);
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

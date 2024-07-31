package fun.lzwi.grapeha.verticle;

import fun.lzwi.grapeha.db.repository.BookRepository;
import fun.lzwi.grapeha.db.repository.BookshelfRepository;
import fun.lzwi.grapeha.library.Book;
import fun.lzwi.grapeha.library.LibraryUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;

import java.util.List;

public class TaskerVerticle extends AbstractVerticle {
  @Override
  public void start() throws Exception {
    super.start();
    EventBus eventBus = vertx.eventBus();
    eventBus.consumer(DatabaseVerticle.EVENT_STARTED).handler(msg -> {

      MessageConsumer<String> consumer = eventBus.consumer("scan.book");
      consumer.handler(path -> {
        List<Book> books = LibraryUtils.scan(path.body());
        BookRepository.getInstance().saveAll(books);
      });
      BookshelfRepository.getInstance().findAll().onSuccess(h -> h.forEach(bookshelf -> eventBus.publish("scan.book", bookshelf.getPath())));

    });

  }


}

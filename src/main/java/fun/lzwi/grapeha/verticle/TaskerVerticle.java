package fun.lzwi.grapeha.verticle;

import fun.lzwi.grapeha.db.repository.BookRepository;
import fun.lzwi.grapeha.library.BookShelfUtils;
import fun.lzwi.grapeha.library.BookUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;

import java.util.stream.Collectors;

// 注册任务
public class TaskerVerticle extends AbstractVerticle {

  //    public static final String ACTION_SCAN_BOOK = "action.scan.book";
  public static final String ACTION_SCAN_BOOKS_IN_BOOKSHELF = "action.scan.book.in.bookshelf";

  //  public static final String ACTION_SCAN_BOOKSHELF = "action.scan.bookshelf";
  //  public static final String ACTION_GENERATE_BOOK_COVER = "action.generate.book.cover";
  //  public static final String ACTION_GENERATE_BOOK_COVER_IN_BOOKSHELF =
  // "action.generate.book.cover.in.bookshelf";
  //  public static final String TASKER_SCAN_ALL_BOOKSHELVES = "tasker.scan.books";

  @Override
  public void start() throws Exception {
    super.start();
    EventBus eventBus = vertx.eventBus();

    MessageConsumer<String> consumer = eventBus.consumer(ACTION_SCAN_BOOKS_IN_BOOKSHELF);
    consumer.handler(message -> scanBooksInBookshelf(message.body()));
  }

  // 扫描图书
  private Future<Boolean> scanBook(String path) {
    return Future.succeededFuture()
        .map(a -> BookUtils.scanBook(path))
        .compose(book -> BookRepository.getInstance().save(book));
  }

  // 扫描书架
  private Future<Boolean> scanBooksInBookshelf(String dir) {
    return Future.succeededFuture()
        .map(
            v ->
                BookShelfUtils.listBookFiles(dir).stream()
                    .map(file -> scanBook(file.getPath()))
                    .collect(Collectors.toList()))
        .compose(futures -> Future.all(futures).compose(v -> Future.succeededFuture(true)));
  }

  // 生成封面
  private Future<Boolean> generateBookCover(String bookId) {
    return Future.succeededFuture()
        .compose(n -> BookRepository.getInstance().findById(bookId))
        .map(v -> BookUtils.generateCover(v.getPath()));
  }
}

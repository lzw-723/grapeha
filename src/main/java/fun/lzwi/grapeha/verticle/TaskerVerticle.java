package fun.lzwi.grapeha.verticle;

import fun.lzwi.grapeha.db.repository.BookRepository;
import fun.lzwi.grapeha.library.BookShelfUtils;
import fun.lzwi.grapeha.library.BookUtils;
import fun.lzwi.grapeha.library.CacheUtils;
import fun.lzwi.grapeha.library.bean.Book;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.util.stream.Collectors;

// 注册任务
public class TaskerVerticle extends AbstractVerticle {

  //    public static final String ACTION_SCAN_BOOK = "action.scan.book";
  public static final String ACTION_SCAN_BOOKS_IN_BOOKSHELF = "action.scan.book.in.bookshelf";

  //  public static final String ACTION_SCAN_BOOKSHELF = "action.scan.bookshelf";
  //  public static final String ACTION_GENERATE_BOOK_COVER = "action.generate.book.cover";
  public static final String ACTION_GENERATE_BOOK_COVER_IN_BOOKSHELF =
      "action.generate.book.cover.in.bookshelf";

  //  public static final String TASKER_SCAN_ALL_BOOKSHELVES = "tasker.scan.books";

  public static final Logger logger = LoggerFactory.getLogger(TaskerVerticle.class);

  @Override
  public void start() throws Exception {
    super.start();
    EventBus eventBus = vertx.eventBus();

    MessageConsumer<String> consumer = eventBus.consumer(ACTION_SCAN_BOOKS_IN_BOOKSHELF);
    consumer.handler(message -> scanBooksInBookshelf(message.body()));

    eventBus
        .consumer(ACTION_GENERATE_BOOK_COVER_IN_BOOKSHELF)
        .handler(message -> generateBookCoversInBookshelf((String) message.body()));
  }

  // 扫描图书
  private Future<Boolean> scanBook(String path) {
    //    Future scanNew = Future.succeededFuture()
    //        .map(a -> BookUtils.scanBook(path));
    //    Future update = scanNew.compose(book -> BookRepository.getInstance().save(book));
    //        .compose(book -> BookRepository.getInstance().save(book));
    return BookRepository.getInstance()
        .findByPath(path)
        // 更新已经在数据库的
        //        .map(
        //            book -> {
        //              Book book2 = BookUtils.scanBook(path);
        //              book2.setId(book.getId());
        //              return book2;
        //            })
        //        .map(book -> BookRepository.getInstance().update(book))
        .map(a -> true)
        .onFailure(
            h -> {
              logger.info("发现新图书：%s。".formatted(path));
              Book book = BookUtils.scanBook(path);
              BookRepository.getInstance().save(book);
            });
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
  private Future<Boolean> generateBookCover(String path) {
    return Future.succeededFuture()
        //        .compose(n -> BookRepository.getInstance().findById(bookId))
        .map(
            a -> {
              String bookCoverPath = CacheUtils.getBookCoverPath(path);
              if (!CacheUtils.isExist(bookCoverPath)) {
                BookUtils.generateCover(path);
                return true;
              }
              return false;
            });
  }

  private Future<Boolean> generateBookCoversInBookshelf(String dir) {
    return Future.succeededFuture()
        .map(
            v ->
                BookShelfUtils.listBookFiles(dir).stream()
                    .map(file -> generateBookCover(file.getPath()))
                    .collect(Collectors.toList()))
        .compose(futures -> Future.all(futures).compose(v -> Future.succeededFuture(true)));
  }
}

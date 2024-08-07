package fun.lzwi.grapeha.db.repository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import fun.lzwi.grapeha.library.bean.Book;
import io.vertx.core.Future;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

public class BookRepository {
  private JDBCPool pool;

  // 私有构造方法，防止被实例化
  private BookRepository() {}

  // 静态内部类，该类被加载时会初始化instance，而且由JVM来保证线程安全
  private static class SingletonHolder {
    // 静态初始化器，由JVM来保证线程安全
    private static final BookRepository INSTANCE = new BookRepository();
  }

  // 提供全局访问点
  public static BookRepository getInstance() {
    return SingletonHolder.INSTANCE;
  }

  public void init(JDBCPool pool) {
    this.pool = pool;
  }

  public Future<Boolean> save(Book book) {
    return pool.preparedQuery(
            "INSERT INTO Books(id, title, author, date, description, path, last_update) VALUES (?, ?, ?, ?, ?, ?, ?);")
        .execute(
            Tuple.of(
                UUID.randomUUID().toString(),
                book.getName(),
                book.getAuthor(),
                book.getDate(),
                book.getDescription(),
                book.getPath(),
                Date.from(Instant.now())))
        .onFailure(Throwable::printStackTrace)
        .map(
            rows -> {
              return true;
            });
  }

  // 实现update
  public Future<Boolean> update(Book book) {
    return pool.preparedQuery(
            "UPDATE Books SET title=?, author=?, date=?, description=?, path=?, last_update=? WHERE id=?")
        .execute(
            Tuple.of(
                book.getName(),
                book.getAuthor(),
                book.getDate(),
                book.getDescription(),
                book.getPath(),
                Date.from(Instant.now()).toString(),
                book.getId()))
        .map(rows -> true);
  }

  public Future<Boolean> saveOrUpdate(Book book) {
    if (book.getId() == null) {
      return save(book);
    } else {
      return update(book);
    }
  }

  public Future<Boolean> saveAll(List<Book> Books) {
    return Future.all(Books.parallelStream().map(this::save).collect(Collectors.toList()))
        .map(true);
  }

  public Future<Book> findById(String id) {
    return pool.preparedQuery(
            "SELECT id,title,author,date,description,path,last_update FROM Books WHERE id=?")
        .execute(Tuple.of(id))
        .map(rows -> getBook(rows.iterator().next()));
  }

  public Future<Book> findByPath(String path) {
    return pool.preparedQuery(
            "SELECT id,title,author,date,description,path,last_update FROM Books WHERE path=?")
        .execute(Tuple.of(path))
        .map(rows -> getBook(rows.iterator().next()));
  }

  private Book getBook(Row row) {
    Book book = new Book();
    book.setId(row.getString("id".toUpperCase()));
    book.setName(row.getString("title".toUpperCase()));
    book.setAuthor(row.getString("author".toUpperCase()));
    book.setDate(row.getString("date".toUpperCase()));
    book.setDescription(row.getString("description".toUpperCase()));
    book.setPath(row.getString("path".toUpperCase()));
    book.setLastUpdate(row.getString("last_update".toUpperCase()));
    return book;
  }

  public Future<List<Book>> findAll() {
    return pool.query("SELECT id,title,author,date,description,path,last_update FROM Books")
        .execute()
        .map(
            rows -> {
              List<Book> Books = new ArrayList<>();
              for (Row row : rows) {
                Books.add(getBook(row));
              }
              return Books;
            });
  }

  public Future<Boolean> deleteAll() {
    return pool.query("DELETE FROM Books").execute().map(true);
  }
}

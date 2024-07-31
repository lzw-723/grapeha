package fun.lzwi.grapeha.db.repository;

import fun.lzwi.grapeha.library.Bookshelf;
import io.vertx.core.Future;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


public class BookshelfRepository {
  private JDBCPool pool;

  // 私有构造方法，防止被实例化
  private BookshelfRepository() {
  }

  // 静态内部类，该类被加载时会初始化instance，而且由JVM来保证线程安全
  private static class SingletonHolder {
    // 静态初始化器，由JVM来保证线程安全
    private static final BookshelfRepository INSTANCE = new BookshelfRepository();
  }

  // 提供全局访问点
  public static BookshelfRepository getInstance() {
    return BookshelfRepository.SingletonHolder.INSTANCE;
  }

  public void init(JDBCPool pool) {
    this.pool = pool;
  }

  private Bookshelf getBookshelf(Row row) {
    Bookshelf bookshelf = new Bookshelf();
    bookshelf.setId(row.getString("id".toUpperCase()));
    bookshelf.setTitle(row.getString("title".toUpperCase()));
    bookshelf.setDescription(row.getString("description".toUpperCase()));
    bookshelf.setPath(row.getString("path".toUpperCase()));
    bookshelf.setCover(row.getString("cover".toUpperCase()));
    return bookshelf;
  }

  public Future<Bookshelf> findById(String id) {
    return pool.preparedQuery("SELECT id,title,description,path,cover FROM Bookshelves WHERE id =?").execute(Tuple.of(id)).map(rows -> getBookshelf(rows.iterator().next()));
  }

  public Future<Boolean> save(Bookshelf bookshelf) {
    return pool.preparedQuery("INSERT INTO Bookshelves (id,title,description,path,cover) VALUES (?,?,?,?,?)").execute(Tuple.of(UUID.randomUUID(), bookshelf.getTitle(), bookshelf.getDescription(), bookshelf.getPath(), bookshelf.getCover())).map(rows -> true);
  }

  public Future<Boolean> update(Bookshelf bookshelf) {
    return pool.preparedQuery("UPDATE Bookshelves SET title=?,description=?,path=?,cover=? WHERE id=?").execute(Tuple.of(bookshelf.getTitle(), bookshelf.getDescription(), bookshelf.getPath(), bookshelf.getCover(), bookshelf.getId())).map(rows -> true);
  }

  public Future<Boolean> delete(String id) {
    return pool.preparedQuery("DELETE FROM Bookshelves WHERE id=?").execute(Tuple.of(id)).map(rows -> true);
  }

  public Future<Boolean> deleteAll() {
    return pool.preparedQuery("DELETE FROM Bookshelves").execute().map(rows -> true);
  }

  public Future<Boolean> saveAll(List<Bookshelf> bookshelves) {
    Future<Boolean> future = Future.all(bookshelves.parallelStream().map(this::save).collect(Collectors.toList())).map(true);
    return future;
  }

  public Future<List<Bookshelf>> findAll() {
    return pool.preparedQuery("SELECT id,title,description,path,cover FROM Bookshelves").execute().map(rows -> {
      List<Bookshelf> bookshelves = new ArrayList<>();
      rows.forEach(row -> bookshelves.add(getBookshelf(row)));
      return bookshelves;
    });
  }
  public Future<Integer> count(){
    return pool.preparedQuery("SELECT COUNT(id) FROM Bookshelves").execute().map(rows -> {
      return rows.iterator().next().getInteger(0);
    });
  }

}

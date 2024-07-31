package fun.lzwi.grapeha.db.repository;

import java.util.ArrayList;
import java.util.List;

import fun.lzwi.grapeha.library.User;
import io.vertx.core.Future;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

public class UserRepository {
  private JDBCPool pool;

  // 私有构造方法，防止被实例化
  private UserRepository() {
  }

  // 静态内部类，该类被加载时会初始化instance，而且由JVM来保证线程安全
  private static class SingletonHolder {
    // 静态初始化器，由JVM来保证线程安全
    private static final UserRepository INSTANCE = new UserRepository();
  }

  // 提供全局访问点
  public static UserRepository getInstance() {
    return SingletonHolder.INSTANCE;
  }

  public void init(JDBCPool pool) {
    this.pool = pool;
  }

  User getUser(Row row) {
    User user = new User();
    user.setId(row.getInteger("id".toUpperCase()));
    user.setUsername(row.getString("username".toUpperCase()));
    user.setPassword(row.getString("password".toUpperCase()));
    return user;
  }

  public Future<User> findById(int id) {
    return pool.preparedQuery("SELECT id,username,password FROM Users WHERE id=?").execute(Tuple.of(id))
        .map(rows -> getUser(rows.iterator().next()));
  }

  public Future<User> findByUsername(String username) {
    return pool.preparedQuery("SELECT id,username,password FROM Users WHERE username=?").execute(Tuple.of(username))
        .map(rows -> getUser(rows.iterator().next()));
  }

  public Future<List<User>> findAll() {
    return pool.preparedQuery("SELECT id,username,password FROM Users").execute()
        .map(rows -> {
          List<User> users = new ArrayList<>();
          for (Row row : rows) {
            users.add(getUser(row));
          }
          return users;
        });
  }

  public Future<Void> save(User user) {
    return pool.preparedQuery("INSERT INTO Users(username,password) VALUES (?,?);")
        .execute(Tuple.of(user.getUsername(), user.getPassword())).onFailure(Throwable::printStackTrace).mapEmpty();
  }

  public Future<Integer> count() {
    return pool.query("SELECT COUNT(id) FROM Users;").execute()
        .map(rows -> rows.iterator().next().getInteger(0)).onFailure(Throwable::printStackTrace);
  }

  public Future<Void> delete(int id) {
    return pool.preparedQuery("DELETE FROM Users WHERE id=?").execute(Tuple.of(id)).onFailure(Throwable::printStackTrace).mapEmpty();
  }
  public Future<Void> deleteAll() {
    return pool.preparedQuery("DELETE FROM Users").execute().onFailure(Throwable::printStackTrace).mapEmpty();
  }
}

package fun.lzwi.grapeha.db;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;

public class HSQLDB {
  private static JDBCPool pool;

  public static Future<JDBCPool> init(Vertx vertx) {
    Logger logger = LoggerFactory.getLogger(HSQLDB.class);

    final JsonObject config = new JsonObject().put("url", "jdbc:hsqldb:file:./config/db/testdb").put("datasourceName", "pool-name").put("username", "sa").put("password", "").put("max_pool_size", 16);

    pool = JDBCPool.pool(vertx, config);

    String createTableSQL = "CREATE TABLE IF NOT EXISTS BOOKS (" + "ID VARCHAR(36) PRIMARY KEY," + "TITLE VARCHAR(255) NOT NULL," + "AUTHOR VARCHAR(255)," + "PATH VARCHAR(255) NOT NULL" + ");";
    return pool.query(createTableSQL).execute().onFailure(e -> {
      logger.error("数据库初始化失败！", e);
    }).onSuccess(r -> {
      logger.info("数据库初始化成功。");
    }).map(pool);
  }
}

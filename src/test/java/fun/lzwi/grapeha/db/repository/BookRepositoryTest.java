package fun.lzwi.grapeha.db.repository;

import fun.lzwi.grapeha.db.HSQLDB;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookRepositoryTest {
  @BeforeAll
  static void beforeAll() {
//    HSQLDB.init(vertx).onSuccess(pool -> BookRepository.getInstance().init(pool));
  }

  @Test
  void save() {
  }

  @Test
  void saveAll() {
  }

  @Test
  void findAll() {
  }
}

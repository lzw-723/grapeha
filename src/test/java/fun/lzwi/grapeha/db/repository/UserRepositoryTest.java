package fun.lzwi.grapeha.db.repository;

import fun.lzwi.grapeha.library.bean.User;
import io.vertx.sqlclient.Row;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

  @Test
  public void testGetUser() {
    // 创建模拟的 Row 对象
    Row row = Mockito.mock(Row.class);

    // 设置模拟的行数据
    Mockito.when(row.getInteger("ID".toUpperCase())).thenReturn(1);
    Mockito.when(row.getString("USERNAME".toUpperCase())).thenReturn("testUser");
    Mockito.when(row.getString("PASSWORD".toUpperCase())).thenReturn("testPassword");

    // 调用待测试方法
    UserRepository repository = UserRepository.getInstance();
    User user = repository.getUser(row);

    // 验证结果
    assertEquals(1, user.getId());
    assertEquals("testUser", user.getUsername());
    assertEquals("testPassword", user.getPassword());
  }

  @Test
  void findById() {
  }

  @Test
  void findByUsername() {
  }

  @Test
  void findAll() {
  }

  @Test
  void save() {
  }

  @Test
  void count() {
  }
}

package fun.lzwi.grapeha.library.reader;

import fun.lzwi.epubime.Resource;
import fun.lzwi.epubime.easy.EasyEpub;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class EpubReader {

  private static InputStream getIn(String epubPath, String href) {
    try {
      EasyEpub epub = new EasyEpub(epubPath);
      Resource resource = epub.getResource(href);
      return resource.getInputStream();
    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String getResourceType(String epubPath, String href) {
    try {
      EasyEpub epub = new EasyEpub(epubPath);
      return epub.getResources().stream().filter((r) -> r.getHref().equals(href)).findFirst().get().getType();
    } catch (ParserConfigurationException | SAXException | IOException e) {
      e.printStackTrace();
    }
    return "application/xhtml+xml";
  }

  public static Future<Buffer> getResource(String epubPath, String href) {
    return Future.succeededFuture(href).map(h -> {

      Buffer res = Buffer.buffer();
      try {
        // 创建一个文件输入流作为示例的InputStream
        InputStream inputStream = getIn(epubPath, href);

        // 将InputStream转换为ReadableByteChannel
        ReadableByteChannel channel = Channels.newChannel(inputStream);

        // 创建ByteBuffer用于存储数据
        ByteBuffer buffer = ByteBuffer.allocate(1024); // 分配1024字节的缓冲区

        // 读取数据到ByteBuffer
        while (channel.read(buffer) > 0) {
          // 切换Buffer到读模式
          buffer.flip();

          // 处理数据，例如打印到控制台
          while (buffer.hasRemaining()) {
            // System.out.print((char) buffer.get());
            res.appendByte(buffer.get());
          }

          // 清空Buffer，准备下一次读取
          buffer.clear();
        }

        // 关闭资源
        channel.close();
        inputStream.close();

      } catch (Exception e) {
        e.printStackTrace();
      }
      return res;
    });
  }
}

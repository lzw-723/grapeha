package fun.lzwi.grapeha.library.parser;

import java.io.*;

import fun.lzwi.epubime.easy.EasyEpub;
import fun.lzwi.epubime.util.XmlUtils;
import fun.lzwi.grapeha.library.bean.Book;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

public class EpubParser {
  private static final Logger logger = LoggerFactory.getLogger(EpubParser.class);

  public static Book parser(File file) throws ParserConfigurationException, IOException, SAXException {
    Book book = new Book();
    book.setPath(file.getPath());
    EasyEpub easyEpub = new EasyEpub(file);
    if (easyEpub.getTitle() != null) {
      book.setName(easyEpub.getTitle());
    } else {
      book.setName(file.getName());
    }

    book.setAuthor(easyEpub.getAuthor());
    book.setDate(easyEpub.getDate());

    if (easyEpub.getCover() == null) {

    } else if (easyEpub.getDescription().contains("<")) {
      // 处理html
      InputStream in = new ByteArrayInputStream(easyEpub.getDescription().getBytes());
      Document document = XmlUtils.getDocument(in);
      String content = document.getTextContent();
      book.setDescription(content);
    } else {
      book.setDescription(easyEpub.getDescription());
    }
    return book;
  }

  public static InputStream getCoverInputStream(String bookPath) throws IOException, ParserConfigurationException,
          SAXException {
    EasyEpub easyEpub = new EasyEpub(new File(bookPath));
    String cover = easyEpub.getCover();
    InputStream in = easyEpub.getResource(cover).getInputStream();
    //    Path destinationPath = Paths.get(String.format("./config/cache/%s.jpg", book.getPath().hashCode()));
    //    Files.copy(in, destinationPath, StandardCopyOption.REPLACE_EXISTING);
    return in;
  }
}

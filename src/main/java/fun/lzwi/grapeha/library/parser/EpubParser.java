package fun.lzwi.grapeha.library.parser;

import java.io.*;

import fun.lzwi.epubime.epub.EpubBook;
import fun.lzwi.epubime.epub.EpubParseException;
import fun.lzwi.epubime.epub.EpubResource;
import fun.lzwi.epubime.epub.Metadata;
import fun.lzwi.grapeha.library.bean.Book;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

public class EpubParser {
  private static final Logger logger = LoggerFactory.getLogger(EpubParser.class);

  public static Book parser(File file) throws ParserConfigurationException, IOException, SAXException,
    EpubParseException {
    Book book = new Book();
    book.setPath(file.getPath());
    fun.lzwi.epubime.epub.EpubParser parser = new fun.lzwi.epubime.epub.EpubParser(file);
    EpubBook epub = parser.parse();
    Metadata metadata = epub.getMetadata();
    if (metadata.getTitle() != null) {
      book.setName(metadata.getTitle());
    } else {
      book.setName(file.getName());
    }

    book.setAuthor(metadata.getCreator());
    book.setDate(metadata.getDate());
    if (metadata.getDescription() != null) {
      book.setDescription(Jsoup.parse(metadata.getDescription()).text());
    }
    return book;
  }

  public static InputStream getCoverInputStream(String bookPath) throws EpubParseException {
    fun.lzwi.epubime.epub.EpubParser parser = new fun.lzwi.epubime.epub.EpubParser(new File(bookPath));
    EpubBook epub = parser.parse();
    EpubResource cover = epub.getCover();
    return new ByteArrayInputStream(cover.getData());
  }
}

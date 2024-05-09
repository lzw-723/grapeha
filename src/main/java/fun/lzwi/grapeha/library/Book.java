package fun.lzwi.grapeha.library;

import java.beans.JavaBean;
import java.io.Serializable;

import io.vertx.core.shareddata.Shareable;

@JavaBean
public class Book implements Shareable, Serializable {

  private String id;
  private String path;
  private String name;
  private String cover;

  /**
   * @return the cover
   */
  public String getCover() {
    return cover;
  }

  /**
   * @param cover the cover to set
   */
  public void setCover(String cover) {
    this.cover = cover;
  }

  /**
   * @return the path
   */
  public String getPath() {
    return path;
  }

  /**
   * @param path the path to set
   */
  public void setPath(String path) {
    this.path = path;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}

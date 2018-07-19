package life.qbic.projectbrowser.helpers;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

/**
 * Converts normal newlines to html newlines as used in html components. can be extended to convert
 * different html tags
 * 
 * @author Andreas Friedrich
 * 
 */
public class HTMLConverter implements Converter<String, String> {

  /**
   * 
   */
  private static final long serialVersionUID = 1898828004063038594L;

  @Override
  public Class<String> getModelType() {
    return String.class;
  }

  @Override
  public Class<String> getPresentationType() {
    return String.class;
  }

  @Override
  public String convertToModel(String value, Class<? extends String> targetType, Locale locale)
      throws ConversionException {
    return value.replace("\n", "<br>");
  }

  @Override
  public String convertToPresentation(String value, Class<? extends String> targetType,
      Locale locale) throws ConversionException {
    return value.replace("<br>", "\n");
  }

}

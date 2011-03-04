package com.google.sitebricks.conversion;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.sitebricks.SitebricksModule;

/**
 * @author JRodriguez
 * @author John Patterson (jdpatterson@gmail.com)
 */
public class DateConverters {
  public static void register(SitebricksModule module) {
    module.converter(LocalizedDateStringConverter.class);
    module.converter(DateLongConverter.class);
    module.converter(DateCalendarConverter.class);
    module.converter(CalendarLongConverter.class);
    module.converter(CalendarStringConverter.class);
  }

  public static class DateLongConverter implements Converter<Long, Date> {
    @Override
    public Date to(Long source) {
      return new Date(source);
    }

    @Override
    public Long from(Date target) {
      return target.getTime();
    }
  }
  
  public static class DateCalendarConverter implements Converter<Date, Calendar> {

    @Override
    public Calendar to(Date source) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(source);
      return calendar;
    }

    @Override
    public Date from(Calendar target) {
      return target.getTime();
    }
  }

  public static class DateStringConverter implements Converter<String, Date> {

    protected DateFormat format;
    
    public DateStringConverter() {
      this.format = DateFormat.getInstance();
    }

    public DateStringConverter(DateFormat format) {
      this.format = format;
    }

    public DateStringConverter(String format) {
      this.format = new SimpleDateFormat(format);
    }
    
    @Override
    public Date to(String source) {
      try {
        return getFormat().parse(source);
      }
      catch (ParseException e) {
        throw new IllegalArgumentException("Invalid date format", e);
      }
    }

    @Override
    public String from(Date target) {
      return format.format(target);
    }
    
    protected DateFormat getFormat() {
      return format;
    }
  }
  
  public static class LocalizedDateStringConverter extends DateStringConverter {
   
    private int dateStyle;
    private int timeStyle;
    private Provider<Locale> provider;
    
    public LocalizedDateStringConverter() {
      this(DateFormat.LONG, DateFormat.LONG);
    }
    public LocalizedDateStringConverter(int dateStyle, int timeStyle) {
      this.dateStyle = dateStyle;
      this.timeStyle = timeStyle;
    }
    
    @Inject(optional=true)
    public void setLocaleProvider(Provider<Locale> provider) {
      this.provider = provider;
    }
    
    @Override
    protected DateFormat getFormat() {
      if (provider != null) {
        return DateFormat.getDateTimeInstance(dateStyle, timeStyle, provider.get());
      }
      else {
        return super.getFormat();
      }
    }
  }

  public static class CalendarStringConverter implements Converter<Calendar, String> {

    private final TypeConverter converter;

    @Inject
    public CalendarStringConverter(TypeConverter converter) {
      this.converter = converter;
    }
    
    @Override
    public String to(Calendar source) {
      Date date = converter.convert(source, Date.class);
      return converter.convert(date, String.class);
    }

    @Override
    public Calendar from(String target) {
      Date date = converter.convert(target, Date.class);
      return converter.convert(date, Calendar.class);
    }
  }

  public static class CalendarLongConverter implements Converter<Calendar, Long> {

    private final TypeConverter converter;

    @Inject
    public CalendarLongConverter(TypeConverter converter) {
      this.converter = converter;
    }
    
    @Override
    public Long to(Calendar source) {
      Date date = converter.convert(source, Date.class);
      return converter.convert(date, Long.class);
    }

    @Override
    public Calendar from(Long target) {
      Date date = converter.convert(target, Date.class);
      return converter.convert(date, Calendar.class);
    }
  }
}
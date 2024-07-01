package org.example.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Primary
public class JacksonConfig extends ObjectMapper {

  private static final Logger LOGGER = LogManager.getLogger(JacksonConfig.class);

  private static JacksonConfig mapper;

  static {
    mapper = new JacksonConfig();

    // Allows case-insensitive enums
    mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
  }

  public JacksonConfig() {
    setSerializationInclusion(JsonInclude.Include.NON_NULL);
    enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
    registerModule(new JavaTimeModule());
    registerModule(new JodaModule());
    registerModule(new Hibernate6Module().disable(Hibernate6Module.Feature.USE_TRANSIENT_ANNOTATION));
    final PropertyFilter serializationFilter = new SimpleBeanPropertyFilter() {
      @Override
      public void serializeAsField(final Object o, final JsonGenerator jgen,
                                   final SerializerProvider provider, final PropertyWriter writer) throws Exception {
        if (include(writer)) {
          writer.serializeAsField(o, jgen, provider);

        } else if (!jgen.canOmitFields()) {
          writer.serializeAsOmittedField(o, jgen, provider);
        }
      }

      @Override
      protected boolean include(BeanPropertyWriter writer) {
        return true;
      }

      @Override
      protected boolean include(PropertyWriter writer) {
        return true;
      }
    };
    final FilterProvider filters = new SimpleFilterProvider().addFilter("serializationFilter", serializationFilter);
    setFilterProvider(filters);
  }

  public static ObjectMapper getMapper() {
    return mapper;
  }

  public static String serialize(final Object obj) {
    String serialized = null;
    if (obj != null) {
      try {
        serialized = getMapper().writeValueAsString(obj);
      } catch (final IOException ex) {
        LOGGER.error("JSON serialization failed", ex);
      }
    }
    return serialized;
  }

}

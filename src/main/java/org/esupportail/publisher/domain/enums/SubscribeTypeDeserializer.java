package org.esupportail.publisher.domain.enums;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;

/**
 * Created by jgribonvald on 01/04/15.
 */
public class SubscribeTypeDeserializer extends JsonDeserializer<SubscribeType> {
    @Override
    public SubscribeType deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        SubscribeType type = SubscribeType.fromName(jp.getValueAsString());
        if (type != null) {
            return type;
        }
        throw new JsonMappingException(String.format("Invalid value '%s' for %s, must be in range of %s", jp.getValueAsString(),
            SubscribeType.class.getSimpleName(), SubscribeType.values().toString()));
    }
}

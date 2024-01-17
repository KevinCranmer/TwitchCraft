package me.crazycranberry.streamcraft.commands;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommandUtils {
    private static final ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

    public static ObjectMapper mapper() {
        return mapper;
    }
}

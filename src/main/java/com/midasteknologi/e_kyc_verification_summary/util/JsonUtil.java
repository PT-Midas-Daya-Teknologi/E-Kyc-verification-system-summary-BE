package com.midasteknologi.e_kyc_verification_summary.util;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

public class JsonUtil {

    public static String writeValueAsString(ObjectMapper objectMapper, Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JacksonException e) {
            return null;
        }
    }
}

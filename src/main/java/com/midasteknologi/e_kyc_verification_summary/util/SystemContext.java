package com.midasteknologi.e_kyc_verification_summary.util;

import java.util.HashMap;
import java.util.Map;

public class SystemContext {

    public static Map<String, Object> context = new HashMap<>();

    public static Object getContext(String key) {
        return context.get(key);
    }

    public static void setContext(String key, Object value) {
        context.put(key, value);
    }
}

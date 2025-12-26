package org.example.postgraduaterecommendation.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.postgraduaterecommendation.exception.XException;
import org.springframework.stereotype.Component;
//  JSON 序列化/反序列化工具类
@Component
@RequiredArgsConstructor
public class ObjectMapperComponent {
    private final ObjectMapper objectMapper;

    //将 Java 对象序列化为 JSON 字符串
    public String writeValueAsString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw XException.builder().codeNum(500).message(e.getMessage()).build();
        }
    }

    //将 JSON 字符串反序列化为 Java 对象
    public Object readValue(String json, TypeReference<?> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw XException.builder().codeNum(500).message(e.getMessage()).build();
        }
    }
}
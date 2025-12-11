package org.example.postgraduaterecommendation.component;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wuwenjin
 */
//防止 Long 类型精度丢失
//将所有的 Long 类型在序列化成 JSON 时，自动转换为字符串类型
//@Configuration 注解的类会被 Spring 扫描并识别为配置类，在应用启动时自动执行
@Configuration// ← 这个注解告诉Spring：这是一个配置类
public class JacksonConfig {
    @Bean// ← 这个注解告诉Spring：这个方法返回的对象要纳入容器管理
    public Jackson2ObjectMapperBuilderCustomizer serializer() {
        return builder -> builder.serializerByType(Long.class, ToStringSerializer.instance);
    }
    //Jackson2ObjectMapperBuilderCustomizer:Jackson的定制化接口，Spring Boot会自动检测所有这种类型的bean，并用它们来配置全局的Jackson行为
}
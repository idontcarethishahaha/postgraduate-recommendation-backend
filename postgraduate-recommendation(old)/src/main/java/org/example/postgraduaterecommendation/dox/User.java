package org.example.postgraduaterecommendation.dox;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;

import java.time.LocalDateTime;

/**
 * @author wuwenjin
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    public static final String COLLEGE_ADMIN = "yHJ7";
    public static final String ADMIN = "Fr5g";
    public static final String COUNSELOR = "Ca24";
    public static final String STUDENT = "dA5q";

    @Id
    @CreatedBy
    private Long id; //bigint对应long
    private String name;
    private Long collegeId;
    private String account;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)//进行序列化时忽略
    private String password;
    private String role;
    private String tel;
    @ReadOnlyProperty
    private LocalDateTime createTime;
    @ReadOnlyProperty
    private LocalDateTime updateTime;
}
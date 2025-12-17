package org.example.postgraduaterecommendation.util;

import lombok.RequiredArgsConstructor;
import org.example.postgraduaterecommendation.exception.Code;
import org.example.postgraduaterecommendation.exception.XException;
import org.example.postgraduaterecommendation.repository.MajorRepository;
import org.example.postgraduaterecommendation.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author wuwenjin
 */
@Component
@RequiredArgsConstructor
public class FileDirUtil {
    private final MajorRepository majorRepository;
    private final UserRepository userRepository;

    // 返回 `学院/类别/专业` 目录名（缓存）
    @Cacheable(value = "majordirs", key = "#majorid")
    public String getMajorDirectoryName(long majorid) {
        return majorRepository.findFileDirectoryName(majorid)
                .orElseThrow(() -> XException.builder()
                        .codeNum(Code.ERROR)
                        .message("专业对应的目录名不存在")
                        .build());
    }

    // 返回 `姓名-学号` 目录名
    public String getUserFileDirectoryName(long uid) {
        Optional<String> dirName = userRepository.findFileDirectoryName(uid);
        if (dirName.isEmpty()) {
            throw XException.builder()
                    .codeNum(Code.ERROR)
                    .message("用户目录名不存在")
                    .build();
        }
        return dirName.get();
    }
}

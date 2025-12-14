package org.example.postgraduaterecommendation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.postgraduaterecommendation.dox.StuScore;
import org.example.postgraduaterecommendation.dox.User;
import org.example.postgraduaterecommendation.dto.StuScoreDTO;
import org.example.postgraduaterecommendation.exception.Code;
import org.example.postgraduaterecommendation.exception.XException;
import org.example.postgraduaterecommendation.vo.StuScoreVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.postgraduaterecommendation.repository.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {
    private final StuScoreRepository stuScoreRepository;
    private final StudentInfoRepository studentInfoRepository;
    private final UserRepository userRepository;

    //提交成绩
    //只提交一次成绩，不重复提交,可以修改
    @Transactional
    public StuScoreVO submitScore(StuScoreDTO dto, Long userId) {
        //学生是否存在
        validateStudentExists(userId);

        // 是否已有成绩记录（待审核/已认定都不允许重复提交）
        if (stuScoreRepository.existsByUserId(userId)) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("您已提交过成绩，不可重复提交")
                    .build();
        }

        //构建成绩记录,默认待审核状态
        StuScore stuScore = StuScore.builder()
                .userId(userId)
                .weightedScore(dto.getWeightedScore())
                .majorRank(dto.getMajorRank())
                .status(StuScore.STATUS_PENDING)
                .build();

        StuScore saved = stuScoreRepository.save(stuScore);
        return convertToVO(saved);
    }

    //修改成绩（仅待审核状态可修改）
    //已认定状态不可修改，待审核状态可修改成绩和排名
    @Transactional
    public StuScoreVO updateScore(StuScoreDTO dto, Long userId) {
        //学生是否存在
        validateStudentExists(userId);

        //查询学生已有成绩记录
        StuScore existingScore = stuScoreRepository.findByUserId(userId)
                .orElseThrow(() -> XException.builder()
                        .number(Code.ERROR)
                        .message("您尚未提交成绩，无法修改")
                        .build());

        //校验状态,已认定不可修改
        if (existingScore.getStatus().equals(StuScore.STATUS_APPROVED)) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("成绩已认定，不可修改")
                    .build();
        }

        //更新成绩
        existingScore.setWeightedScore(dto.getWeightedScore());
        existingScore.setMajorRank(dto.getMajorRank());
        //状态仍为待审核

        //保存并返回VO
        StuScore updated = stuScoreRepository.save(existingScore);
        return convertToVO(updated);
    }

    //查询学生个人成绩
    public StuScoreVO getStudentScore(Long userId) {
        //学生是否存在
        validateStudentExists(userId);

        //查询成绩
        StuScore stuScore = stuScoreRepository.findByUserId(userId).orElse(null);
        return stuScore != null ? convertToVO(stuScore) : null;
    }
    //------------------------------------------------------------------------------------
    //校验学生是否存在（用户表+学生信息表）
    private void validateStudentExists(Long userId) {
        // 用户是否存在且角色为学生
        User user = userRepository.findById(userId)
                .orElseThrow(() -> XException.builder()
                        .number(Code.ERROR)
                        .message("用户不存在")
                        .build());
        if (!User.STUDENT.equals(user.getRole())) {
            throw XException.builder()
                    .code(Code.FORBIDDEN)
                    .message("仅学生可操作成绩申报")
                    .build();
        }

        //学生信息是否存在（关联student_info表）
        studentInfoRepository.findByUserId(userId)
                .orElseThrow(() -> XException.builder()
                        .number(Code.ERROR)
                        .message("学生信息未完善，无法提交成绩")
                        .build());
    }

    private StuScoreVO convertToVO(StuScore stuScore) {
        String statusDesc = stuScore.getStatus().equals(StuScore.STATUS_PENDING)
                ? "待审核"
                : "已认定";

        return StuScoreVO.builder()
                .id(stuScore.getId())
                .weightedScore(stuScore.getWeightedScore())
                .majorRank(stuScore.getMajorRank())
                .status(stuScore.getStatus())
                .statusDesc(statusDesc)
                .build();
    }
}
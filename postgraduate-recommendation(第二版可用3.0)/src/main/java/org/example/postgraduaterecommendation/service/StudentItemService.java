package org.example.postgraduaterecommendation.service;

import lombok.RequiredArgsConstructor;
import org.example.postgraduaterecommendation.dox.StudentItem;
import org.example.postgraduaterecommendation.dox.StudentItemFile;
import org.example.postgraduaterecommendation.dox.StudentItemLog;
import org.example.postgraduaterecommendation.dto.StudentItemResp;
import org.example.postgraduaterecommendation.dto.StudentItemsDO;
import org.example.postgraduaterecommendation.dto.StudentItemsStatusDO;
import org.example.postgraduaterecommendation.exception.Code;
import org.example.postgraduaterecommendation.exception.XException;
import org.example.postgraduaterecommendation.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.example.postgraduaterecommendation.dox.StudentItem.Status.*;

@Service
@RequiredArgsConstructor
public class StudentItemService {
    private final StudentItemRepository studentItemRepository;
    private final UserRepository userRepository;
    private final MajorRepository majorRepository;
    private final StudentItemFileRepository studentItemFileRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final StudentItemLogRepository studentItemLogRepository;

    //添加学生指标
    @Transactional(rollbackFor = Exception.class)
    public StudentItem addStudentItem(StudentItem studentItem) {
        return studentItemRepository.save(studentItem);
    }

    // 返回 `学院/类别/专业` 目录名（缓存）
    @Cacheable(value = "majordirs", key = "#majorid")
    public String getMajorDirectoryName(long majorid) {
        return majorRepository.findFileDirectoryName(majorid)
                .orElseThrow(() -> XException.builder()
                        .codeNum(Code.ERROR)
                        .message("专业对应的目录名不存在")
                        .build());
    }


    //返回 `姓名-学号` 目录名
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

    //添加学生指标附件
    @Transactional(rollbackFor = Exception.class)
    public StudentItemFile addStudentItemFile(StudentItemFile sf) {
        return studentItemFileRepository.save(sf);
    }

    //查询学生指标
    public StudentItem getStudentItem(long uid, long stuitemid) {
        Optional<StudentItem> studentItem = studentItemRepository.findByUserId(uid, stuitemid);
        if (studentItem.isEmpty()) {
            throw XException.builder()
                    .codeNum(Code.ERROR)
                    .message("学生指标不存在")
                    .build();
        }
        return studentItem.get();
    }

    //根据根指标ID查询学生指标列表（转DTO）
    public List<StudentItemResp> listStudentItems(long uid, long rootitemid) {
        List<StudentItemsDO> doList = studentItemRepository.findByRootItemId(uid, rootitemid);
        return convertToDTO(doList);
    }

    //查询学生指标附件列表
    public List<StudentItemFile> listStudentItemFiles(long stuitemid) {
        return studentItemFileRepository.findByStudentItemIds(stuitemid);
    }

    //根据用户ID查询学生指标DTO列表
    public List<StudentItemResp> getStudentItemDTOs(long uid) {
        List<StudentItemsDO> doList = studentItemRepository.findByUserId(uid);
        return convertToDTO(doList);
    }

    //DO 转 DTO
    private List<StudentItemResp> convertToDTO(List<StudentItemsDO> dos) {
        return dos.stream()
                .collect(Collectors.groupingBy(StudentItemsDO::getId))
                .values()
                .stream()
                .map(itemGroup -> {
                    var first = itemGroup.getFirst();
                    var studentItemResp = new StudentItemResp();
                    BeanUtils.copyProperties(first, studentItemResp);
                    studentItemResp.setItemName(first.getItemName());
                    studentItemResp.setItemComment(first.getItemComment());

                    var files = itemGroup.stream()
                            .filter(record -> record.getFilename() != null && !record.getFilename().isEmpty())
                            .map(record -> StudentItemFile.builder()
                                    .id(record.getStudentItemFileId())
                                    .studentItemId(first.getId())
                                    .filename(record.getFilename())
                                    .build())
                            .toList();

                    studentItemResp.setFiles(files);
                    return studentItemResp;
                })
                .sorted(Comparator.comparing(StudentItemResp::getItemId))
                .toList();
    }

    //根据用户ID和文件ID查询文件路径
    public Path getFilePath(long uid, long fileid) {
        Optional<String> pathStr = studentItemFileRepository.findPath(uid, fileid);
        if (pathStr.isEmpty()) {
            throw XException.builder()
                    .codeNum(Code.ERROR)
                    .message("文件不存在")
                    .build();
        }
        return Path.of(pathStr.get());
    }

    //根据文件ID查询文件路径
    public Path getFilePath(long fileid) {
        Optional<StudentItemFile> stuFile = studentItemFileRepository.findById(fileid);
        if (stuFile.isEmpty()) {
            throw XException.builder()
                    .codeNum(Code.ERROR)
                    .message("文件不存在")
                    .build();
        }
        return Path.of(stuFile.get().getPath(), stuFile.get().getFilename());
    }

    //删除学生指标附件
    @Transactional(rollbackFor = Exception.class)
    public void removeStudentItemFile(long fileid) {
        studentItemFileRepository.deleteById(fileid);
    }

    //批量删除学生指标附件
    @Transactional(rollbackFor = Exception.class)
    public void removeStudentItemFiles(long studentitemid) {
        studentItemFileRepository.deleteByStudentItemId(studentitemid);
    }

    //删除学生指标
    @Transactional(rollbackFor = Exception.class)
    public void removeStudentItem(long id) {
        studentItemRepository.deleteById(id);
    }

    //更新学生指标信息
    @Transactional(rollbackFor = Exception.class)
    public int updateStudentItem(long uid, long stuid, String name, String comment, String status) {
        int affectedRows = studentItemRepository.updateByUserId(uid, stuid, name, comment, status);
        if (affectedRows == 0) {
            throw XException.builder()
                    .codeNum(Code.ERROR)
                    .message("更新条目不存在")
                    .build();
        }
        return affectedRows;
    }

    //根据专业ID查询学生指标统计信息
    public List<StudentItemsStatusDO> listStudentsInfos(long majorid) {
        return studentItemRepository.findStudentItemsInfos(majorid, PENDING_REVIEW, REJECTED, PENDING_MODIFICATION, CONFIRMED);
    }

    //根据用户ID查询学生指标统计信息
    public StudentItemsStatusDO getStudentItemsInfo(long uid) {
        Optional<StudentItemsStatusDO> statusDO = studentItemRepository.findStudentItemsInfo(uid, PENDING_REVIEW, REJECTED, PENDING_MODIFICATION, CONFIRMED);
        if (statusDO.isEmpty()) {
            throw XException.builder()
                    .codeNum(Code.ERROR)
                    .message("学生指标统计信息不存在")
                    .build();
        }
        return statusDO.get();
    }

    //更新学生指标状态
    @Transactional(rollbackFor = Exception.class)
    public int updateStatus(long uid, String status) {
        return studentItemRepository.updateStatus(uid, status);
    }

    //校验用户类别权限
    public boolean checkUserCategoryAuth(long sid, long adminid) {
        Integer result = userCategoryRepository.checkUsersInSameMajorCategory(sid, adminid);
        return result != null && result == 1;
    }

    //更新学生指标 + 保存审核日志
    @Transactional(rollbackFor = Exception.class)
    public void updteStudentItem(StudentItem stuItem, StudentItemLog log) {
        int affectedRows = studentItemRepository.update(stuItem.getId(), stuItem.getPoint(), stuItem.getStatus());
        if (affectedRows > 0) {
            studentItemLogRepository.save(log);
        }
    }

    //查询学生指标审核日志
    public List<StudentItemLog> listStudentItemLogs(long uid, long stuitemid) {
        return studentItemLogRepository.find(uid, stuitemid);
    }

}

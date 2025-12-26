package org.example.postgraduaterecommendation.controller;


import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.postgraduaterecommendation.dox.Item;
import org.example.postgraduaterecommendation.dox.StudentItem;
import org.example.postgraduaterecommendation.dox.StudentItemFile;
import org.example.postgraduaterecommendation.dox.WeightedScore;
import org.example.postgraduaterecommendation.exception.Code;
import org.example.postgraduaterecommendation.exception.XException;
import org.example.postgraduaterecommendation.service.*;
import org.example.postgraduaterecommendation.vo.ResultVO;
import org.example.postgraduaterecommendation.vo.TokenAttribute;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api/student/")
@RequiredArgsConstructor
public class StudentController {
    private final ItemService itemService;
    private final WeightedScoreService weightedScoreService;
    private final StudentItemService studentItemService;
    private final FileService fileService;
    private final MajorCategoryService majorCategoryService;

    // 获取一级指标
    @GetMapping("topitems")
    public ResultVO getItems(@RequestAttribute(TokenAttribute.MCID) long mcid) {
        List<Item> items = itemService.listTopItems(mcid);
        return ResultVO.success(items);
    }

    // 获取全部子items
    @GetMapping("items/{parentitemid}")
    public ResultVO getItems(@PathVariable long parentitemid,
                             @RequestAttribute(TokenAttribute.MCID) long mcid) {
        Object items = itemService.listItems(mcid, parentitemid);
        return ResultVO.success(items);
    }

    // 查询加权分
    @GetMapping("weightedscores")
    public ResultVO getWeightedScores(@RequestAttribute(TokenAttribute.UID) long uid) {
        try {
            WeightedScore weightedScore = weightedScoreService.getWeightedScore(uid);
            return ResultVO.success(weightedScore);
        } catch (Exception e) {
            return ResultVO.success(Map.of());
        }
    }

    // 提交/更新加权分
    @PostMapping("weightedscores")
    public ResultVO postWeightedScore(@RequestBody WeightedScore weightedScore,
                                      @RequestAttribute(TokenAttribute.UID) long uid) {
        try {
            // 查询现有加权分
            WeightedScore ws = weightedScoreService.getWeightedScore(uid);
            if (ws.getVerified() == WeightedScore.VERIFIED) {
                return ResultVO.error(Code.ERROR, "成绩已认定，无法修改");
            }
            // 更新加权分
            weightedScoreService.updateWeightedScore(uid, weightedScore.getScore(), weightedScore.getRanking(), WeightedScore.UNVERIFIED);
            return ResultVO.success();
        } catch (Exception e) {
            // 无数据则新增
            weightedScore.setNew();
            weightedScore.setId(uid);
            weightedScore.setVerified(WeightedScore.UNVERIFIED);
            weightedScoreService.addWeightedScore(weightedScore);
            return ResultVO.success();
        }
    }

    // 提交学生指标项
    @PostMapping("studentitems")
    public ResultVO postStudentItems(@RequestBody StudentItem studentItem,
                                     @RequestAttribute(TokenAttribute.UID) long uid,
                                     @RequestAttribute(TokenAttribute.MCID) long mcid) {
        try {
            // 校验指标项是否存在
            itemService.getItem(studentItem.getItemId(), mcid);
            // 构建学生指标项
            StudentItem stuI = StudentItem.builder()
                    .rootItemId(studentItem.getRootItemId())
                    .itemId(studentItem.getItemId())
                    .name(studentItem.getName())
                    .status(StudentItem.Status.PENDING_REVIEW)
                    .userId(uid)
                    .comment(studentItem.getComment())
                    .build();
            // 保存指标项
            studentItemService.addStudentItem(stuI);
            return ResultVO.success();
        } catch (Exception e) {
            return ResultVO.error(Code.ERROR, "指标项不存在");
        }
    }

    // 上传学生指标项附件
    @PostMapping("studentitems/{stuitemid}/files")
    public ResultVO postStudentFiles(@PathVariable long stuitemid,
                                     @RequestParam("file") MultipartFile uploadFile,//接收前端传递的MultipartFile文件对象
                                     @RequestAttribute(TokenAttribute.UID) long uid,
                                     @RequestAttribute(TokenAttribute.MID) long mid) {
        // 校验指标项状态，已认定的指标项禁止上传
        StudentItem sutFile = studentItemService.getStudentItem(uid, stuitemid);
        if (sutFile.getStatus().equals(StudentItem.Status.CONFIRMED)) {
            throw XException.builder()
                    .codeNum(Code.ERROR)
                    .message("指标项已认定，不可修改")
                    .build();
        }

        // 校验文件名，防路径穿越攻击（过滤文件名中的..///\）
        String fileName = uploadFile.getOriginalFilename();
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            log.warn("恶意上传包含路径的文件,用户ID：{}", uid);
            throw  XException.builder()
                    .codeNum(Code.ERROR)
                    .message("上传文件错误")
                    .build();
        }

        //创建学生专属目录，目录结构：根目录/${学院/类别/专业}/${姓名-学号}
        Path relativePath = fileService.createAndGetRelativePath(mid, uid);
        try (InputStream inputStream = uploadFile.getInputStream()) {
            Path savedPath = fileService.saveFile(relativePath, fileName, inputStream);

            // 保存文件记录
            StudentItemFile sf = StudentItemFile.builder()
                    .studentItemId(stuitemid)
                    .path(savedPath.getParent().toString())
                    .filename(savedPath.getFileName().toString())
                    .build();
            studentItemService.addStudentItemFile(sf);

            // 更新指标项状态
            studentItemService.updateStatus(uid, StudentItem.Status.PENDING_REVIEW);
            return ResultVO.success();
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw XException.builder()
                    .codeNum(Code.ERROR)
                    .message("文件上传失败")
                    .build();
        }
    }

    // 查询学生指标项
    @GetMapping("studentitems/{rootitemid}")
    public ResultVO getStudentItems(@PathVariable long rootitemid,
                                    @RequestAttribute(TokenAttribute.UID) long uid) {
        List<?> items = studentItemService.listStudentItems(uid, rootitemid);
        return ResultVO.success(items);
    }

    // 文件下载
    @GetMapping("studentitems/files/{fileid}")
    public void download(@PathVariable long fileid,
                         @RequestAttribute(TokenAttribute.UID) long uid,
                         HttpServletResponse response) throws IOException {
        // 获取文件路径和大小
        Path filePath = studentItemService.getFilePath(uid, fileid);
        long fileSize = fileService.getSize(filePath);

        // 设置响应头
        String fileName = URLEncoder.encode(filePath.getFileName().toString(), StandardCharsets.UTF_8);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("filename", fileName);
        response.setContentLengthLong(fileSize);

        // 写入文件流
        try (InputStream is = fileService.downloadFile(filePath);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
        }
    }

    // 删除文件
    @DeleteMapping("studentitems/files/{fileid}")
    public ResultVO deleteFiles(@PathVariable long fileid,
                                @RequestAttribute(TokenAttribute.UID) long uid) {
        // 获取文件路径并删除物理文件
        Path filePath = studentItemService.getFilePath(uid, fileid);
        boolean removed = fileService.removeFile(filePath);

        // 删除数据库记录
        if (removed) {
            studentItemService.removeStudentItemFile(fileid);
            return ResultVO.success();
        } else {
            return ResultVO.error(Code.ERROR, "文件移除失败");
        }
    }

    // 删除学生指标项（含附件）
    @DeleteMapping("studentitems/{stuitemid}")
    public ResultVO deleteStudentItems(@PathVariable long stuitemid,
                                       @RequestAttribute(TokenAttribute.UID) long uid) {
        // 1. 删除指标项
        StudentItem studentItem = studentItemService.getStudentItem(uid, stuitemid);
        studentItemService.removeStudentItem(stuitemid);

        // 2. 删除附件文件和数据库记录
        List<StudentItemFile> files = studentItemService.listStudentItemFiles(stuitemid);
        for (StudentItemFile file : files) {
            Path filePath = Path.of(file.getPath(), file.getFilename());
            fileService.removeFile(filePath);
        }
        studentItemService.removeStudentItemFiles(stuitemid);

        return ResultVO.success();
    }

    // 更新学生指标项
    @PatchMapping("studentitems/{stuitemid}")
    public ResultVO updateStudentItems(@PathVariable long stuitemid,
                                       @RequestBody StudentItem studentItem,
                                       @RequestAttribute(TokenAttribute.UID) long uid) {
        studentItemService.updateStudentItem(uid, stuitemid, studentItem.getName(), studentItem.getComment(), StudentItem.Status.PENDING_REVIEW);
        return ResultVO.success();
    }

    // 查询学生指标状态
    @GetMapping("statuses")
    public ResultVO getStatuses(@RequestAttribute(TokenAttribute.UID) long uid) {
        try {
            Object status = studentItemService.getStudentItemsInfo(uid);
            return ResultVO.success(status);
        } catch (Exception e) {
            return ResultVO.success(Map.of());
        }
    }

    // 查询学生指标审核日志
    @GetMapping("logs/{stuitemid}")
    public ResultVO getStudentItemLogs(@PathVariable long stuitemid,
                                       @RequestAttribute(TokenAttribute.UID) long uid) {
        List<?> logs = studentItemService.listStudentItemLogs(uid, stuitemid);
        return ResultVO.success(logs);
    }

    // 查询类别权重
    @GetMapping("category")
    public ResultVO getCategoryWeighting(@RequestAttribute(TokenAttribute.MCID) long mcid) {
        Object majorCategory = majorCategoryService.getMajorCategory(mcid);
        return ResultVO.success(majorCategory);
    }
}
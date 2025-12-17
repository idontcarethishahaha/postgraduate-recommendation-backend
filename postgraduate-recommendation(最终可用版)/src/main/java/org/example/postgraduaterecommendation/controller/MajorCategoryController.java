package org.example.postgraduaterecommendation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.postgraduaterecommendation.dox.*;
import org.example.postgraduaterecommendation.dto.ConfirmWeightedScoreReq;
import org.example.postgraduaterecommendation.dto.StudentItemReq;
import org.example.postgraduaterecommendation.exception.Code;
import org.example.postgraduaterecommendation.exception.XException;
import org.example.postgraduaterecommendation.repository.StudentItemFileRepository;
import org.example.postgraduaterecommendation.service.*;
import org.example.postgraduaterecommendation.vo.ResultVO;
import org.example.postgraduaterecommendation.vo.TokenAttribute;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.File;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * @author wuwenjin
 * 辅导员
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/college/")
public class MajorCategoryController {
    private final MajorCategoryService majorCategoryService;
    private final CollegeService collegeService;
    private final ItemService itemService;
    private final StudentItemService studentItemService;
    private final WeightedScoreService weightedScoreService;
    private final FileService fileService;
    //private final UserService userService;

    //查询分类列表
    @GetMapping("categories")
    public ResultVO getCategories(
            @RequestAttribute(TokenAttribute.UID) long uid,
            @RequestAttribute(TokenAttribute.ROLE) String role,
            @RequestAttribute(TokenAttribute.CID) long cid) {
        if (role.equals(User.COLLEGE_ADMIN)) {
            return ResultVO.success(majorCategoryService.listMajorCategoriesByCollegeId(cid));
        }
        return ResultVO.success(majorCategoryService.listMajorCategoriesByUserId(uid));
    }

    //查询分类下的专业列表
    @GetMapping("categories/{mcid}/majors")
    public ResultVO getMajors(@PathVariable long mcid,
                              @RequestAttribute(TokenAttribute.UID) long uid) {
        // 同步校验权限（校验失败抛异常），顺序执行查询逻辑
        majorCategoryService.checkInMajorCategory(uid, mcid);
        return ResultVO.success(majorCategoryService.listMajors(mcid));
    }

    //加载指定专业下所有学生提交状态
    @GetMapping("majors/{mid}/students/statuses")
    public ResultVO getUsers(@PathVariable long mid) {
        return ResultVO.success(studentItemService.listStudentsInfos(mid));
    }

    //加载指定类别下所有指标项
    @GetMapping("categories/{mcid}/items")
    public ResultVO getItems(@PathVariable long mcid) {
        return ResultVO.success(itemService.listItems(mcid));
    }

    //查询学生加权分
    @GetMapping("students/{sid}/weightedscore")
    public ResultVO getStudentWeightScore(
            @PathVariable long sid,
            @RequestAttribute(TokenAttribute.UID) long uid) {
        try {
            // 校验权限
            studentItemService.checkUserCategoryAuth(sid, uid);
            // 查询加权分，无数据返回null
            Object weightedScore = weightedScoreService.getWeightedScore(sid);
            return ResultVO.success(weightedScore);
        } catch (Exception e) {
            log.warn("查询学生{}加权分失败：{}", sid, e.getMessage());
            return ResultVO.success();
        }
    }

    //提交学生加权分
    @PostMapping("students/{sid}/weightedscore")
    public ResultVO postWeightedScore(
            @RequestBody ConfirmWeightedScoreReq req,
            @PathVariable long sid,
            @RequestAttribute(TokenAttribute.UID) long uid) {
        req.getLog().setUserId(uid);
        // 同步校验权限 + 执行更新，顺序执行
        studentItemService.checkUserCategoryAuth(sid, uid);
        weightedScoreService.updateWeightedScore(
                sid,
                req.getWeightedScore().getScore(),
                req.getWeightedScore().getRanking(),
                WeightedScore.VERIFIED,
                req.getLog()
        );
        return ResultVO.success();
    }

    //查询学生指标项
    @GetMapping("students/{sid}/studentitems")
    public ResultVO getStudentItems(
            @PathVariable long sid,
            @RequestAttribute(TokenAttribute.UID) long uid) {
        studentItemService.checkUserCategoryAuth(sid, uid);
        return ResultVO.success(studentItemService.getStudentItemDTOs(sid));
    }

    //提交学生指标项
    @PostMapping("students/{sid}/studentitems")
    public ResultVO postStudentItems(
            @RequestBody StudentItemReq req,
            @PathVariable long sid,
            @RequestAttribute(TokenAttribute.UID) long uid) {
        req.getLog().setUserId(uid);
        studentItemService.checkUserCategoryAuth(sid, uid);
        studentItemService.updteStudentItem(req.getStudentItem(), req.getLog());
        return ResultVO.success();
    }
    //=======================================
    // 文件下载
//    @GetMapping("studentitems/files/{fileid}")
//    public ResponseEntity<Resource> download(@PathVariable long fileid) throws Exception {
//        // 用 Path 接收（studentItemService.getFilePath 返回 Path）
//        Path filePath = studentItemService.getFilePath(fileid);
//        // getSize 入参为 Path
//        long fileSize = fileService.getSize(filePath);
//
//        // Path 获取文件名用 getFileName()，并转字符串
//        String fileName = URLEncoder.encode(
//                filePath.getFileName().toString(),
//                StandardCharsets.UTF_8
//        );
//
//        // 构建下载响应头
//        HttpHeaders headers = new HttpHeaders();
//        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
//        headers.set("filename", fileName);
//        headers.setContentLength(fileSize);
//
//        // FileSystemResource 支持 Path 类型
//        Resource resource = new FileSystemResource(filePath);
//        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
//    }
//
     //===================================
//    @GetMapping("studentitems/files/{fileid}")
//    public ResponseEntity<Resource> download(@PathVariable long fileid) throws Exception {
//        // 用 Path 接收（studentItemService.getFilePath 返回 Path）
//        Path filePath = studentItemService.getFilePath(fileid);
//        // getSize 入参为 Path
//        //long fileSize = fileService.getSize(filePath);
//        long fileSize = Files.size(filePath);
//
//        // Path 获取文件名用 getFileName()，并转字符串
//        String fileName = URLEncoder.encode(
//                filePath.getFileName().toString(),
//                StandardCharsets.UTF_8
//        );
//
//        // 构建下载响应头
//        HttpHeaders headers = new HttpHeaders();
//        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
//        headers.set("filename", fileName);
//        headers.setContentLength(fileSize);
//
//        // FileSystemResource 支持 Path 类型
//        Resource resource = new FileSystemResource(filePath);
//        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
//    }
// ================
    // new test
@GetMapping("studentitems/files/{fileid}")
public ResponseEntity<Resource> download(@PathVariable long fileid) throws Exception {
    // 1. 获取完整绝对路径（已拼接根目录）
    Path filePath = studentItemService.getFilePath(fileid);

    // 2. 用 FileService 校验并获取文件大小（复用已有逻辑）
    long fileSize = fileService.getSize(filePath);

    // 3. 处理文件名编码
    String fileName = URLEncoder.encode(
            filePath.getFileName().toString(),
            StandardCharsets.UTF_8
    );

    // 4. 构建响应头
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
    headers.set("filename", fileName);
    headers.setContentLength(fileSize);

    // 5. 返回文件资源
    Resource resource = new FileSystemResource(filePath);
    return new ResponseEntity<>(resource, headers, HttpStatus.OK);
}
}

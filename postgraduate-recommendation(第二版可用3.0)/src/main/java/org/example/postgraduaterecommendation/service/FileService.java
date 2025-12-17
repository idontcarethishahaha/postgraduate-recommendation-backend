package org.example.postgraduaterecommendation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {
    @Value("${my.upload}")
    private String rootDirectory;

    private final StudentItemService studentItemService;

    //创建并获取相对路径：`学院/类别/专业/姓名-学号`
    public Path createAndGetRelativePath(long mid, long uid) {
        String majorD = studentItemService.getMajorDirectoryName(mid);
        String userD = studentItemService.getUserFileDirectoryName(uid);

        // 同步创建目录（mkdirs 确保多级目录创建）
        Path fullPath = Paths.get(rootDirectory, majorD, userD);
        try {
            Files.createDirectories(fullPath);
        } catch (IOException e) {
            log.error("创建目录失败：{}", fullPath, e);
            throw new RuntimeException("创建目录失败", e);
        }
        // 返回相对路径
        return Paths.get(majorD, userD);
    }

    //保存文件（添加时间戳避免重名）
    public Path saveFile(Path relativePath, String fileName, InputStream content) {
        // 处理文件名：添加时间戳
        int index = fileName.lastIndexOf(".");
        String name = fileName.substring(0, index) + "-" + System.currentTimeMillis() + fileName.substring(index);
        Path fileRelativePath = relativePath.resolve(name);
        Path fullPath = Paths.get(rootDirectory).resolve(fileRelativePath);

        // 同步写入文件
        try (OutputStream os = new FileOutputStream(fullPath.toFile());
             BufferedInputStream bis = new BufferedInputStream(content);
             BufferedOutputStream bos = new BufferedOutputStream(os)) {

            byte[] buffer = new byte[8192]; // 8KB 缓冲区
            int len;
            while ((len = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            bos.flush();
        } catch (IOException e) {
            log.error("保存文件失败：{}", fullPath, e);
            throw new RuntimeException("保存文件失败", e);
        }
        return fileRelativePath;
    }

    //删除文件
    public boolean removeFile(Path filePath) {
        Path fullPath = Paths.get(rootDirectory).resolve(filePath);
        try {
            return Files.deleteIfExists(fullPath);
        } catch (IOException e) {
            log.error("删除文件失败：{}", fullPath, e);
            throw new RuntimeException("删除文件失败", e);
        }
    }

    //下载文件（返回文件输入流）
    public InputStream downloadFile(Path filePath) {
        Path fullPath = Paths.get(rootDirectory).resolve(filePath);
        try {
            return new BufferedInputStream(new FileInputStream(fullPath.toFile()));
        } catch (FileNotFoundException e) {
            log.error("文件不存在：{}", fullPath, e);
            throw new RuntimeException("文件不存在", e);
        }
    }

    //获取文件大小
//    public long getSize(Path filePath) {
//        Path fullPath = Paths.get(rootDirectory).resolve(filePath);
//        try {
//            return Files.size(fullPath);
//        } catch (IOException e) {
//            log.error("获取文件大小失败：{}", fullPath, e);
//            throw new RuntimeException("获取文件大小失败", e);
//        }
//    }
    //
    public long getSize(Path filePath) {

        // 1. 统一路径处理：避免重复拼接根目录
        Path fullPath;
        if (filePath.isAbsolute()) {
            // 如果传入的是绝对路径，直接使用
            fullPath = filePath;
        } else {
            // 如果是相对路径，拼接根目录
            fullPath = Paths.get(rootDirectory).resolve(filePath).normalize();
        }
       // log.info("获取文件大小，最终路径：{}", fullPath);

        // 2. 前置校验：文件是否存在
        if (!Files.exists(fullPath)) {
            String errorMsg = String.format("文件不存在：%s", fullPath);
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        // 3. 前置校验：是否是文件（不是文件夹）
        if (!Files.isRegularFile(fullPath)) {
            String errorMsg = String.format("路径不是文件：%s", fullPath);
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        // 4. 尝试获取文件大小，精准捕获异常
        try {
            return Files.size(fullPath);
        } catch (AccessDeniedException e) {
            // 权限不足
            String errorMsg = String.format("无读取权限：%s", fullPath);
            log.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (IOException e) {
            // 其他IO异常（如文件损坏、路径非法）
            String errorMsg = String.format("获取文件大小失败：%s，原因：%s", fullPath, e.getMessage());
            log.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }
}

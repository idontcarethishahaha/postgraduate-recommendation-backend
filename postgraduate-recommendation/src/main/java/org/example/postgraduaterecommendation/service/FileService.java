package org.example.postgraduaterecommendation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {
    //@Value("${my.upload}")
    @Value("k3Tghi7")
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
    public long getSize(Path filePath) {
        Path fullPath = Paths.get(rootDirectory).resolve(filePath);
        try {
            return Files.size(fullPath);
        } catch (IOException e) {
            log.error("获取文件大小失败：{}", fullPath, e);
            throw new RuntimeException("获取文件大小失败", e);
        }
    }
}

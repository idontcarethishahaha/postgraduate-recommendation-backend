package org.example.postgraduaterecommendation.service;

import lombok.extern.slf4j.Slf4j;
import org.example.postgraduaterecommendation.util.FileDirUtil;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class FileService {
    @Value("${my.upload}")//根目录配置 application.yml
    private String rootDirectory;

    private final FileDirUtil fileDirUtil;

    //保存文件（添加时间戳避免重名）
    public Path saveFile(Path relativePath, String fileName, InputStream content) {
        // 处理文件名：添加时间戳
        int index = fileName.lastIndexOf(".");//查找最后一个点号的位置（获取文件扩展名）
        //fileName.substring(0, index)获取文件名主体（不含扩展名）
        //System.currentTimeMillis(): 获取当前时间戳（毫秒级）
        String name = fileName.substring(0, index) + "-" + System.currentTimeMillis() + fileName.substring(index);
        //拼接路径
        Path fileRelativePath = relativePath.resolve(name);//相对路径
        Path fullPath = Paths.get(rootDirectory).resolve(fileRelativePath);//绝对路径：根目录 + 相对路径

        /*
        三层流包装：
        FileOutputStream：基础文件输出流
        BufferedOutputStream：缓冲输出流（提高写入性能）
        BufferedInputStream：缓冲输入流（提高读取性能）
         */
        // 流式写入文件
        try (OutputStream os = new FileOutputStream(fullPath.toFile());
             BufferedInputStream bis = new BufferedInputStream(content);
             BufferedOutputStream bos = new BufferedOutputStream(os)) {

            byte[] buffer = new byte[8192]; // 8KB 缓冲区
            int len;
            //循环读取，写入
            while ((len = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            bos.flush();//确保所有数据写入磁盘
        } catch (IOException e) {
            log.error("保存文件失败：{}", fullPath, e);
            throw new RuntimeException("保存文件失败", e);
        }
        return fileRelativePath;// 返回文件相对路径（用于数据库存储）
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

    //将磁盘上的文件转换为 InputStream（输入流）
    //下载文件
    public InputStream downloadFile(Path filePath) {//filePath 文件的相对路径
        Path fullPath = Paths.get(rootDirectory).resolve(filePath);
        try {
            // 打开文件输入流
            // fullPath.toFile()将 Path 转换为 File 对象
            // FileInputStream,基础文件输入流，直接读取文件字节
            // BufferedInputStream,包装为缓冲流（8KB 默认缓冲区），减少磁盘 IO 次数，提升读取效率
            return new BufferedInputStream(new FileInputStream(fullPath.toFile()));
        } catch (FileNotFoundException e) {
            log.error("文件不存在：{}", fullPath, e);
            throw new RuntimeException("文件不存在", e);
        }
    }

    //获取文件大小
    public long getSize(Path filePath) {
        Path fullPath;
        if (filePath.isAbsolute()) {
            fullPath = filePath;
        } else {
            fullPath = Paths.get(rootDirectory).resolve(filePath).normalize();
        }
        if (!Files.exists(fullPath)) {
            String errorMsg = String.format("文件不存在：%s", fullPath);
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        if (!Files.isRegularFile(fullPath)) {
            String errorMsg = String.format("路径不是文件：%s", fullPath);
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        try {
            return Files.size(fullPath);
        } catch (AccessDeniedException e) {
            String errorMsg = String.format("无读取权限：%s", fullPath);
            log.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (IOException e) {
            String errorMsg = String.format("获取文件大小失败：%s，原因：%s", fullPath, e.getMessage());
            log.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }
    //===========================

    // 创建并获取相对路径：`学院/类别/专业/姓名-学号`
    public Path createAndGetRelativePath(long mid, long uid) {
        String majorD = fileDirUtil.getMajorDirectoryName(mid);//从专业ID获取 学院/类别/专业 目录名
        String userD = fileDirUtil.getUserFileDirectoryName(uid);// 从用户ID获取 姓名-学号 目录名

        //绝对路径 = 根目录 + 相对路径
        Path fullPath = Paths.get(rootDirectory, majorD, userD);
        try {
            Files.createDirectories(fullPath);// 递归创建目录（不存在则创建）
        } catch (IOException e) {
            log.error("创建目录失败：{}", fullPath, e);
            throw new RuntimeException("创建目录失败", e);
        }
        return Paths.get(majorD, userD);// 返回相对路径（不含根目录）
    }

    // 手动构造方法
    public FileService(FileDirUtil fileDirUtil) {
        this.fileDirUtil = fileDirUtil;
    }
}

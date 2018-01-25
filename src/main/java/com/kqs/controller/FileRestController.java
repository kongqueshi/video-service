package com.kqs.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 2018/1/24.
 */
@RestController
public class FileRestController {
    @GetMapping("/api/files")
    public String listAllFiles() {
        List<String> fileList = new ArrayList<String>();
        File file = new File("/Volumes/UNTITLED/other");
        for (File f : file.listFiles()) {
            if (f.getName().toLowerCase().endsWith("mp4") && !f.getName().startsWith(".")) {
                fileList.add(f.getName());
            }
        }

        return JSON.toJSONString(fileList);
    }

    @GetMapping("/api/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename, HttpServletRequest request, HttpServletResponse response) throws IOException {
        File file = new File("/Volumes/UNTITLED/other/"+ filename);

        ByteArrayResource data;

        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-Type", "video/mp4");
        headers.add("Accept-Ranges", "bytes");
        System.out.println(request.getHeader("range"));
        String range = request.getHeader("range");
        if ( range != null) {
            range = range.replace("bytes=", "");

            Path path = Paths.get(file.getAbsolutePath());
            if (range.split("-")[1].equals("")) {
                byte[] fileBytes = Files.readAllBytes(path);
                data = new ByteArrayResource(fileBytes);
                headers.add("Content-Range", "bytes 0-");
                headers.add("Content-Length", "" + file.length());
            } else {
                int start = Integer.parseInt(range.split("-")[0]);
                int end = Integer.parseInt(range.split("-")[1]);
                int length = end - start + 1;
                byte[] magic = new byte[length];
                RandomAccessFile raf = new RandomAccessFile(file, "r");
                raf.seek(start);
                raf.read(magic, 0, length);
                data = new ByteArrayResource(magic);
                headers.add("Content-Range", String.format("bytes %s-%s/%s", start, end, file.length()));
                headers.add("Content-Length", "" + length);
            }
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(data);
        }

        return null;

    }
}

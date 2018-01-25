package com.kqs.controller;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.IOUtils;
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
import java.nio.channels.Channels;
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
    private final int MAX_BYTES = 1024 * 2;

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
    public void getFile(@PathVariable String filename, HttpServletRequest request, HttpServletResponse response) throws IOException {
        File file = new File("/Volumes/UNTITLED/other/"+ filename);

        response.setHeader("Content-Type", "video/mp4");
        response.setHeader("Accept-Ranges", "bytes");
        System.out.println(request.getHeader("range"));
        String range = request.getHeader("range");
        if ( range != null) {
            range = range.replace("bytes=", "");
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            int start = Integer.parseInt(range.split("-")[0]);
            if (range.split("-").length < 2) {
                response.setHeader("Content-Range", "bytes 0-");
                response.setHeader("Content-Length", "" + file.length());
                write(response, raf, start, file.length() - 1, true);
            } else {
                int end = Integer.parseInt(range.split("-")[1]);
                int length = end - start + 1;
                response.setHeader("Content-Range", String.format("bytes %s-%s/%s", start, end, file.length()));
                response.setHeader("Content-Length", "" + length);
                write(response, raf, start, length, false);
            }
        }

    }

    private void write(HttpServletResponse response, RandomAccessFile raf, long start, long length, boolean all) throws IOException {
        InputStream is = Channels.newInputStream(raf.getChannel());
        if (all) {
            IOUtils.copy(is, response.getOutputStream());
        } else {
            IOUtils.copyLarge(is, response.getOutputStream(), start, length);
        }
    }
}

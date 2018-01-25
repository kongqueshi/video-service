package com.kqs.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 2018/1/24.
 */
@Controller
public class IndexController {
    @GetMapping("/")
    public String index(ModelMap map) {
        map.addAttribute("files", listAllFiles());
        return "index";
    }

    @GetMapping("/video/{filename:.+}")
    public String video(@PathVariable String filename, ModelMap map) {
        map.addAttribute("filename", filename + ".mp4");
        return "video";
    }

    public List<String> listAllFiles() {
        List<String> fileList = new ArrayList<String>();
        File file = new File("/Volumes/UNTITLED/other");
        for (File f : file.listFiles()) {
            if (f.getName().toLowerCase().endsWith(".mp4") && !f.getName().startsWith(".")) {
                fileList.add(f.getName().replace(".mp4", ""));
            }
        }
        return fileList;
    }
}

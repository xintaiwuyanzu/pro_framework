package com.dr.process.activiti.model.controller;

import com.dr.process.activiti.model.entity.Project;
import com.dr.process.activiti.model.utils.Constants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 *
 */
//@RestController
@RequestMapping("${common.api-path:/api}" + Constants.API_VERSION)
public class ProjectController {
    @GetMapping("projects")
    public List<Project> projects() {
        return Collections.emptyList();
    }

}

package com.wayn.quartz.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.wayn.commom.base.BaseControlller;
import com.wayn.commom.util.ParameterUtil;
import com.wayn.commom.util.Response;
import com.wayn.quartz.domain.JobLog;
import com.wayn.quartz.service.JobLogService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequestMapping("/quartz/jobLog")
@Controller
public class JobLogController extends BaseControlller {

    private static final String PREFIX = "quartz/jobLog";

    @Autowired
    private JobLogService jobLogService;

    @RequiresPermissions("quartz:jobLog:list")
    @GetMapping
    public String JobLogIndex() {
        return PREFIX + "/jobLog";
    }

    @RequiresPermissions("quartz:jobLog:list")
    @ResponseBody
    @PostMapping("/list")
    public Page<JobLog> list(Model model, JobLog jobLog) {
        Page<JobLog> page = getPage();
        ParameterUtil.set(jobLog);
        return jobLogService.selectJobLogList(page, jobLog);
    }

    @RequiresPermissions("quartz:jobLog:remove")
    @ResponseBody
    @DeleteMapping("/remove/{id}")
    public Response remove(ModelMap modelMap, @PathVariable("id") Long id) {
        jobLogService.remove(id);
        return Response.success("删除成功");
    }

    @RequiresPermissions("quartz:jobLog:remove")
    @ResponseBody
    @PostMapping("/batchRemove")
    public Response batchRemove(ModelMap modelMap, @RequestParam("ids[]") Long[] ids) {
        jobLogService.batchRemove(ids);
        return Response.success("删除成功");
    }

    @PostMapping("/export")
    public void list(JobLog jobLog, HttpServletRequest request, HttpServletResponse response) throws IOException {
        ParameterUtil.set(jobLog);
        jobLogService.export(jobLog, response, request);
    }

}

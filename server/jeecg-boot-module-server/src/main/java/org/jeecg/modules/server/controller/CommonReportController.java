package org.jeecg.modules.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.modules.server.service.ICommonReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.jeecg.common.api.vo.Result;

import java.util.Map;

@Api(tags = "通用服务接口")
@RestController
@RequestMapping("/common")
public class CommonReportController {
    @Autowired
    ICommonReportService commonService;

    @ApiOperation(value="执行sql", notes="参数格式示例：{sql_name: '%Y-%m-%d', xxx: 'xxxx' //xxx为sql中的参数名称，sql中的参数要写成 #{xxx}}")
    @PostMapping("/executeSql")
    public Result<?> executeSql(@RequestBody Map<String, String> map) {
        return Result.OK(commonService.executeSql(map));
    }
}

package org.jeecg.modules.server.controller;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.modules.server.service.ICommonReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.jeecg.common.api.vo.Result;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    @PostMapping("/saveLayoutFile")
    public Result<?> saveLayoutFile(@RequestBody Map<?, ?> map) throws IOException {
        commonService.createLayoutFile(map.get("layout_str").toString(), map.get("layout_name").toString());
        return Result.OK("success");
    }

    @PostMapping("/saveLayoutInfo")
    public Result<?> saveLayoutInfo(@RequestBody Map<?, ?> map) {
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        Date date = new Date();
        String now = format.format(date);
        commonService.insertLayoutInfo(map, now);
        return Result.OK("success");
    }

    @PostMapping("/getLayout")
    public Result<?> getLayout(@RequestBody Map<?, ?> map) throws IOException {
        String str = commonService.readLayoutFile(map.get("layout_name").toString());
        if(str.equals("Error")){
            return Result.error("文件不存在");
        }
        return Result.OK(str);
    }
}

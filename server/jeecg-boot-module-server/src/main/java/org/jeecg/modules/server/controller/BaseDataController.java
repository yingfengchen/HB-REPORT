package org.jeecg.modules.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.modules.server.service.IEnergyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Api(tags="基础数据管理")
@RestController
@RequestMapping("/base")
public class BaseDataController {
    @Autowired
    IEnergyService service;

    @ApiOperation(value="获取所有Tag数据", notes="参数格式示例：{timeUnit: '%Y-%m-%d'}")
    @PostMapping(value = "/getTagList")
    List<Map> getTagList(@RequestBody Map map) {
        return service.getTagList(map);
    }

    @ApiOperation(value="批量插入Tag", notes="批量插入Tag")
    @PostMapping(value = "/insertTagDefBunch")
    void insertTagDefBunch(@RequestBody List<Map> maps) {
        maps.forEach(map -> {
            service.insertTagDefBunch(map);
        });
    }

    @ApiOperation(value="批量删除Tag", notes="批量删除Tag")
    @PostMapping(value = "/deleteTags")
    void deleteTags(@RequestBody List<Map> maps) {
        maps.forEach(map -> {
            service.deleteTag(map);
        });
    }
}

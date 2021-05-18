package org.jeecg.modules.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.jeecg.modules.server.service.IEnergyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags="能源分析")
@RestController
@RequestMapping("/energy")
public class EnergyController {
    @Autowired
    IEnergyService service;

    @ApiOperation(value="查询累计值趋势", notes="参数格式示例：{timeUnit: '%Y-%m-%d', tagList: 'xxxx', startTime: '2021-04-01', endTime: '2021-04-01'}")
    @PostMapping(value = "/getCumulativeTrend")
    List<Map> getWaterUse(@RequestBody Map map) {
        return service.getTrendOfWaterUse(map);
    }

    @ApiOperation(value="获取tag阶段性消耗量", notes="参数格式示例：{tagList: 'xxxx', startTime: '2021-04-01', endTime: '2021-04-01'}")
    @PostMapping(value = "/getRangeEnergyConsume")
    List<Map> getRangeEnergyConsume(@RequestBody Map map) {
        return service.getRangeEnergyConsume(map);
    }

    @ApiOperation(value="查询瞬时值趋势", notes="参数格式示例：{tagList: 'xxxx'}")
    @PostMapping(value = "/getInstantaneousValueTrend")
    List<Map> getInstantaneousValueTrend(@RequestBody Map map) {
        return service.getInstantaneousValueTrend(map);
    }

    @ApiOperation(value="查询相应节点当天当月的累计差值", notes="参数格式示例：{tagList: 'xxxx'}")
    @PostMapping(value = "/getDayMonthDiffValueOfTags")
    List<Map> getDayMonthDiffValueOfTags(@RequestBody Map map) {
        return service.getDayMonthDiffValueOfTags(map);
    }

    @ApiOperation(value="查询相应节点当天当月的瞬时值", notes="参数格式示例：{tagList: 'xxxx'}")
    @PostMapping(value = "/getDayMonthValueOfTags")
    List<Map> getDayMonthValueOfTags(@RequestBody Map map) {
        return service.getDayMonthValueOfTags(map);
    }

    @ApiOperation(value="获取一个节点24小时的累计值差值趋势数据", notes="参数格式示例：{tag: 'xxxx', diff_flag: true}")
    @PostMapping(value = "/getTrendOfOneTag24Hour")
    List<Map> getTrendOfOneTag24Hour(@RequestBody Map map) {
        return service.getTrendOfOneTag24Hour(map);
    }

    @ApiOperation(value="获取一个节点近两天的累计值差值", notes="参数格式示例：{tag: 'xxxx'")
    @PostMapping(value = "/getTwoDayDiffRateOfOnTag")
    List<Map> getTwoDayDiffRateOfOnTag(@RequestBody Map map) {
        return service.getTwoDayDiffRateOfOnTag(map);
    }
}

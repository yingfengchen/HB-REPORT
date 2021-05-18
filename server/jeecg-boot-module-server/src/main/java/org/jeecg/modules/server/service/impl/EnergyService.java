package org.jeecg.modules.server.service.impl;

import org.jeecg.modules.server.mapper.EnergyMapper;
import org.jeecg.modules.server.service.IEnergyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EnergyService implements IEnergyService {
    @Autowired
    EnergyMapper mapper;

    @Override
    public List<Map> getTrendOfWaterUse(Map map) {
        return mapper.getTrendOfWaterUse(map);
    }

    @Override
    public List<Map> getInstantaneousValueTrend(Map map) {
        return mapper.getInstantaneousValueTrend(map);
    }

    @Override
    public List<Map> getDayMonthDiffValueOfTags(Map map) {
        return mapper.getDayMonthDiffValueOfTags(map);
    }

    @Override
    public List<Map> getDayMonthValueOfTags(Map map) {
        return mapper.getDayMonthValueOfTags(map);
    }

    @Override
    public List<Map> getTrendOfOneTag24Hour(Map map) {
        return mapper.getTrendOfOneTag24Hour(map);
    }

    @Override
    public List<Map> getTwoDayDiffRateOfOnTag(Map map) {
        return mapper.getTwoDayDiffRateOfOnTag(map);
    }

    @Override
    public List<Map> getRangeEnergyConsume(Map map) {
        return mapper.getRangeEnergyConsume(map);
    }

    @Override
    public List<Map> getTagList(Map map) {
        return mapper.getTagList(map);
    }

    @Override
    public void insertTagDefBunch(Map map) {
        mapper.insertTagDefBunch(map);
    }

    @Override
    public void deleteTag(Map map) {
        mapper.deleteTag(map);
    }
}

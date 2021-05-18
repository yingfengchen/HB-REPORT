package org.jeecg.modules.server.service;

import java.util.List;
import java.util.Map;

public interface IEnergyService {
    List<Map> getTrendOfWaterUse(Map map);

    List<Map> getInstantaneousValueTrend(Map map);

    List<Map> getDayMonthDiffValueOfTags(Map map);

    List<Map> getDayMonthValueOfTags(Map map);

    List<Map> getTrendOfOneTag24Hour(Map map);

    List<Map> getTwoDayDiffRateOfOnTag(Map map);

    List<Map> getRangeEnergyConsume(Map map);

    List<Map> getTagList(Map map);

    void insertTagDefBunch(Map map);

    void deleteTag(Map map);
}

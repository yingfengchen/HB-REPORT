package org.jeecg.modules.server.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@DS("master")
@Mapper
public interface EnergyMapper {
    List<Map> getTrendOfWaterUse(Map map);

    List<Map> getInstantaneousValueTrend(Map map);

    List<Map> getDayMonthDiffValueOfTags(Map map);

    List<Map> getDayMonthValueOfTags(Map map);

    List<Map> getTrendOfOneTag24Hour(Map map);

    List <Map> getTwoDayDiffRateOfOnTag(Map map);

    List<Map> getRangeEnergyConsume(Map map);

    List<Map> getTagList(Map map);

    void insertTagDefBunch(Map map);

    void deleteTag(Map map);
}

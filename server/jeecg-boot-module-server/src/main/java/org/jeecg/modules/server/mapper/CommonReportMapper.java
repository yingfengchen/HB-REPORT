package org.jeecg.modules.server.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface CommonReportMapper {
    @DS("mes")
    List<Map<?, ?>> executeSql(Map<String, String> map);

    @DS("master")
    String getSqlStr(Map<String, String> map);
}

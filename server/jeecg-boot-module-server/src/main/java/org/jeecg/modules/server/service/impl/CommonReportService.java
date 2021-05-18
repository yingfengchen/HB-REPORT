package org.jeecg.modules.server.service.impl;

import org.jeecg.modules.server.mapper.CommonReportMapper;
import org.jeecg.modules.server.service.ICommonReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class CommonReportService implements ICommonReportService {
    @Autowired
    CommonReportMapper commonReportMapper;

    @Override
    public List<Map<?, ?>> executeSql(Map<String, String> map) {
        String sql_name = map.get("sql_name");

        if (sql_name.equals("")) {
            List<Map<?, ?>> result_error = new ArrayList<>();
            Map<String, String> map_error = new HashMap<>();
            map_error.put("error", "sql语句语法有问题，请检查");
            result_error.add(map_error);
            return result_error;
        }

        AtomicReference<String> sqlStr = new AtomicReference<>(commonReportMapper.getSqlStr(map));

        map.forEach((k, v) -> {
            String sql_temp = sqlStr.get().replace("#{" + k + "}", "\"" + v + "\"");
            sqlStr.set(sql_temp);
        });

        map.put("sqlStr", sqlStr.toString());
        return commonReportMapper.executeSql(map);
    }
}

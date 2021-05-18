package org.jeecg.modules.server.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface ICommonReportService {
    List<Map<?, ?>> executeSql(Map<String, String> map);
}

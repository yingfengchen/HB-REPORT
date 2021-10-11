package org.jeecg.modules.server.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public interface ICommonReportService {
    List<Map<?, ?>> executeSql(Map<String, String> map);
    void insertLayoutInfo(Map map, String now);
    void createLayoutFile(String layout_str, String layout_name) throws IOException;
    String readLayoutFile(String layout_name) throws IOException;
}

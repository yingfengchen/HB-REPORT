package org.jeecg.modules.server.service.impl;

import cn.hutool.core.io.resource.ClassPathResource;
import org.jeecg.modules.server.mapper.CommonReportMapper;
import org.jeecg.modules.server.service.ICommonReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class CommonReportService implements ICommonReportService {
    @Autowired
    CommonReportMapper commonReportMapper;

    @Value("${jeecg.path.layout}")
    private String FilePath;

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
            String sql_temp = sqlStr.get().replace("#{" + k + "}", v.equals("")?"NULL":"'" + v + "'");
            sqlStr.set(sql_temp);
        });

        map.put("sqlStr", sqlStr.toString());
        System.out.println("---------------------" + sql_name + "----------------------");
        System.out.println(sqlStr);
        return commonReportMapper.executeSql(map);
    }

    @Override
    public void insertLayoutInfo(Map map, String now) {
        String layout_name = map.get("layout_name").toString();
        ((List<Map>)map.get("items")).forEach(r -> {
            r.forEach((k,v) -> {
                if(v == null){
                    r.put(k, "");
                }
            });
            r.put("belong", layout_name);
            r.put("timekey", now);
            commonReportMapper.insertLayoutItem(r);
        });
    }

    @Override
    public void createLayoutFile(String layout_str, String layout_name) throws IOException {
        byte[] sourceByte = layout_str.getBytes(StandardCharsets.UTF_8);
        File file = new File(FilePath + "/layout_" + layout_name + ".txt");
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(sourceByte);
        outputStream.close();
    }

    @Override
    public String readLayoutFile(String layout_name) throws IOException {
        String s = "";
        File file = new File(FilePath + "/layout_" + layout_name + ".txt");
        if(!file.exists()) {
            return "Error";
        }
        InputStreamReader in = new InputStreamReader(new FileInputStream(file),"UTF-8");
        BufferedReader br = new BufferedReader(in);
        StringBuilder content = new StringBuilder();
        while ((s=br.readLine())!=null){
            content.append(s);
        }
        return content.toString();
    }
}

package com.example.config;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by YangGuanRong
 * date: 2023/1/9
 */
@Slf4j
public class DBUtils {

    /**
     * 初始化数据库
     */
    public static void initDataBase() throws Exception {
        Properties p = loadPropertyFromStream();
        createDatabase(p);
    }

    private static void createDatabase(Properties properties) {

        String driver = properties.getProperty("spring.datasource.driver-class-name");
        String dbUrl = properties.getProperty("spring.datasource.url");
        String username = properties.getProperty("spring.datasource.username");
        String password = properties.getProperty("spring.datasource.password");
        try {
            if (driver != null && dbUrl != null && username != null && password != null) {
                String temp = dbUrl.substring(0, dbUrl.indexOf("?"));
                String dbName = temp.substring(temp.lastIndexOf("/")+1);
                log.info(dbName);
                Class.forName(driver);
                try (Connection conn = DriverManager.getConnection(dbUrl, username, password)) {
                    log.info("{} create connection success...", dbUrl);
                } catch (Exception e1) {
                    log.error("connect db failed.", e1);
                    dbUrl = dbUrl.replace(dbName, "mysql");
                    try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
                         Statement stat = conn.createStatement()) {
                        stat.executeUpdate("create database " + dbName);
                    }
                }
            }
        } catch (Exception e) {
            log.error("create db failed", e);
        }
    }

    /**
     * 加载flyway的配置文件，并返回Properties的配置对象
     * Returns:
     * java.util.Properties 配置对象
     * Throws:
     * IOException
     * @return
     * @throws Exception
     */
    private static Properties loadPropertyFromStream() throws Exception {

        Properties property = new Properties();
        InputStream fi;

        try {
            fi = getFlywayConfigFileAsStream();
            property.load(fi);
        } catch (FileNotFoundException e) {
            log.warn("flyway.conf load failed!");
            throw e;
        } catch (Exception e) {
            log.warn("flyway init failed!");
            throw e;
        }
        return property;
    }

    /**
     * 读取Flyway配置文件 1.持续集成部署环境中，读取api.jar当前平级目录下的flyway.conf配置文件 2.未采用持续集成部署，读取api.jar当前平级目录下config目录下的flyway.conf配置文件 3.开发环境IDE中，读取classpath下的flyway.conf配置文件 (pom文件的build配置会将flyway.conf移动到config下) 按照123的顺序依次加载，加载到即不会再继续往后面加载
     * Returns:
     * java.io.InputStream
     * @return
     * @throws FileNotFoundException
     */
    private static InputStream getFlywayConfigFileAsStream() throws FileNotFoundException {

        ArrayList<File> candidateFileArr = new ArrayList<>();

        //1.在部署环境的当前目录下找
        File fileDeploy = Paths.get("./", "application.properties").toFile();

        //2.在当前的config路径下找
        File fileConfig = Paths.get("./config/", "application.properties").toFile();
        candidateFileArr.add(fileDeploy);
        candidateFileArr.add(fileConfig);

        for (File file : candidateFileArr) {
            if (file.exists() && file.isFile() && file.canRead()) {
                log.warn("load flyway.conf ,the file location is :" + file.getPath());
                try {
                    return new FileInputStream(file);
                } catch (Exception e) {
                    log.warn("load flyway.conf in :" + file.getPath() + " failed!");
                }
            }
        }

        //3.在开发环境的classpath下面找(resources下)
        try {
            return getFileFromClassPath();
        } catch (Exception e) {
            log.warn("classpath found flyway.conf failed!");
        }
        log.warn("flyway config file no found! please check ...");
        throw new FileNotFoundException();
    }


    /**
     * 从classpath下寻找配置文件
     *
     * @return java.io.InputStream 文件输入流对象
     * @throws FileNotFoundException
     */
    private static InputStream getFileFromClassPath() throws FileNotFoundException {

        InputStream in = DBUtils.class.getClassLoader().getResourceAsStream("application.properties");
        if (in != null) {
            log.info("load flyway.conf from classpath success!");
            return in;
        } else {
            throw new FileNotFoundException();
        }
    }

}

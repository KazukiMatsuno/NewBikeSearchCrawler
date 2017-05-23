/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.co.bughouse.batch.common;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author user
 */
public class MyProperties {
    // ロガー宣言

    private static Logger logger = Logger.getLogger(MyProperties.class);

    public static Properties getProperties(String fileName) throws FileNotFoundException, IOException {
        logger.info("START:getProperties" + fileName);
        Properties properties = new Properties();
        InputStream is;
        try {
            String separator = System.getProperty("file.separator");

            is = new BufferedInputStream(new FileInputStream(
                    System.getProperty("user.dir") + separator + "resources" + separator + fileName + ".properties"));
            properties.load(is);
        } catch (FileNotFoundException e) {
            logger.error("ファイルが存在しません");
            throw e;
        } catch (IOException e) {
            logger.error("ファイル読み込みに失敗しました");
            throw e;
        }

        return properties;
    }
}

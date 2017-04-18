package io.github.lvzhouyang.mybatis.web.controller;

import io.github.lvzhouyang.mybatis.web.model.GeneratorParam;
import io.github.lvzhouyang.mybatis.web.utils.GeneratorUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.*;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The type Index controller.
 * 生成页面
 *
 * @author lvzhouyang.
 * @version 1.0
 * @since 2017.04.18
 */
@Controller
public class IndexController {

    public static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    @RequestMapping(value = "gen")
    public void gen(GeneratorParam param, HttpServletRequest request, HttpServletResponse response) {
        try {
            List<String> warnings = new ArrayList<>();
            // 准备 配置文件
            String srcDirName = UUID.randomUUID().toString().replace("-", "");
            param.setBuildPath(srcDirName);
            // 2.获取 配置信息
            Configuration config = new Configuration();

            // 应用配置信息
            this.applyConfig(config, param);

            // 3.创建 默认命令解释调回器
            DefaultShellCallback callback = new DefaultShellCallback(true);
            // 4.创建 mybatis的生成器
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
            // 5.执行，关闭生成器
            myBatisGenerator.generate(null);
            GeneratorUtils.zipFolder(param.getBuildPath(), srcDirName + ".zip");
            String url = srcDirName + ".zip";
            File downloadFile = new File(url);
            ServletContext context = request.getServletContext();

            // get MIME type of the file
            String mimeType = context.getMimeType(url);
            if (mimeType == null) {
                // set to binary type if MIME mapping not found
                mimeType = "application/octet-stream";
                LOGGER.info("context getMimeType is null");
            }
            LOGGER.info("MIME type: " + mimeType);

            // set content attributes for the response
            response.setContentType(mimeType);
            response.setContentLength((int) downloadFile.length());

            // set headers for the response
            String headerKey = "Content-Disposition";
            String headerValue = String.format("attachment; filename=\"%s\"",
                    downloadFile.getName());
            response.setHeader(headerKey, headerValue);

            // Copy the stream to the response's output stream.
            InputStream myStream = new FileInputStream(url);
            IOUtils.copy(myStream, response.getOutputStream());
            response.flushBuffer();

            Executors.newFixedThreadPool(1).submit(() -> {
                try {
                    TimeUnit.SECONDS.sleep(10);
                    GeneratorUtils.deleteDir(new File(url));
                    GeneratorUtils.deleteDir(new File(srcDirName));
                } catch (Exception e) {
                    LOGGER.error("异步删除失败", e);
                }
            });
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    private void applyConfig(Configuration config, GeneratorParam param) {
        File dirFile = new File(param.getBuildPath());
        if (!dirFile.exists()) {
            dirFile.mkdirs();
            new File(param.getBuildPath() + "/src/main/java").mkdirs();
            new File(param.getBuildPath() + "/src/main/resources").mkdirs();
        }
        Context context = new Context(ModelType.FLAT);
        config.addContext(context);

        context.setId("Mysql");
        context.setTargetRuntime("MyBatis3Simple");
        context.addProperty("beginningDelimiter", "`");
        context.addProperty("endingDelimiter", "`");
        CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
        commentGeneratorConfiguration.setConfigurationType("org.mybatis.generator.QnloftCommentGenerator");
        commentGeneratorConfiguration.addProperty("suppressAllComments", "false");
        commentGeneratorConfiguration.addProperty("suppressDate", "true");
        context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);
        // 注释
        CommentGeneratorConfiguration cgc = context.getCommentGeneratorConfiguration();
        cgc.setConfigurationType("io.github.lvzhouyang.mybatis.web.utils.QnloftCommentGenerator");

        // 配置数据库属性
        JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();

        String connection = "jdbc:mysql://" + param.getConnection() + ":" + param.getPort() + "/" + param.getDataBase();
        jdbcConnectionConfiguration.setConnectionURL(connection);
        jdbcConnectionConfiguration.setUserId(param.getUserId());
        jdbcConnectionConfiguration.setPassword(param.getUserPass());
        jdbcConnectionConfiguration.setDriverClass("com.mysql.jdbc.Driver");
        context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);
        //配置模型的包名
        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
        javaModelGeneratorConfiguration.setTargetPackage(param.getModelPath());
        javaModelGeneratorConfiguration.setTargetProject(param.getBuildPath() + "/src/main/java");
        context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);
        //mapper的包名
        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
        javaClientGeneratorConfiguration.setTargetPackage(param.getMapperPath());
        javaClientGeneratorConfiguration.setTargetProject(param.getBuildPath() + "/src/main/java");
        javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
        context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);
        //映射文件的包名
        SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
        sqlMapGeneratorConfiguration.setTargetPackage(param.getMappingPath());
        sqlMapGeneratorConfiguration.setTargetProject(param.getBuildPath() + "/src/main/resources");
        context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);

        TableConfiguration tc = new TableConfiguration(context);
        tc.setTableName("%");
        tc.setAllColumnDelimitingEnabled(true);
        GeneratedKey generatedKey = new GeneratedKey("id", "Mysql", true, "");
        tc.setGeneratedKey(generatedKey);

        // 插件
        PluginConfiguration sp = new PluginConfiguration();
        sp.setConfigurationType("org.mybatis.generator.plugins.SerializablePlugin");
        context.addPluginConfiguration(sp);

        PluginConfiguration abp = new PluginConfiguration();
        abp.setConfigurationType("io.github.lvzhouyang.mybatis.web.plugins.AddAliasToBaseColumnListPlugin");
        context.addPluginConfiguration(abp);

        if (!StringUtils.isEmpty(param.getMapperPlugin())) {
            PluginConfiguration pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("io.github.lvzhouyang.mybatis.web.plugins.MapperPlugin");
            pluginConfiguration.addProperty("mappers", "com.lvzhouyang.base.BaseMapper");
            context.addPluginConfiguration(pluginConfiguration);
        } else {
            tc.setSelectByExampleStatementEnabled(true);
            tc.setDeleteByPrimaryKeyStatementEnabled(true);
            tc.setUpdateByExampleStatementEnabled(true);
            tc.setCountByExampleStatementEnabled(true);
        }

        context.getTableConfigurations().clear();
        context.getTableConfigurations().add(tc);

        if (param.getTableNames() != null && 0 < param.getTableNames().length) {
            //表集合
            List<TableConfiguration> tableConfigurations = context.getTableConfigurations();
            tableConfigurations.clear();
            for (int i = 0; i < param.getTableNames().length; i++) {
                if (!StringUtils.isEmpty(param.getTableNames()[i]) && !StringUtils.isEmpty(param.getModelNames()[i])) {
                    TableConfiguration tableConfiguration = new TableConfiguration(context);
                    tableConfiguration.setTableName(param.getTableNames()[i]);
                    tableConfiguration.setDomainObjectName(param.getModelNames()[i]);
                    tableConfiguration.setCountByExampleStatementEnabled(true);
                    tableConfiguration.setDeleteByExampleStatementEnabled(true);
                    tableConfiguration.setSelectByExampleStatementEnabled(true);
                    tableConfiguration.setUpdateByExampleStatementEnabled(true);
                    //模型是否驼峰命名，为0则为驼峰
                    if (param.getIsHump().equals("0")) {
                        tableConfiguration.getProperties().setProperty("useActualColumnNames", "true");
                    }
                    tableConfigurations.add(tableConfiguration);
                }
            }
        }
    }

}

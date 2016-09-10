package info.yywang.micro.sms.utils;

import com.iydsj.sw.common.utils.StringUtils;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xiuyuhang on 16/8/4.
 */
public class AnalysisUtils {

    final static Logger logger = LoggerFactory.getLogger(AnalysisUtils.class);

    private static final String TEMPLATE_NAME = "message";

    private static final String DEFAULT_CHARSET = "UTF-8";

    public static String analysis(final String template, Map<String, String> params, String charset) {
        Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate(TEMPLATE_NAME, template);

        cfg.setTemplateLoader(stringTemplateLoader);
        if (StringUtils.isBlank(charset)) {
            cfg.setDefaultEncoding(DEFAULT_CHARSET);
        } else {
            cfg.setDefaultEncoding(charset);
        }

        try {
            Template freemakerTempl = cfg.getTemplate(TEMPLATE_NAME);

            StringWriter writer = new StringWriter();
            freemakerTempl.process(params, writer);

            return writer.toString();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } catch (TemplateException e) {
            logger.error(e.getMessage(), e);
        }
        return "";
    }

    public static String analysis(final String template, Map<String, String> params) {
        return analysis(template, params, DEFAULT_CHARSET);
    }

    public static List<String> analysisAndGetParams(String template, Map<String, String> params) {

        Map<Integer, String> key = new TreeMap<>();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            Pattern pattern = Pattern.compile("\\$\\{(" + entry.getKey() + ")\\}");
            Matcher matcher = pattern.matcher(template);

            while (matcher.find()) {
                key.put(matcher.start(), entry.getKey());
            }
        }

        List<String> finalParams = new ArrayList<String>();

        for (Map.Entry<Integer, String> entry : key.entrySet()) {
            finalParams.add(params.get(entry.getValue()));
        }

        return finalParams;
    }
}

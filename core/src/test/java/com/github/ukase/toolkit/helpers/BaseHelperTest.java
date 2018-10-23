package com.github.ukase.toolkit.helpers;

import com.github.ukase.service.HtmlRenderer;
import com.github.ukase.toolkit.upload.UploadedTemplateLoader;
import com.github.ukase.web.UkasePayload;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;

/**
 * Test is used to test concreate helper
 *
 * !!! Requires property ukase.enabled-sources.upload = true
 */
@Slf4j
@Data
public class BaseHelperTest {
    public final static String DATA_CONTAINER_KEY = "data";

    @Autowired
    private UploadedTemplateLoader templatesLoader;

    @Autowired
    private HtmlRenderer htmlRenderer;

    /**
     *
     * @param bodyContent - content into the <body></body> tag. Usecase {{helper param1 param2}}
     * @param data - data to generate html
     * @param resultContains - expected result into the generated html
     * @return - generated html
     * @throws IOException
     */
    public String test(String bodyContent, Map<String, Object> data, String resultContains) throws IOException {
        final String htmlTemplate =
                        "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n" +
                        "        \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" +
                        "<head>\n" +
                        "    <meta content=\"text/html\" charset=\"utf-8\"/>\n" +
                        "</head>" +
                        "<html><body>" +
                        "{0}" +
                        "</body></html>";
        log.debug("data: {}, bodyContent: {}", data, bodyContent);
        String templateStr = MessageFormat.format( htmlTemplate, bodyContent );
        byte[] template = templateStr.getBytes();

        String templateName = loadTemplate( templateStr );
        UkasePayload payload = new UkasePayload();
        payload.setBinary(template);
        payload.setData(data);
        payload.setIndex(templateName);

        String generatedHtml = htmlRenderer.render(payload);
        log.debug("Generated html: {}", generatedHtml);
        if( resultContains != null ) {
            Assert.assertTrue("Generated html (" + generatedHtml + ") doesn't contain expectedResult: " +
                    resultContains, generatedHtml.contains(resultContains));
        }
        return generatedHtml;
    }

    private String loadTemplate(String template) throws IOException {
        final String TEST_TEMPLATE = "TEST_TEMPLATE";
        templatesLoader.uploadTemplate(TEST_TEMPLATE, template);
        return "upload://" + TEST_TEMPLATE;
    }

    protected Map<String, Object> createDataInDataContianer(Object value) {
        String valueStr = Objects.toString(value, null);
        return Collections.singletonMap(DATA_CONTAINER_KEY, new SingleValueContainer(valueStr));

    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class SingleValueContainer implements Serializable{
    private String value;
}



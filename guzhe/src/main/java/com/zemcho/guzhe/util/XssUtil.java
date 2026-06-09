package com.zemcho.guzhe.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

public class XssUtil {

    private static Safelist whitelist = Safelist.basic();

    private static Document.OutputSettings outputSettings = new Document.OutputSettings().prettyPrint(false);

    static {
        whitelist.addAttributes(":all", "style");
        whitelist.addAttributes(":all", "align", "class");
        whitelist.addTags("img").addAttributes("img", "align", "alt", "height", "src", "title", "width");
        whitelist.addTags("s");
        whitelist.addTags("table").addAttributes("table", "border");
        whitelist.addTags("caption");
        whitelist.addTags("thead");
        whitelist.addTags("tr");
        whitelist.addTags("th").addAttributes("th", "colspan", "headers", "rowspan", "scope");
        whitelist.addTags("tbody");
        whitelist.addTags("td").addAttributes("td", "colspan", "headers", "rowspan", "valign", "nowrap");
        whitelist.addTags("hr");
        whitelist.addTags("del");
        whitelist.addTags("font").addAttributes("font", "face");
        whitelist.addTags("video").addAttributes("video", "src");
        whitelist.addTags("audio").addAttributes("audio", "src");
    }

    public static String filter(String content) {
        if (content != null) {
            return Jsoup.clean(content, "", whitelist, outputSettings);
        }
        return content;
    }
}
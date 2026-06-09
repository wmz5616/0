package com.zemcho.guzhe.util.pdf;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.properties.AreaBreakType;
import com.zemcho.guzhe.util.BigDecimalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @title: PdfUtil
 * @Description:
 * @Date: 2024/2/23 17:15
 */
public class PdfUtil {
    private static final String SHIPPED_FONT_RESOURCE_PATH = Objects.requireNonNull(PdfUtil.class.getResource
            ("/font/simhei.ttf")).toString();
//    private static final String SHIPPED_FONT_RESOURCE_PATH = Objects.requireNonNull(PdfUtil.class.getResource
//    ("/font" +
//            "/simhei.ttf")).toString();
//

    /**
     * html转pdf文件生成
     *
     * @param html
     * @param pdfFileName
     * @param pdfDirPath
     * @param pageSize
     */
    public static void htmlConvertToPdf(String html, String pdfFileName, String pdfDirPath, PageSize pageSize) {
        try {
//            if (pdfDirPath == null || "".equals(pdfDirPath)) {
//                pdfDirPath = FilePath.UPLOAD;
//            }
            String pdfPath = pdfDirPath + pdfFileName + ".pdf";
            File file = new File(pdfDirPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            OutputStream outputStream = new FileOutputStream(pdfPath);

            PdfWriter pdfWriter = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);

            // 设置为纸张大小，默认为A4
            if (pageSize == null) {
                pageSize = PageSize.A4;
            }
            pdfDocument.setDefaultPageSize(pageSize);

            // 设置字体，支持中文
            ConverterProperties converterProperties = new ConverterProperties();
            FontProvider fontProvider = new DefaultFontProvider();
//            FontProgram fontProgram = FontProgramFactory.createFont(SHIPPED_FONT_RESOURCE_PATH + ",0");
            FontProgram fontProgram = FontProgramFactory.createFont(SHIPPED_FONT_RESOURCE_PATH);
            fontProvider.addFont(fontProgram);
            converterProperties.setFontProvider(fontProvider);

            // 生成 pdf 文档
            HtmlConverter.convertToPdf(html, pdfDocument, converterProperties);
            pdfWriter.close();
            pdfDocument.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * html转pdf导出
     *
     * @param html
     * @param pdfFileName
     * @param request
     * @param response
     * @param pageSize
     */
    public static void htmlConvertToPdfExportV2(String html, String pdfFileName, HttpServletRequest request,
                                                HttpServletResponse response, PageSize pageSize) {
        try {
            OutputStream outputStream = response.getOutputStream();

            String userAgent = request.getHeader("USER-AGENT");
            try {
                if (StringUtils.contains(userAgent, "Mozilla")) {
                    pdfFileName = new String(pdfFileName.getBytes(), "ISO8859-1");
                } else {
                    pdfFileName = URLEncoder.encode(pdfFileName, "utf8");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + pdfFileName + ".pdf");

            PdfWriter pdfWriter = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);

            // 设置为纸张大小，默认为A4
            if (pageSize == null) {
                pageSize = PageSize.A4;
            }
            pdfDocument.setDefaultPageSize(pageSize);

            // 设置字体，支持中文
            ConverterProperties converterProperties = new ConverterProperties();
            FontProvider fontProvider = new DefaultFontProvider();
//            FontProgram fontProgram = FontProgramFactory.createFont(SHIPPED_FONT_RESOURCE_PATH + ",0");
            FontProgram fontProgram = FontProgramFactory.createFont(SHIPPED_FONT_RESOURCE_PATH);
            fontProvider.addFont(fontProgram);
            converterProperties.setFontProvider(fontProvider);

            // 生成 pdf 文档
            HtmlConverter.convertToPdf(html, pdfDocument, converterProperties);
            outputStream.close();
            pdfWriter.close();
            pdfDocument.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * html转pdf导出
     *
     * @param html
     * @param pdfFileName
     * @param request
     * @param response
     */
    public static void htmlConvertToPdfExport(String html, String pdfFileName, HttpServletRequest request,
                                              HttpServletResponse response) {
        try {
            // 设置字体，支持中文
            ConverterProperties converterProperties = new ConverterProperties();
            FontProvider fontProvider = new DefaultFontProvider();
//            FontProgram fontProgram = FontProgramFactory.createFont(SHIPPED_FONT_RESOURCE_PATH + ",0");
            FontProgram fontProgram = FontProgramFactory.createFont(SHIPPED_FONT_RESOURCE_PATH);
            fontProvider.addFont(fontProgram);
            converterProperties.setFontProvider(fontProvider);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            HtmlConverter.convertToPdf(html, bos, converterProperties);

            OutputStream fos = response.getOutputStream();
            String userAgent = request.getHeader("USER-AGENT");
            try {
                if (StringUtils.contains(userAgent, "Mozilla")) {
                    pdfFileName = new String(pdfFileName.getBytes(), "ISO8859-1");
                } else {
                    pdfFileName = URLEncoder.encode(pdfFileName, "utf8");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + pdfFileName + ".pdf");
            fos.write(bos.toByteArray());
            fos.close();
            bos.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * html转pdf分页处理
     *
     * @param pageHtml    html内容,一个元素内容就是一页
     * @param pdfFileName
     * @param pdfDirPath
     * @param pageSize
     * @param marginsMap
     */
    public static void pageHtmlConvertToPdf(List<String> pageHtml, String pdfFileName, String pdfDirPath,
                                            PageSize pageSize, Map<String, Double> marginsMap) {
        try {
//            if (pdfDirPath == null || "".equals(pdfDirPath)) {
//                pdfDirPath = FilePath.UPLOAD;
//            }
            String pdfPath = pdfDirPath + pdfFileName + ".pdf";
            File file = new File(pdfDirPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            OutputStream outputStream = new FileOutputStream(pdfPath);

            PdfWriter pdfWriter = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);

            // 设置为纸张大小，默认为A4
            if (pageSize == null) {
                pageSize = PageSize.A4;
            }
            pdfDocument.setDefaultPageSize(pageSize);

            // 设置字体，支持中文
            ConverterProperties converterProperties = new ConverterProperties();
            FontProvider fontProvider = new DefaultFontProvider();
//            FontProgram fontProgram = FontProgramFactory.createFont(SHIPPED_FONT_RESOURCE_PATH + ",0");
            FontProgram fontProgram = FontProgramFactory.createFont(SHIPPED_FONT_RESOURCE_PATH);
            fontProvider.addFont(fontProgram);
            converterProperties.setFontProvider(fontProvider);

            // 生成 pdf 文档
            Document document = new Document(pdfDocument);
            if (marginsMap != null) {
                document.setMargins(marginsMap.get("top").floatValue(), marginsMap.get("right").floatValue(),
                        marginsMap.get("bottom").floatValue(), marginsMap.get("left").floatValue());
            }
            Integer totalPage = pageHtml.size();
            Integer page = 1;
            for (String html : pageHtml) {
                List<IElement> iElements = HtmlConverter.convertToElements(html, converterProperties);
                for (IElement iElement : iElements) {
                    document.add((IBlockElement) iElement);
                }
                if (page < totalPage) {  //添加新页
                    document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                }
                page++;
            }

            document.close();
            pdfWriter.close();
            pdfDocument.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * html转pdf分页导出
     *
     * @param pageHtml
     * @param pdfFileName
     * @param request
     * @param response
     * @param pageSize
     * @param marginsMap
     */
    public static void pageHtmlConvertToPdfExport(List<String> pageHtml, String pdfFileName, HttpServletRequest request,
                                                  HttpServletResponse response, PageSize pageSize,
                                                  Map<String, Double> marginsMap) {
        try {
            OutputStream outputStream = response.getOutputStream();

            String userAgent = request.getHeader("USER-AGENT");
            try {
                if (StringUtils.contains(userAgent, "Mozilla")) {
                    pdfFileName = new String(pdfFileName.getBytes(), "ISO8859-1");
                } else {
                    pdfFileName = URLEncoder.encode(pdfFileName, "utf8");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + pdfFileName + ".pdf");

            PdfWriter pdfWriter = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);

            // 设置为纸张大小，默认为A4
            if (pageSize == null) {
                pageSize = PageSize.A4;
            }
            pdfDocument.setDefaultPageSize(pageSize);

            // 设置字体，支持中文
            ConverterProperties converterProperties = new ConverterProperties();
            FontProvider fontProvider = new DefaultFontProvider();
//            FontProgram fontProgram = FontProgramFactory.createFont(SHIPPED_FONT_RESOURCE_PATH + ",0");
            FontProgram fontProgram = FontProgramFactory.createFont(SHIPPED_FONT_RESOURCE_PATH);
            fontProvider.addFont(fontProgram);
            converterProperties.setFontProvider(fontProvider);

            // 生成 pdf 文档
            Document document = new Document(pdfDocument);
            if (marginsMap != null) {
                document.setMargins(marginsMap.get("top").floatValue(), marginsMap.get("right").floatValue(),
                        marginsMap.get("bottom").floatValue(), marginsMap.get("left").floatValue());
            }
            Integer totalPage = pageHtml.size();
            Integer page = 1;
            for (String html : pageHtml) {
                List<IElement> iElements = HtmlConverter.convertToElements(html, converterProperties);
                for (IElement iElement : iElements) {
                    document.add((IBlockElement) iElement);
                }
                if (page < totalPage) {  //添加新页
                    document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                }
                page++;
            }

            document.close();
            outputStream.close();
            pdfWriter.close();
            pdfDocument.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * 毫米转点
     * 1mm ≈ 2.834645...（pt）
     *
     * @param mm
     * @return
     */
    public static Double mmToPt(Integer mm) {
        Double cn = 2.834645;
        Double pt = BigDecimalUtil.multiplicationDouble(cn, mm, 0);

        return pt;
    }

    /**
     * 毫米转像素
     *
     * @param mm
     * @return
     */
    public static Integer mmToPx(Integer mm) {
        float dpi = 96.0f;
        float inch = mm / 25.4f;
        return Math.round(dpi * inch);
    }

    /**
     * 添加可配置水印到 PDF
     *
     * @param srcPdfPath      源 PDF 路径--pdf文件路径
     * @param destPdfPath     输出 PDF 路径--水印pdf文件保存路径
     * @param watermarkText   水印文字
     * @param fontSize        字体大小（pt）
     * @param columns         每行水印数量（建议 1~3）
     * @param verticalSpacing 行间距（pt），建议为 fontSize * 1.2 ~ 1.8
     * @param opacity         透明度（0.0 ~ 1.0），建议 0.2~0.5
     */
    public static void addWatermarkToPdf(String srcPdfPath, String destPdfPath, String watermarkText, float fontSize,
                                         int columns, float verticalSpacing, float opacity) {
        try {
            File destFile = new File(destPdfPath);
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }

            PdfReader reader = new PdfReader(srcPdfPath);
            PdfWriter writer = new PdfWriter(destPdfPath);
            PdfDocument pdfDoc = new PdfDocument(reader, writer);

            // 加载字体
            PdfFont font = PdfFontFactory.createFont(SHIPPED_FONT_RESOURCE_PATH);

            // 设置透明度
            PdfExtGState extGState = new PdfExtGState().setFillOpacity(opacity);

            int totalPages = pdfDoc.getNumberOfPages();

            for (int i = 1; i <= totalPages; i++) {
                PdfPage page = pdfDoc.getPage(i);
                Rectangle pageSize = page.getPageSize();

                PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);
                canvas.saveState()
                        .setFillColor(ColorConstants.LIGHT_GRAY)
                        .setExtGState(extGState);

                // 计算单个水印宽度（用于布局）
                float textWidth = font.getWidth(watermarkText, fontSize);

                // 计算 X 坐标列表（支持多列）
                List<Float> xPositions = calculateXPositions(pageSize.getWidth(), textWidth, columns);

                // 从页面顶部开始绘制水印
                float currentY = pageSize.getHeight() - fontSize;

                while (currentY > -fontSize) { // 允许最后一行略低于底部
                    for (Float x : xPositions) {
                        Canvas layoutCanvas = new Canvas(canvas, pageSize);
                        Paragraph para = new Paragraph(watermarkText)
                                .setFont(font)
                                .setFontSize(fontSize)
                                .setFontColor(ColorConstants.LIGHT_GRAY)
                                .setRotationAngle(Math.toRadians(-45))
                                .setFixedPosition(x, currentY, textWidth);
                        layoutCanvas.add(para);
                    }
                    currentY -= verticalSpacing;
                }

                canvas.restoreState();
            }

            pdfDoc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addWatermarkToPdf(String srcPdfPath, String destPdfPath, String watermarkText) {
        addWatermarkToPdf(srcPdfPath, destPdfPath, watermarkText, 30f, 3, 100f, 0.3f);
    }

    /**
     * 根据列数计算水印的 X 坐标位置
     */
    private static List<Float> calculateXPositions(float pageWidth, float textWidth, int columns) {
        List<Float> positions = new ArrayList<>();
        if (columns <= 1) {
            // 居中
            positions.add((pageWidth - textWidth) / 2);
        } else {
            // 多列均匀分布（带间隙）
            float gapFactor = 1.3f; // 列间距系数
            float totalOccupied = (columns - 1) * textWidth * gapFactor + textWidth;
            if (totalOccupied > pageWidth) {
                // 如果太宽，强制居中单列
                positions.add((pageWidth - textWidth) / 2);
            } else {
                float startX = (pageWidth - totalOccupied) / 2;
                for (int i = 0; i < columns; i++) {
                    positions.add(startX + i * textWidth * gapFactor);
                }
            }
        }
        return positions;
    }
}

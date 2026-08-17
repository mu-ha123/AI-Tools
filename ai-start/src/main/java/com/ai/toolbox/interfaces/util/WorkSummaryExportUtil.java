package com.ai.toolbox.interfaces.util;

import com.ai.toolbox.application.worklog.dto.WorkSummaryDTO;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class WorkSummaryExportUtil {

    public static byte[] exportWorkSummary(WorkSummaryDTO summary) throws IOException {
        try (XWPFDocument document = new XWPFDocument()) {
            XWPFParagraph titlePara = document.createParagraph();
            titlePara.setAlignment(org.apache.poi.xwpf.usermodel.ParagraphAlignment.CENTER);
            XWPFRun titleRun = titlePara.createRun();
            titleRun.setText("工作总结");
            titleRun.setBold(true);
            titleRun.setFontSize(22);
            titleRun.setFontFamily("微软雅黑");

            XWPFParagraph rangePara = document.createParagraph();
            rangePara.setAlignment(org.apache.poi.xwpf.usermodel.ParagraphAlignment.CENTER);
            XWPFRun rangeRun = rangePara.createRun();
            rangeRun.setText(summary.getStartDate() + " ~ " + summary.getEndDate());
            rangeRun.setFontSize(12);
            rangeRun.setColor("666666");
            rangeRun.setFontFamily("微软雅黑");

            XWPFParagraph emptyPara = document.createParagraph();
            emptyPara.createRun().addBreak();

            String[] lines = summary.getContent().split("\n");
            for (String line : lines) {
                XWPFParagraph para = document.createParagraph();
                XWPFRun run = para.createRun();
                run.setText(line);
                run.setFontSize(11);
                run.setFontFamily("微软雅黑");
                if (line.startsWith("【") || line.startsWith("#")) {
                    run.setBold(true);
                    run.setFontSize(13);
                }
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            document.write(bos);
            return bos.toByteArray();
        }
    }
}

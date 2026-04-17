package postmortem.util;

import postmortem.model.BugEntry;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExportUtil {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static boolean exportToCSV(List<BugEntry> bugs, String filePath) {
        try (FileWriter fw = new FileWriter(filePath);
             BufferedWriter writer = new BufferedWriter(fw)) {

            // Write header
            writer.write("ID,User ID,Title,Error Message,Error Context,Solution,Tags,Status,Created At,Updated At\n");

            // Write data
            for (BugEntry bug : bugs) {
                String line = String.join(",",
                        escapeCSV(String.valueOf(bug.getId())),
                        escapeCSV(String.valueOf(bug.getUserId())),
                        escapeCSV(bug.getTitle()),
                        escapeCSV(bug.getErrorMessage()),
                        escapeCSV(bug.getErrorContext()),
                        escapeCSV(bug.getSolution()),
                        escapeCSV(bug.getTags()),
                        escapeCSV(bug.getStatus()),
                        escapeCSV(bug.getCreatedAt() == null ? "" : bug.getCreatedAt().format(DATE_FORMATTER)),
                        escapeCSV(bug.getUpdatedAt() == null ? "" : bug.getUpdatedAt().format(DATE_FORMATTER))
                );
                writer.write(line);
                writer.write("\n");
            }

            return true;
        } catch (IOException e) {
            System.err.println("Error exporting to CSV: " + e.getMessage());
            return false;
        }
    }

    public static String generateHTMLReport(List<BugEntry> bugs) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; }");
        html.append("table { border-collapse: collapse; width: 100%; }");
        html.append("th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }");
        html.append("th { background-color: #0078d4; color: white; }");
        html.append("tr:nth-child(even) { background-color: #f2f2f2; }");
        html.append(".OPEN { color: #d32f2f; font-weight: bold; }");
        html.append(".RESOLVED { color: #388e3c; font-weight: bold; }");
        html.append("</style></head><body>");
        html.append("<h1>Bug Entry Report</h1>");
        html.append("<p>Generated: ").append(LocalDateTime.now().format(DATE_FORMATTER)).append("</p>");
        html.append("<table><tr><th>ID</th><th>Title</th><th>Error Message</th><th>Status</th><th>Created</th></tr>");

        for (BugEntry bug : bugs) {
            html.append("<tr>");
            html.append("<td>").append(bug.getId()).append("</td>");
            html.append("<td>").append(bug.getTitle()).append("</td>");
            html.append("<td>").append(bug.getErrorMessage()).append("</td>");
            html.append("<td class=\"").append(bug.getStatus()).append("\">").append(bug.getStatus()).append("</td>");
            html.append("<td>").append(bug.getCreatedAt().format(DATE_FORMATTER)).append("</td>");
            html.append("</tr>");
        }

        html.append("</table></body></html>");
        return html.toString();
    }

    public static boolean exportHTMLReport(List<BugEntry> bugs, String filePath) {
        try (FileWriter fw = new FileWriter(filePath)) {
            fw.write(generateHTMLReport(bugs));
            return true;
        } catch (IOException e) {
            System.err.println("Error exporting HTML report: " + e.getMessage());
            return false;
        }
    }

    private static String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}

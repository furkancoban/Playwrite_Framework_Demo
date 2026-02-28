package com.example.orangehrm.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Tracks test execution in real-time and generates reports immediately after each scenario.
 * This ensures reports are available even if tests are interrupted mid-run.
 */
public class SimpleReportTracker {
    
    private static final String REPORT_DATA_FILE = "target/test-execution-data.txt";
    private static final String HTML_REPORT_FILE = "target/test-execution-report.html";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private static List<ScenarioResult> scenarios = new ArrayList<>();
    private static LocalDateTime testStartTime = LocalDateTime.now();
    
    public static class ScenarioResult {
        String name;
        String status;
        LocalDateTime startTime;
        LocalDateTime endTime;
        long durationMs;
        
        public ScenarioResult(String name, String status, LocalDateTime startTime, LocalDateTime endTime, long durationMs) {
            this.name = name;
            this.status = status;
            this.startTime = startTime;
            this.endTime = endTime;
            this.durationMs = durationMs;
        }
    }
    
    /**
     * Record scenario result and immediately flush to disk
     */
    public static synchronized void recordScenario(String name, String status, LocalDateTime startTime, LocalDateTime endTime, long durationMs) {
        ScenarioResult result = new ScenarioResult(name, status, startTime, endTime, durationMs);
        scenarios.add(result);
        
        // Immediately write to disk so data is available if tests are interrupted
        flushToDisk();
        generateHtmlReport();
    }
    
    /**
     * Write execution data to text file (fast, simple format)
     */
    private static void flushToDisk() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(REPORT_DATA_FILE))) {
            writer.println("TEST_START_TIME=" + testStartTime.format(DATE_FORMATTER));
            writer.println("TOTAL_SCENARIOS=" + scenarios.size());
            
            int passed = 0;
            int failed = 0;
            long totalDuration = 0;
            
            for (ScenarioResult scenario : scenarios) {
                if ("PASSED".equalsIgnoreCase(scenario.status)) {
                    passed++;
                } else if ("FAILED".equalsIgnoreCase(scenario.status)) {
                    failed++;
                }
                totalDuration += scenario.durationMs;
                
                writer.println("SCENARIO|" + scenario.name + "|" + scenario.status + "|" + 
                             scenario.startTime.format(DATE_FORMATTER) + "|" +
                             scenario.endTime.format(DATE_FORMATTER) + "|" +
                             scenario.durationMs);
            }
            
            writer.println("PASSED=" + passed);
            writer.println("FAILED=" + failed);
            writer.println("TOTAL_DURATION_MS=" + totalDuration);
            
        } catch (IOException e) {
            TestLogger.warn("Failed to write test execution data: " + e.getMessage());
        }
    }
    
    /**
     * Generate HTML report from tracked scenarios
     */
    public static synchronized void generateHtmlReport() {
        try {
            int total = scenarios.size();
            int passed = 0;
            int failed = 0;
            @SuppressWarnings("unused")
			long totalDuration = 0;
            
            for (ScenarioResult scenario : scenarios) {
                if ("PASSED".equalsIgnoreCase(scenario.status)) {
                    passed++;
                } else if ("FAILED".equalsIgnoreCase(scenario.status)) {
                    failed++;
                }
                totalDuration += scenario.durationMs;
            }
            
            double passRate = total > 0 ? (passed * 100.0 / total) : 0;
            
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>\n<html>\n<head>\n");
            html.append("    <title>Test Execution Report</title>\n");
            html.append("    <meta http-equiv='refresh' content='5'>\n"); // Auto refresh every 5 seconds
            html.append("    <style>\n");
            html.append("        body { font-family: Arial, sans-serif; margin: 20px; background: #f5f5f5; }\n");
            html.append("        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; border-radius: 10px; margin-bottom: 20px; }\n");
            html.append("        .header h1 { margin: 0; font-size: 28px; }\n");
            html.append("        .header p { margin: 5px 0; opacity: 0.9; }\n");
            html.append("        .stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; margin-bottom: 20px; }\n");
            html.append("        .stat-card { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }\n");
            html.append("        .stat-card h3 { margin: 0 0 10px 0; color: #666; font-size: 14px; text-transform: uppercase; }\n");
            html.append("        .stat-card .number { font-size: 36px; font-weight: bold; margin: 10px 0; }\n");
            html.append("        .passed { color: #10b981; }\n");
            html.append("        .failed { color: #ef4444; }\n");
            html.append("        .total { color: #3b82f6; }\n");
            html.append("        .scenarios { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }\n");
            html.append("        table { width: 100%; border-collapse: collapse; }\n");
            html.append("        th { background: #f8fafc; text-align: left; padding: 12px; font-weight: 600; color: #475569; border-bottom: 2px solid #e2e8f0; }\n");
            html.append("        td { padding: 12px; border-bottom: 1px solid #e2e8f0; }\n");
            html.append("        tr:hover { background: #f8fafc; }\n");
            html.append("        .badge { display: inline-block; padding: 4px 12px; border-radius: 12px; font-size: 12px; font-weight: 600; }\n");
            html.append("        .badge-passed { background: #d1fae5; color: #065f46; }\n");
            html.append("        .badge-failed { background: #fee2e2; color: #991b1b; }\n");
            html.append("        .live-indicator { display: inline-block; width: 10px; height: 10px; background: #10b981; border-radius: 50%; animation: pulse 2s infinite; margin-right: 8px; }\n");
            html.append("        @keyframes pulse { 0%, 100% { opacity: 1; } 50% { opacity: 0.5; } }\n");
            html.append("        .alert { background: #fef3c7; border-left: 4px solid #f59e0b; padding: 15px; margin-bottom: 20px; border-radius: 4px; }\n");
            html.append("    </style>\n</head>\n<body>\n");
            
            html.append("    <div class='header'>\n");
            html.append("        <h1><span class='live-indicator'></span>Live Test Execution Report</h1>\n");
            html.append("        <p>Started: ").append(testStartTime.format(DATE_FORMATTER)).append("</p>\n");
            html.append("        <p>Last Updated: ").append(LocalDateTime.now().format(DATE_FORMATTER)).append("</p>\n");
            html.append("        <p style='font-size: 12px; margin-top: 10px;'>⚡ This report auto-refreshes every 5 seconds</p>\n");
            html.append("    </div>\n\n");
            
            if (total == 0) {
                html.append("    <div class='alert'>\n");
                html.append("        <strong>⏳ Waiting for test execution...</strong> No scenarios have completed yet.\n");
                html.append("    </div>\n");
            }
            
            html.append("    <div class='stats'>\n");
            html.append("        <div class='stat-card'>\n");
            html.append("            <h3>Total Scenarios</h3>\n");
            html.append("            <div class='number total'>").append(total).append("</div>\n");
            html.append("        </div>\n");
            html.append("        <div class='stat-card'>\n");
            html.append("            <h3>✅ Passed</h3>\n");
            html.append("            <div class='number passed'>").append(passed).append("</div>\n");
            html.append("        </div>\n");
            html.append("        <div class='stat-card'>\n");
            html.append("            <h3>❌ Failed</h3>\n");
            html.append("            <div class='number failed'>").append(failed).append("</div>\n");
            html.append("        </div>\n");
            html.append("        <div class='stat-card'>\n");
            html.append("            <h3>Pass Rate</h3>\n");
            html.append("            <div class='number' style='color: #8b5cf6;'>").append(String.format("%.1f%%", passRate)).append("</div>\n");
            html.append("        </div>\n");
            html.append("    </div>\n\n");
            
            if (total > 0) {
                html.append("    <div class='scenarios'>\n");
                html.append("        <h2 style='margin-top: 0; color: #1e293b;'>Scenario Execution Details</h2>\n");
                html.append("        <table>\n");
                html.append("            <thead>\n");
                html.append("                <tr>\n");
                html.append("                    <th>#</th>\n");
                html.append("                    <th>Scenario Name</th>\n");
                html.append("                    <th>Status</th>\n");
                html.append("                    <th>Start Time</th>\n");
                html.append("                    <th>Duration</th>\n");
                html.append("                </tr>\n");
                html.append("            </thead>\n");
                html.append("            <tbody>\n");
                
                for (int i = 0; i < scenarios.size(); i++) {
                    ScenarioResult scenario = scenarios.get(i);
                    String statusClass = "PASSED".equalsIgnoreCase(scenario.status) ? "badge-passed" : "badge-failed";
                    
                    html.append("                <tr>\n");
                    html.append("                    <td>").append(i + 1).append("</td>\n");
                    html.append("                    <td>").append(escapeHtml(scenario.name)).append("</td>\n");
                    html.append("                    <td><span class='badge ").append(statusClass).append("'>").append(scenario.status).append("</span></td>\n");
                    html.append("                    <td>").append(scenario.startTime.format(DATE_FORMATTER)).append("</td>\n");
                    html.append("                    <td>").append(scenario.durationMs).append(" ms</td>\n");
                    html.append("                </tr>\n");
                }
                
                html.append("            </tbody>\n");
                html.append("        </table>\n");
                html.append("    </div>\n");
            }
            
            html.append("\n    <div style='margin-top: 20px; padding: 15px; background: white; border-radius: 8px; text-align: center; color: #64748b; font-size: 12px;'>\n");
            html.append("        <p>Report File: <code>").append(HTML_REPORT_FILE).append("</code></p>\n");
            html.append("        <p>Data File: <code>").append(REPORT_DATA_FILE).append("</code></p>\n");
            html.append("        <p style='margin-top: 10px;'>This report updates in real-time. Stop the tests anytime - data is preserved.</p>\n");
            html.append("    </div>\n");
            
            html.append("</body>\n</html>");
            
            Files.write(Paths.get(HTML_REPORT_FILE), html.toString().getBytes());
            
        } catch (IOException e) {
            TestLogger.warn("Failed to generate HTML report: " + e.getMessage());
        }
    }
    
    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
    
    /**
     * Reset tracking (useful for new test runs)
     */
    public static synchronized void reset() {
        scenarios.clear();
        testStartTime = LocalDateTime.now();
    }
    
    /**
     * Get current statistics
     */
    public static synchronized String getStats() {
        int total = scenarios.size();
        int passed = 0;
        int failed = 0;
        
        for (ScenarioResult scenario : scenarios) {
            if ("PASSED".equalsIgnoreCase(scenario.status)) {
                passed++;
            } else if ("FAILED".equalsIgnoreCase(scenario.status)) {
                failed++;
            }
        }
        
        return String.format("Total: %d | Passed: %d | Failed: %d", total, passed, failed);
    }
}

package com.example.orangehrm.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

/**
 * Generates HTML report from partial JSON test data.
 * Useful for creating reports even when tests are interrupted before completion.
 */
@SuppressWarnings("unused")
public class ReportGenerator {
    
    private static final String JSON_REPORT_PATH = "target/cucumber.json";
    private static final String HTML_REPORT_PATH = "target/cucumber-report-partial.html";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Generate HTML report from partial JSON data
     * Called when test execution is interrupted
     */
    public static void generatePartialReport() {
        try {
            File jsonFile = new File(JSON_REPORT_PATH);
            if (!jsonFile.exists()) {
                TestLogger.info("Cucumber JSON file not found - this is normal if tests were stopped early");
                return;
            }
            
            if (jsonFile.length() == 0) {
                TestLogger.info("Cucumber JSON file is empty - tests may have been stopped before first scenario completed");
                return;
            }
            
            String jsonContent = new String(Files.readAllBytes(jsonFile.toPath()));
            if (jsonContent.trim().isEmpty() || jsonContent.trim().equals("[]")) {
                TestLogger.info("No scenario data in Cucumber JSON - tests stopped before completion");
                return;
            }
            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode scenarios = null;
            try {
                scenarios = mapper.readTree(jsonContent);
            } catch (Exception parseEx) {
                TestLogger.info("Cucumber JSON is incomplete or malformed - tests were likely interrupted mid-scenario");
                return;
            }
            
            int totalScenarios = 0;
            int passedScenarios = 0;
            int failedScenarios = 0;
            int totalSteps = 0;
            int passedSteps = 0;
            int failedSteps = 0;
            int skippedSteps = 0;
            
            // Parse scenarios
            if (scenarios.isArray()) {
                for (JsonNode scenario : scenarios) {
                    totalScenarios++;
                    boolean scenarioFailed = false;
                    
                    JsonNode steps = scenario.get("steps");
                    if (steps != null && steps.isArray()) {
                        for (JsonNode step : steps) {
                            totalSteps++;
                            JsonNode result = step.get("result");
                            if (result != null) {
                                String status = result.get("status").asText();
                                if ("passed".equalsIgnoreCase(status)) {
                                    passedSteps++;
                                } else if ("failed".equalsIgnoreCase(status)) {
                                    failedSteps++;
                                    scenarioFailed = true;
                                } else if ("skipped".equalsIgnoreCase(status) || "undefined".equalsIgnoreCase(status)) {
                                    skippedSteps++;
                                }
                            }
                        }
                    }
                    
                    if (scenarioFailed) {
                        failedScenarios++;
                    } else {
                        passedScenarios++;
                    }
                }
            }
            
            String htmlReport = generateHtmlContent(totalScenarios, passedScenarios, failedScenarios, 
                                                   totalSteps, passedSteps, failedSteps, skippedSteps);
            
            Files.write(Paths.get(HTML_REPORT_PATH), htmlReport.getBytes());
            TestLogger.success("Partial HTML report generated: " + HTML_REPORT_PATH);
            
        } catch (IOException e) {
            TestLogger.error("Failed to generate partial report", e);
        }
    }
    
    private static String generateHtmlContent(int totalScenarios, int passedScenarios, int failedScenarios,
                                             int totalSteps, int passedSteps, int failedSteps, int skippedSteps) {
        
        double scenarioPassRate = totalScenarios > 0 ? (passedScenarios * 100.0 / totalScenarios) : 0;
        double stepPassRate = totalSteps > 0 ? (passedSteps * 100.0 / totalSteps) : 0;
        
        return "<!DOCTYPE html>\n" +
               "<html>\n" +
               "<head>\n" +
               "    <title>Cucumber Test Report (Partial)</title>\n" +
               "    <style>\n" +
               "        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }\n" +
               "        .header { background-color: #333; color: white; padding: 20px; border-radius: 5px; }\n" +
               "        .summary { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; margin: 20px 0; }\n" +
               "        .card { background: white; padding: 20px; border-radius: 5px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }\n" +
               "        .card h3 { margin: 0 0 10px 0; color: #333; }\n" +
               "        .card .number { font-size: 32px; font-weight: bold; color: #666; }\n" +
               "        .passed { color: #28a745; }\n" +
               "        .failed { color: #dc3545; }\n" +
               "        .skipped { color: #ffc107; }\n" +
               "        .total { color: #007bff; }\n" +
               "        .bar { width: 100%; height: 30px; background-color: #e9ecef; border-radius: 3px; overflow: hidden; margin: 10px 0; }\n" +
               "        .bar-segment { display: inline-block; height: 100%; }\n" +
               "        .bar-passed { background-color: #28a745; }\n" +
               "        .bar-failed { background-color: #dc3545; }\n" +
               "        .bar-skipped { background-color: #ffc107; }\n" +
               "        .status { text-align: center; font-size: 24px; margin: 20px 0; }\n" +
               "        .warning { background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 15px 0; border-radius: 3px; }\n" +
               "        table { width: 100%; border-collapse: collapse; margin: 20px 0; }\n" +
               "        th, td { padding: 10px; text-align: left; border-bottom: 1px solid #ddd; }\n" +
               "        th { background-color: #f8f9fa; font-weight: bold; }\n" +
               "    </style>\n" +
               "</head>\n" +
               "<body>\n" +
               "    <div class='header'>\n" +
               "        <h1>🧪 Cucumber Test Report (Partial/Interrupted)</h1>\n" +
               "        <p>Report Generated: " + LocalDateTime.now().format(DATE_FORMATTER) + "</p>\n" +
               "        <p><strong>Status:</strong> Tests were interrupted before completion</p>\n" +
               "        <p>For complete reports, use the official Cucumber reporting plugin or view the JSON file directly.</p>\n" +
               "    </div>\n" +
               "\n" +
               "    <div class='warning'>\n" +
               "        <strong>⚠️ Note:</strong> This is a partial report. Not all tests may have completed. " +
               "Official HTML report: <code>target/cucumber-reports/index.html</code> (requires full test run)\n" +
               "    </div>\n" +
               "\n" +
               "    <h2>Scenario Summary</h2>\n" +
               "    <div class='summary'>\n" +
               "        <div class='card'>\n" +
               "            <h3>Total Scenarios</h3>\n" +
               "            <div class='number total'>" + totalScenarios + "</div>\n" +
               "        </div>\n" +
               "        <div class='card'>\n" +
               "            <h3 class='passed'>✅ Passed</h3>\n" +
               "            <div class='number passed'>" + passedScenarios + "</div>\n" +
               "        </div>\n" +
               "        <div class='card'>\n" +
               "            <h3 class='failed'>❌ Failed</h3>\n" +
               "            <div class='number failed'>" + failedScenarios + "</div>\n" +
               "        </div>\n" +
               "        <div class='card'>\n" +
               "            <h3>Pass Rate</h3>\n" +
               "            <div class='number' style='color: #666;'>" + String.format("%.1f%%", scenarioPassRate) + "</div>\n" +
               "        </div>\n" +
               "    </div>\n" +
               "\n" +
               "    <h2>Step Summary</h2>\n" +
               "    <div class='summary'>\n" +
               "        <div class='card'>\n" +
               "            <h3>Total Steps</h3>\n" +
               "            <div class='number total'>" + totalSteps + "</div>\n" +
               "        </div>\n" +
               "        <div class='card'>\n" +
               "            <h3 class='passed'>✅ Passed</h3>\n" +
               "            <div class='number passed'>" + passedSteps + "</div>\n" +
               "        </div>\n" +
               "        <div class='card'>\n" +
               "            <h3 class='failed'>❌ Failed</h3>\n" +
               "            <div class='number failed'>" + failedSteps + "</div>\n" +
               "        </div>\n" +
               "        <div class='card'>\n" +
               "            <h3 class='skipped'>⏭️ Skipped/Undefined</h3>\n" +
               "            <div class='number skipped'>" + skippedSteps + "</div>\n" +
               "        </div>\n" +
               "    </div>\n" +
               "\n" +
               "    <h2>Progress Visualization</h2>\n" +
               "    <div style='background: white; padding: 20px; border-radius: 5px;'>\n" +
               "        <p><strong>Scenario Progress</strong></p>\n" +
               "        <div class='bar'>\n" +
               "            <div class='bar-segment bar-passed' style='width: " + scenarioPassRate + "%;'></div>\n" +
               "            <div class='bar-segment bar-failed' style='width: " + (totalScenarios > 0 ? (failedScenarios * 100.0 / totalScenarios) : 0) + "%;'></div>\n" +
               "        </div>\n" +
               "        <p><strong>Step Progress</strong></p>\n" +
               "        <div class='bar'>\n" +
               "            <div class='bar-segment bar-passed' style='width: " + stepPassRate + "%;'></div>\n" +
               "            <div class='bar-segment bar-failed' style='width: " + (totalSteps > 0 ? (failedSteps * 100.0 / totalSteps) : 0) + "%;'></div>\n" +
               "            <div class='bar-segment bar-skipped' style='width: " + (totalSteps > 0 ? (skippedSteps * 100.0 / totalSteps) : 0) + "%;'></div>\n" +
               "        </div>\n" +
               "    </div>\n" +
               "\n" +
               "    <h2>Files</h2>\n" +
               "    <ul>\n" +
               "        <li>JSON Report: <code>target/cucumber.json</code></li>\n" +
               "        <li>This Report (Partial): <code>target/cucumber-report-partial.html</code></li>\n" +
               "        <li>Full Report: <code>target/cucumber-reports/index.html</code> (after complete test run)</li>\n" +
               "    </ul>\n" +
               "\n" +
               "</body>\n" +
               "</html>";
    }
}

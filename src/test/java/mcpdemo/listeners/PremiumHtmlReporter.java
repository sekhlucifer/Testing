package mcpdemo.listeners;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PremiumHtmlReporter implements IReporter {

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        int totalTests = 0;
        int passedTests = 0;
        int failedTests = 0;
        int skippedTests = 0;

        StringBuilder rowsHtml = new StringBuilder();
        StringBuilder detailedRowsHtml = new StringBuilder();
        
        int tcId = 1;

        for (ISuite suite : suites) {
            Map<String, ISuiteResult> results = suite.getResults();
            for (ISuiteResult result : results.values()) {
                ITestContext context = result.getTestContext();
                
                passedTests += context.getPassedTests().size();
                failedTests += context.getFailedTests().size();
                skippedTests += context.getSkippedTests().size();
                totalTests = passedTests + failedTests + skippedTests;

                // Build Passed Tests
                for (ITestResult passedTest : context.getPassedTests().getAllResults()) {
                    long duration = (passedTest.getEndMillis() - passedTest.getStartMillis());
                    String name = passedTest.getName() + " (" + passedTest.getTestContext().getName() + ")";
                    rowsHtml.append(buildRow(name, "Passed", duration / 1000.0));
                    detailedRowsHtml.append(buildDetailedRow("TC_" + String.format("%02d", tcId++), name, "Test executed successfully", "Pass", "-"));
                }

                // Build Failed Tests
                for (ITestResult failedTest : context.getFailedTests().getAllResults()) {
                    long duration = (failedTest.getEndMillis() - failedTest.getStartMillis());
                    String name = failedTest.getName() + " (" + failedTest.getTestContext().getName() + ")";
                    rowsHtml.append(buildRow(name, "Failed", duration / 1000.0));
                    String error = failedTest.getThrowable() != null ? failedTest.getThrowable().getClass().getSimpleName() : "Unknown Error";
                    detailedRowsHtml.append(buildDetailedRow("TC_" + String.format("%02d", tcId++), name, "Test failed during execution", "Fail", error));
                }

                // Build Skipped Tests
                for (ITestResult skippedTest : context.getSkippedTests().getAllResults()) {
                    long duration = (skippedTest.getEndMillis() - skippedTest.getStartMillis());
                    String name = skippedTest.getName() + " (" + skippedTest.getTestContext().getName() + ")";
                    rowsHtml.append(buildRow(name, "Skipped", duration / 1000.0));
                    detailedRowsHtml.append(buildDetailedRow("TC_" + String.format("%02d", tcId++), name, "Test was skipped", "Skip", "-"));
                }
            }
        }

        String dateStr = new SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(new Date());

        String template = getHtmlTemplate();
        template = template.replace("{{DATE}}", dateStr);
        template = template.replace("{{TOTAL}}", String.valueOf(totalTests));
        template = template.replace("{{PASSED}}", String.valueOf(passedTests));
        template = template.replace("{{FAILED}}", String.valueOf(failedTests));
        template = template.replace("{{SKIPPED}}", String.valueOf(skippedTests));
        template = template.replace("{{SUMMARY_ROWS}}", rowsHtml.toString());
        template = template.replace("{{DETAILED_ROWS}}", detailedRowsHtml.toString());

        // Ensure output directory exists
        File outDir = new File(outputDirectory);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            FileWriter writer = new FileWriter(new File(outDir, "Premium-Report.html"));
            writer.write(template);
            writer.close();
            System.out.println("Premium Report Generated: " + outDir.getAbsolutePath() + "/Premium-Report.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String buildRow(String name, String status, double durationSec) {
        String badgeClass = status.equals("Passed") ? "badge-pass" : status.equals("Failed") ? "badge-fail" : "badge-skip";
        return "<tr class=\"row-data\">" +
                "<td>" + name + "</td>" +
                "<td><span class=\"badge " + badgeClass + "\">" + status + "</span></td>" +
                "<td>" + String.format("%.2f", durationSec) + "s</td>" +
                "</tr>\n";
    }

    private String buildDetailedRow(String id, String scenario, String expected, String status, String error) {
        String badgeClass = status.equals("Pass") ? "badge-pass" : status.equals("Fail") ? "badge-fail" : "badge-skip";
        return "<tr class=\"row-data\">" +
                "<td style=\"color: var(--text-muted)\">#" + id + "</td>" +
                "<td>" + scenario + "</td>" +
                "<td>" + expected + "</td>" +
                "<td>" + (error.equals("-") ? "Matched expected" : error) + "</td>" +
                "<td><span class=\"badge " + badgeClass + "\">" + status + "</span></td>" +
                "</tr>\n";
    }

    private String getHtmlTemplate() {
        return """
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Premium Automation Report</title>
  <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
  <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;600;700&display=swap" rel="stylesheet">
  <style>
    :root {
      --bg-gradient: linear-gradient(135deg, #0f172a 0%, #1e1b4b 100%);
      --glass-bg: rgba(255, 255, 255, 0.03);
      --glass-border: rgba(255, 255, 255, 0.08);
      --text-main: #f8fafc;
      --text-muted: #94a3b8;
      --pass: #10b981;
      --fail: #f43f5e;
      --skip: #f59e0b;
      --total: #3b82f6;
    }

    * { box-sizing: border-box; margin: 0; padding: 0; }

    body {
      font-family: 'Outfit', sans-serif;
      background: var(--bg-gradient);
      color: var(--text-main);
      min-height: 100vh;
      padding: 40px 20px;
    }

    .container {
      max-width: 1200px;
      margin: 0 auto;
      background: var(--glass-bg);
      backdrop-filter: blur(16px);
      -webkit-backdrop-filter: blur(16px);
      border: 1px solid var(--glass-border);
      border-radius: 24px;
      padding: 40px;
      box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
      animation: fadeUp 0.8s ease-out;
    }

    @keyframes fadeUp {
      from { opacity: 0; transform: translateY(30px); }
      to { opacity: 1; transform: translateY(0); }
    }

    .header { display: flex; flex-direction: column; align-items: center; margin-bottom: 40px; text-align: center; }

    .logo {
      width: 70px; height: 70px; background: rgba(255,255,255,0.1); border-radius: 20px;
      display: flex; align-items: center; justify-content: center; margin-bottom: 20px;
      box-shadow: 0 10px 25px rgba(0,0,0,0.2);
    }
    .logo img { width: 45px; }

    h1 {
      font-size: 2.5rem; font-weight: 700;
      background: linear-gradient(to right, #60a5fa, #a78bfa);
      -webkit-background-clip: text; -webkit-text-fill-color: transparent; margin-bottom: 15px;
    }

    .meta-grid { display: flex; gap: 20px; flex-wrap: wrap; justify-content: center; }
    .meta-item { background: rgba(255,255,255,0.05); padding: 8px 16px; border-radius: 30px; font-size: 0.9rem; color: var(--text-muted); border: 1px solid var(--glass-border); }
    .meta-item span { color: var(--text-main); font-weight: 600; margin-left: 5px; }

    .summary { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin-bottom: 40px; }
    .card { background: var(--glass-bg); border: 1px solid var(--glass-border); border-radius: 20px; padding: 25px; text-align: center; transition: all 0.3s ease; position: relative; overflow: hidden; }
    .card::before { content: ''; position: absolute; top: 0; left: 0; width: 100%; height: 4px; }
    .card.total::before { background: var(--total); }
    .card.passed::before { background: var(--pass); }
    .card.failed::before { background: var(--fail); }
    .card.skipped::before { background: var(--skip); }
    .card:hover { transform: translateY(-5px); box-shadow: 0 15px 30px rgba(0,0,0,0.3); background: rgba(255,255,255,0.06); }
    .card-title { font-size: 1.1rem; color: var(--text-muted); text-transform: uppercase; letter-spacing: 1px; margin-bottom: 10px; }
    .card-value { font-size: 2.5rem; font-weight: 700; }

    .dashboard-layout { display: flex; gap: 40px; margin-bottom: 40px; }
    .chart-wrapper { flex: 1; background: var(--glass-bg); border: 1px solid var(--glass-border); border-radius: 24px; padding: 20px; display: flex; align-items: center; justify-content: center; min-height: 300px; }
    .table-wrapper { flex: 2; }

    .section-title { font-size: 1.5rem; font-weight: 600; margin-bottom: 20px; display: flex; align-items: center; gap: 10px; }
    .section-title::before { content: ''; width: 12px; height: 12px; background: #a78bfa; border-radius: 50%; display: inline-block; }

    table { width: 100%; border-collapse: separate; border-spacing: 0 8px; }
    th, td { padding: 16px 20px; text-align: left; }
    th { color: var(--text-muted); font-weight: 600; font-size: 0.9rem; text-transform: uppercase; letter-spacing: 0.5px; }

    tr.row-data { background: var(--glass-bg); transition: all 0.2s ease; }
    tr.row-data td:first-child { border-top-left-radius: 12px; border-bottom-left-radius: 12px; }
    tr.row-data td:last-child { border-top-right-radius: 12px; border-bottom-right-radius: 12px; }
    tr.row-data:hover { background: rgba(255,255,255,0.08); transform: scale(1.01); }

    .badge { padding: 6px 12px; border-radius: 20px; font-size: 0.85rem; font-weight: 600; letter-spacing: 0.5px; display: inline-block; }
    .badge-pass { background: rgba(16, 185, 129, 0.15); color: var(--pass); border: 1px solid rgba(16, 185, 129, 0.3); }
    .badge-fail { background: rgba(244, 63, 94, 0.15); color: var(--fail); border: 1px solid rgba(244, 63, 94, 0.3); }
    .badge-skip { background: rgba(245, 158, 11, 0.15); color: var(--skip); border: 1px solid rgba(245, 158, 11, 0.3); }

    .footer { text-align: center; margin-top: 40px; padding-top: 20px; border-top: 1px solid var(--glass-border); color: var(--text-muted); font-size: 0.9rem; }
    @media (max-width: 900px) { .dashboard-layout { flex-direction: column; } }
  </style>
</head>
<body>
<div class="container">
  <div class="header">
    <div class="logo"><img src="https://playwright.dev/img/playwright-logo.svg" alt="Playwright Logo"></div>
    <h1>Execution Report</h1>
    <div class="meta-grid">
      <div class="meta-item">Project: <span>Automation Setup</span></div>
      <div class="meta-item">Date: <span>{{DATE}}</span></div>
      <div class="meta-item">Environment: <span>QA</span></div>
    </div>
  </div>

  <div class="summary">
    <div class="card total">
      <div class="card-title">Total Tests</div>
      <div class="card-value" style="color: var(--total)">{{TOTAL}}</div>
    </div>
    <div class="card passed">
      <div class="card-title">Passed</div>
      <div class="card-value" style="color: var(--pass)">{{PASSED}}</div>
    </div>
    <div class="card failed">
      <div class="card-title">Failed</div>
      <div class="card-value" style="color: var(--fail)">{{FAILED}}</div>
    </div>
    <div class="card skipped">
      <div class="card-title">Skipped</div>
      <div class="card-value" style="color: var(--skip)">{{SKIPPED}}</div>
    </div>
  </div>

  <div class="dashboard-layout">
    <div class="chart-wrapper">
      <canvas id="resultChart"></canvas>
    </div>
    <div class="table-wrapper">
      <h2 class="section-title">Recent Executions</h2>
      <table>
        <thead><tr><th>Test Name</th><th>Status</th><th>Duration</th></tr></thead>
        <tbody>
          {{SUMMARY_ROWS}}
        </tbody>
      </table>
    </div>
  </div>

  <h2 class="section-title">Detailed Analysis</h2>
  <table>
    <thead><tr><th>ID</th><th>Scenario</th><th>Expected</th><th>Actual</th><th>Status</th></tr></thead>
    <tbody>
      {{DETAILED_ROWS}}
    </tbody>
  </table>

  <div class="footer">
    Generated with ❤️ by Playwright Automation Framework • © 2026
  </div>
  
  <!-- Utterances Customer Feedback Widget -->
  <script src="https://utteranc.es/client.js" repo="sekhlucifer/Testing" issue-term="pathname" theme="github-light" crossorigin="anonymous" async></script>
</div>

<script>
  Chart.defaults.color = '#94a3b8';
  Chart.defaults.font.family = "'Outfit', sans-serif";
  const ctx = document.getElementById('resultChart');
  new Chart(ctx, {
    type: 'doughnut',
    data: {
      labels: ['Passed', 'Failed', 'Skipped'],
      datasets: [{
        data: [{{PASSED}}, {{FAILED}}, {{SKIPPED}}],
        backgroundColor: ['rgba(16, 185, 129, 0.8)','rgba(244, 63, 94, 0.8)','rgba(245, 158, 11, 0.8)'],
        borderColor: ['#10b981','#f43f5e','#f59e0b'],
        borderWidth: 2, hoverOffset: 4
      }]
    },
    options: {
      responsive: true, maintainAspectRatio: false, cutout: '70%',
      plugins: { legend: { position: 'bottom', labels: { padding: 20, usePointStyle: true, pointStyle: 'circle' } } },
      animation: { animateScale: true, animateRotate: true }
    }
  });
</script>
</body>
</html>
""";
    }
}

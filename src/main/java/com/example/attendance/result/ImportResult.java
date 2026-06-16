package com.example.attendance.result;

import java.util.ArrayList;
import java.util.List;

public class ImportResult {
    private int successCount = 0;
    private int failCount = 0;
    private List<String> failReports = new ArrayList<>();

    public ImportResult() {
    }

    public ImportResult(int successCount, int failCount, List<String> failReports) {
        this.successCount = successCount;
        this.failCount = failCount;
        this.failReports = failReports;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public List<String> getFailReports() {
        return failReports;
    }

    public void setFailReports(List<String> failReports) {
        this.failReports = failReports;
    }

    public void incrementSuccess() {
        successCount++;
    }

    public void incrementFail() {
        failCount++;
    }

    // getter
    public int getSuccessCount() {
        return successCount;
    }

    public int getFailCount() {
        return failCount;
    }
    public void incrementFail(String reason) {
        this.failCount++;
        this.failReports.add(reason);
    }
}

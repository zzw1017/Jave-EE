package com.example.attendance.result;

public class ImportResult {
    private int successCount = 0;
    private int failCount = 0;

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
}

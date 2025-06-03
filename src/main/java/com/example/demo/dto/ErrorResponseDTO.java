package com.example.demo.dto;

import java.time.LocalDateTime;

public class ErrorResponseDTO {
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String details;

    public ErrorResponseDTO() {
        this.timestamp = LocalDateTime.now().toString();
    }

    public ErrorResponseDTO(int status, String error, String message, String path) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public ErrorResponseDTO(int status, String error, String message, String path, String details) {
        this(status, error, message, path);
        this.details = details;
    }

    // Getters and Setters
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}

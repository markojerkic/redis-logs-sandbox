package dev.jerkic.redis_logs.model.dto;

public record LogLineFilter(Long before, Long after, Long logId) {}

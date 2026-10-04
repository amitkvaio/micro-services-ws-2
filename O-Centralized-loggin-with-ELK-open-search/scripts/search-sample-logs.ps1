param(
    [string]$OpenSearchUrl = "http://localhost:9200",
    [string]$TraceId = "trace-2026-elk-demo-001"
)

$ErrorActionPreference = "Stop"

$body = @{
    query = @{
        match = @{
            traceId = $TraceId
        }
    }
    sort = @(
        @{
            "@timestamp" = @{
                order = "asc"
            }
        }
    )
} | ConvertTo-Json -Depth 10

Invoke-RestMethod `
    -Method Post `
    -Uri "$OpenSearchUrl/microservices-logs-demo/_search?pretty" `
    -ContentType "application/json" `
    -Body $body

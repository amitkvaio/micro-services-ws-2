param(
    [string]$OpenSearchUrl = "http://localhost:9200"
)

$ErrorActionPreference = "Stop"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$chapterDir = Split-Path -Parent $scriptDir
$bulkFile = Join-Path $chapterDir "sample-logs\microservices-logs.ndjson"
$indexName = "microservices-logs-demo"

if (-not (Test-Path -LiteralPath $bulkFile)) {
    throw "Sample log file not found: $bulkFile"
}

$mapping = @"
{
  "mappings": {
    "properties": {
      "@timestamp": { "type": "date" },
      "serviceName": { "type": "keyword" },
      "environment": { "type": "keyword" },
      "level": { "type": "keyword" },
      "traceId": { "type": "keyword" },
      "spanId": { "type": "keyword" },
      "httpMethod": { "type": "keyword" },
      "endpoint": { "type": "keyword" },
      "status": { "type": "integer" },
      "durationMs": { "type": "integer" },
      "message": { "type": "text" }
    }
  }
}
"@

try {
    Invoke-RestMethod -Method Put -Uri "$OpenSearchUrl/$indexName" -ContentType "application/json" -Body $mapping | Out-Null
}
catch {
    Write-Host "Index may already exist. Continuing with bulk import."
}

Invoke-RestMethod -Method Post -Uri "$OpenSearchUrl/_bulk" -ContentType "application/x-ndjson" -InFile $bulkFile | Out-Null

Write-Host "Sample logs imported into index: $indexName"
Write-Host "Open Dashboards: http://localhost:5601"
Write-Host "Search example: traceId:\"trace-2026-elk-demo-001\""

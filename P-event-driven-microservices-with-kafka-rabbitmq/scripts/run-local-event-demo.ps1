param(
    [string]$QueueFile = ".\target\orders.events.jsonl"
)

$ErrorActionPreference = "Stop"

$targetDir = Split-Path -Parent $QueueFile
if ($targetDir -and -not (Test-Path -LiteralPath $targetDir)) {
    New-Item -ItemType Directory -Force -Path $targetDir | Out-Null
}

if (Test-Path -LiteralPath $QueueFile) {
    Remove-Item -LiteralPath $QueueFile -Force
}

$events = @(
    [ordered]@{
        eventId = "evt-1001"
        eventType = "OrderCreated"
        traceId = "trace-order-1001"
        orderId = "ORD-1001"
        amount = 2500
        currency = "INR"
    },
    [ordered]@{
        eventId = "evt-1002"
        eventType = "OrderCreated"
        traceId = "trace-order-1002"
        orderId = "ORD-1002"
        amount = 900
        currency = "INR"
    }
)

Write-Host ""
Write-Host "EVENT PRODUCER"
Write-Host "--------------"

foreach ($event in $events) {
    $json = $event | ConvertTo-Json -Compress
    Add-Content -LiteralPath $QueueFile -Value $json
    Write-Host "Published event: $($event.eventType) for $($event.orderId)"
}

Write-Host ""
Write-Host "MESSAGE BROKER"
Write-Host "--------------"
Write-Host "Events are stored in: $QueueFile"

Write-Host ""
Write-Host "EVENT CONSUMERS"
Write-Host "---------------"

$publishedEvents = Get-Content -LiteralPath $QueueFile | ForEach-Object { $_ | ConvertFrom-Json }

foreach ($event in $publishedEvents) {
    Write-Host "Payment Service consumed $($event.orderId), traceId=$($event.traceId)"
    Write-Host "Inventory Service consumed $($event.orderId), traceId=$($event.traceId)"
    Write-Host "Notification Service consumed $($event.orderId), traceId=$($event.traceId)"
}

Write-Host ""
Write-Host "EXPECTED LEARNING"
Write-Host "-----------------"
Write-Host "The producer does not call Payment, Inventory, or Notification directly."
Write-Host "Consumers read the event independently. This is the basic idea behind Kafka/RabbitMQ async communication."

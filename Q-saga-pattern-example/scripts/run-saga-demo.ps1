param(
    [switch]$FailureOnly
)

$ErrorActionPreference = "Stop"

function Write-Step {
    param(
        [string]$SagaId,
        [string]$Service,
        [string]$Action,
        [string]$Status
    )

    Write-Host ("{0,-16} | {1,-18} | {2,-24} | {3}" -f $SagaId, $Service, $Action, $Status)
}

Write-Host ""
Write-Host "SAGA DEMO"
Write-Host "---------"
Write-Host "Saga ID          | Service            | Action                   | Status"
Write-Host "-----------------|--------------------|--------------------------|----------"

if (-not $FailureOnly) {
    $sagaId = "saga-order-1001"
    Write-Step $sagaId "Order Service" "Create order" "PENDING"
    Write-Step $sagaId "Payment Service" "Take payment" "PAID"
    Write-Step $sagaId "Inventory Service" "Reserve stock" "RESERVED"
    Write-Step $sagaId "Order Service" "Confirm order" "CONFIRMED"
    Write-Host ""
}

$failedSagaId = "saga-order-1002"
Write-Step $failedSagaId "Order Service" "Create order" "PENDING"
Write-Step $failedSagaId "Payment Service" "Take payment" "PAID"
Write-Step $failedSagaId "Inventory Service" "Reserve stock" "FAILED"
Write-Step $failedSagaId "Payment Service" "Refund payment" "REFUNDED"
Write-Step $failedSagaId "Order Service" "Cancel order" "CANCELLED"

Write-Host ""
Write-Host "EXPECTED LEARNING"
Write-Host "-----------------"
Write-Host "Saga does not use one big database transaction."
Write-Host "Each service completes a local transaction."
Write-Host "When a later step fails, compensation steps make the business flow safe."

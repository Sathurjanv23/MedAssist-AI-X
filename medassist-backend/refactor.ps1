$ErrorActionPreference = "Stop"

$baseDir = "C:\Users\User\Desktop\Medassist AI X\medassist-backend\src\main\java\com\medassist"
$testDir = "C:\Users\User\Desktop\Medassist AI X\medassist-backend\src\test\java\com\medassist"

$mapping = @{
    # common
    "constants\AppConstants.java" = "common\constants\AppConstants.java"
    "exception\GlobalExceptionHandler.java" = "common\exception\GlobalExceptionHandler.java"
    "exception\BadRequestException.java" = "common\exception\BadRequestException.java"
    "exception\ResourceNotFoundException.java" = "common\exception\ResourceNotFoundException.java"
    "exception\FileException.java" = "common\exception\FileException.java"
    "exception\UnauthorizedException.java" = "common\exception\UnauthorizedException.java"
    "dto\common\ApiResponse.java" = "common\response\ApiResponse.java"
    "dto\common\ErrorResponse.java" = "common\response\ErrorResponse.java"

    # user
    "controller\UserController.java" = "user\controller\UserController.java"
    "controller\ProfileController.java" = "user\controller\ProfileController.java"
    "service\UserService.java" = "user\service\UserService.java"
    "service\impl\UserServiceImpl.java" = "user\service\impl\UserServiceImpl.java"
    "service\ProfileService.java" = "user\service\ProfileService.java"
    "service\impl\ProfileServiceImpl.java" = "user\service\impl\ProfileServiceImpl.java"
    "repository\UserRepository.java" = "user\repository\UserRepository.java"
    "repository\MedicalProfileRepository.java" = "user\repository\MedicalProfileRepository.java"
    "model\User.java" = "user\model\User.java"
    "model\MedicalProfile.java" = "user\model\MedicalProfile.java"
    "dto\request\ProfileUpdateRequest.java" = "user\dto\request\ProfileUpdateRequest.java"
    "dto\request\MedicalProfileRequest.java" = "user\dto\request\MedicalProfileRequest.java"
    "dto\response\UserResponse.java" = "user\dto\response\UserResponse.java"

    # auth
    "controller\AuthController.java" = "auth\controller\AuthController.java"
    "service\AuthService.java" = "auth\service\AuthService.java"
    "service\impl\AuthServiceImpl.java" = "auth\service\impl\AuthServiceImpl.java"
    "dto\request\LoginRequest.java" = "auth\dto\request\LoginRequest.java"
    "dto\request\RegisterRequest.java" = "auth\dto\request\RegisterRequest.java"
    "dto\request\RefreshTokenRequest.java" = "auth\dto\request\RefreshTokenRequest.java"
    "dto\response\AuthResponse.java" = "auth\dto\response\AuthResponse.java"

    # health
    "controller\HealthController.java" = "health\controller\HealthController.java"
    "controller\TimelineController.java" = "health\controller\TimelineController.java"
    "service\HealthService.java" = "health\service\HealthService.java"
    "service\impl\HealthServiceImpl.java" = "health\service\impl\HealthServiceImpl.java"
    "service\TimelineService.java" = "health\service\TimelineService.java"
    "service\impl\TimelineServiceImpl.java" = "health\service\impl\TimelineServiceImpl.java"
    "repository\HealthDataRepository.java" = "health\repository\HealthDataRepository.java"
    "repository\TimelineRepository.java" = "health\repository\TimelineRepository.java"
    "model\HealthData.java" = "health\model\HealthData.java"
    "model\HealthTimeline.java" = "health\model\HealthTimeline.java"
    "dto\response\HealthTwinResponse.java" = "health\dto\response\HealthTwinResponse.java"
    "scheduler\HealthCheckScheduler.java" = "health\scheduler\HealthCheckScheduler.java"

    # report
    "controller\ReportController.java" = "report\controller\ReportController.java"
    "service\ReportService.java" = "report\service\ReportService.java"
    "service\impl\ReportServiceImpl.java" = "report\service\impl\ReportServiceImpl.java"
    "repository\MedicalReportRepository.java" = "report\repository\MedicalReportRepository.java"
    "model\MedicalReport.java" = "report\model\MedicalReport.java"
    "dto\response\ReportResponse.java" = "report\dto\response\ReportResponse.java"

    # medicine
    "controller\MedicineController.java" = "medicine\controller\MedicineController.java"
    "service\MedicineService.java" = "medicine\service\MedicineService.java"
    "service\impl\MedicineServiceImpl.java" = "medicine\service\impl\MedicineServiceImpl.java"
    "repository\MedicineRepository.java" = "medicine\repository\MedicineRepository.java"
    "model\Medicine.java" = "medicine\model\Medicine.java"
    "dto\request\MedicineRequest.java" = "medicine\dto\request\MedicineRequest.java"
    "scheduler\MedicineReminderScheduler.java" = "medicine\scheduler\MedicineReminderScheduler.java"

    # ai
    "controller\AIController.java" = "ai\controller\AIController.java"
    "ai\AIService.java" = "ai\service\AIService.java"
    "ai\OllamaClient.java" = "ai\service\OllamaClient.java"
    "ai\ContextBuilder.java" = "ai\service\ContextBuilder.java"
    "ai\SafetyGuard.java" = "ai\service\SafetyGuard.java"
    "ai\OllamaModels.java" = "ai\model\OllamaModels.java"
    "model\AiChat.java" = "ai\model\AiChat.java"
    "repository\ChatRepository.java" = "ai\repository\ChatRepository.java"
    "dto\request\ChatRequest.java" = "ai\dto\request\ChatRequest.java"
    "dto\request\SymptomRequest.java" = "ai\dto\request\SymptomRequest.java"
    "dto\response\ChatResponse.java" = "ai\dto\response\ChatResponse.java"

    # ocr
    "ocr\OcrService.java" = "ocr\service\OcrService.java"
    "ocr\OcrModels.java" = "ocr\model\OcrModels.java"

    # doctor
    "controller\DoctorController.java" = "doctor\controller\DoctorController.java"
    "model\Doctor.java" = "doctor\model\Doctor.java"
    "model\Consultation.java" = "doctor\model\Consultation.java"
    "repository\DoctorRepository.java" = "doctor\repository\DoctorRepository.java"
    "repository\ConsultationRepository.java" = "doctor\repository\ConsultationRepository.java"

    # admin
    "controller\AdminController.java" = "admin\controller\AdminController.java"

    # notification
    "notification\EmailService.java" = "notification\email\EmailService.java"
    "websocket\WebSocketConfig.java" = "notification\websocket\WebSocketConfig.java"
    "websocket\WebSocketNotificationService.java" = "notification\websocket\WebSocketNotificationService.java"
    "websocket\NotificationPayload.java" = "notification\websocket\NotificationPayload.java"

    # storage
    "storage\S3StorageService.java" = "storage\service\S3StorageService.java"

    # audit
    "audit\AuditService.java" = "audit\service\AuditService.java"
    "model\AuditLog.java" = "audit\model\AuditLog.java"
    "repository\AuditLogRepository.java" = "audit\repository\AuditLogRepository.java"
}

# 1. Create package replacements mapping
$packageReplacements = @{}
foreach ($key in $mapping.Keys) {
    $oldPackage = "com.medassist." + ($key -replace '\\[^\\]+\.java$', '') -replace '\\', '.'
    $newPackage = "com.medassist." + ($mapping[$key] -replace '\\[^\\]+\.java$', '') -replace '\\', '.'
    
    $className = ($key -split '\\')[-1] -replace '\.java$', ''

    # Handle cases where files were at root of medassist (which none of these are, but just in case)
    if ($oldPackage -eq "com.medassist.") { $oldPackage = "com.medassist" }

    $oldImport = "${oldPackage}.${className}"
    $newImport = "${newPackage}.${className}"

    if ($oldImport -ne $newImport) {
        $packageReplacements[$oldImport] = $newImport
    }
}

Write-Host "Moving files..."
# 2. Move files
foreach ($key in $mapping.Keys) {
    $sourcePath = Join-Path $baseDir $key
    $destPath = Join-Path $baseDir $mapping[$key]
    
    if (Test-Path $sourcePath) {
        $destDir = Split-Path $destPath
        if (-not (Test-Path $destDir)) {
            New-Item -ItemType Directory -Force -Path $destDir | Out-Null
        }
        Move-Item -Path $sourcePath -Destination $destPath -Force
        Write-Host "Moved $key -> $($mapping[$key])"
    } else {
        Write-Host "Warning: $sourcePath does not exist!"
    }
}

Write-Host "Updating imports and packages..."
# 3. Update contents
$allJavaFiles = Get-ChildItem -Path $baseDir, $testDir -Recurse -Filter "*.java"

foreach ($file in $allJavaFiles) {
    $content = Get-Content $file.FullName -Raw
    $originalContent = $content

    # Update package declaration based on folder structure
    $relativePath = $file.FullName.Substring($baseDir.Length + 1)
    if ($file.FullName.StartsWith($testDir)) {
        $relativePath = $file.FullName.Substring($testDir.Length + 1)
    }

    $expectedPackage = "com.medassist." + ($relativePath -replace '\\[^\\]+\.java$', '') -replace '\\', '.'
    if ($expectedPackage -eq "com.medassist.com.medassist") { $expectedPackage = "com.medassist" } # fallback for root test files
    if ($file.FullName.StartsWith($testDir) -and $relativePath -notmatch '\\') {
        $expectedPackage = "com.medassist"
    }
    
    $content = $content -replace '(?m)^package\s+com\.medassist.*?;', "package ${expectedPackage};"

    # Update imports
    foreach ($oldImport in $packageReplacements.Keys) {
        $newImport = $packageReplacements[$oldImport]
        # Replace explicit import
        $content = $content -replace "(?m)^import\s+${oldImport};", "import ${newImport};"
        # Also replace fully qualified usages in code if any exist
        $content = $content -replace "\b$($oldImport -replace '\.', '\.')\b", $newImport
    }

    # Clean up empty import lines if any
    if ($content -cne $originalContent) {
        Set-Content -Path $file.FullName -Value $content -Encoding UTF8
    }
}

Write-Host "Cleaning up empty directories..."
# 4. Cleanup old empty directories
$oldDirs = @("controller", "service\impl", "service", "repository", "model", "dto\request", "dto\response", "dto\common", "dto", "exception", "constants", "ai", "ocr", "doctor", "admin", "notification", "storage", "audit", "scheduler", "websocket")

foreach ($dir in $oldDirs) {
    $fullPath = Join-Path $baseDir $dir
    if (Test-Path $fullPath) {
        $isEmpty = @(Get-ChildItem -Path $fullPath -Recurse -File).Count -eq 0
        if ($isEmpty) {
            Remove-Item -Path $fullPath -Recurse -Force
        }
    }
}

Write-Host "Refactoring complete."

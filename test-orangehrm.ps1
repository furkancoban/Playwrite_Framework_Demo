# OrangeHRM Site Verification Script
Write-Host ""
Write-Host "Starting OrangeHRM site verification..." -ForegroundColor Cyan
Write-Host ""

$url = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login"

try {
    # Fetch the page
    Write-Host "Fetching page from: $url" -ForegroundColor Yellow
    $response = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 30
    
    Write-Host "Page loaded successfully!" -ForegroundColor Green
    Write-Host "   Status Code: $($response.StatusCode)" -ForegroundColor Gray
    Write-Host "   Content Length: $($response.Content.Length) bytes" -ForegroundColor Gray
    Write-Host ""
    
    # Parse HTML content
    $html = $response.Content
    
    # Check for input[name="username"]
    Write-Host "Checking for input with name='username'..." -ForegroundColor Yellow
    $usernamePattern = 'name="username"'
    if ($html -match $usernamePattern) {
        Write-Host "FOUND: input[name='username'] EXISTS" -ForegroundColor Green
    } else {
        Write-Host "NOT FOUND: input[name='username']" -ForegroundColor Red
    }
    Write-Host ""
    
    # Check for input[name="password"]
    Write-Host "Checking for input with name='password'..." -ForegroundColor Yellow
    $passwordPattern = 'name="password"'
    if ($html -match $passwordPattern) {
        Write-Host "FOUND: input[name='password'] EXISTS" -ForegroundColor Green
    } else {
        Write-Host "NOT FOUND: input[name='password']" -ForegroundColor Red
    }
    Write-Host ""
    
    # Extract all input elements
    Write-Host "Extracting all input elements..." -ForegroundColor Cyan
    Write-Host ("=" * 80) -ForegroundColor Gray
    
    $inputPattern = '<input[^>]*>'
    $inputs = [regex]::Matches($html, $inputPattern, [System.Text.RegularExpressions.RegexOptions]::IgnoreCase)
    
    Write-Host "Found $($inputs.Count) input elements:" -ForegroundColor White
    Write-Host ""
    
    $inputIndex = 1
    foreach ($input in $inputs) {
        Write-Host "Input $($inputIndex):" -ForegroundColor Cyan
        
        # Extract attributes
        $inputHtml = $input.Value
        Write-Host "  Raw: $($inputHtml.Substring(0, [Math]::Min(120, $inputHtml.Length)))" -ForegroundColor Gray
        
        # Name
        if ($inputHtml -match 'name="([^"]*)"') {
            Write-Host "  Name: '$($matches[1])'" -ForegroundColor Green
            Write-Host "    -> Selector: input[name='$($matches[1])']" -ForegroundColor Yellow
        }
        
        # Type
        if ($inputHtml -match 'type="([^"]*)"') {
            Write-Host "  Type: '$($matches[1])'" -ForegroundColor Green
        }
        
        # ID
        if ($inputHtml -match '\sid="([^"]*)"') {
            Write-Host "  ID: '$($matches[1])'" -ForegroundColor Green
            Write-Host "    -> Selector: #$($matches[1])" -ForegroundColor Yellow
        }
        
        # Class
        if ($inputHtml -match 'class="([^"]*)"') {
            $classes = $matches[1] -replace '\s+', '.'
            Write-Host "  Class: '$($matches[1])'" -ForegroundColor Green
            Write-Host "    -> Selector: .$classes" -ForegroundColor Yellow
        }
        
        # Placeholder
        if ($inputHtml -match 'placeholder="([^"]*)"') {
            Write-Host "  Placeholder: '$($matches[1])'" -ForegroundColor Green
        }
        
        Write-Host ""
        $inputIndex++
    }
    
    Write-Host ("=" * 80) -ForegroundColor Gray
    
    # Extract form HTML (if any)
    Write-Host ""
    Write-Host "Extracting login form HTML..." -ForegroundColor Cyan
    $formPattern = '(?s)<form[^>]*>.*?</form>'
    if ($html -match $formPattern) {
        $formHtml = $matches[0]
        $truncatedForm = $formHtml.Substring(0, [Math]::Min(1500, $formHtml.Length))
        Write-Host $truncatedForm -ForegroundColor Gray
        if ($formHtml.Length -gt 1500) {
            Write-Host ""
            Write-Host "... (truncated, total length: $($formHtml.Length) chars)" -ForegroundColor DarkGray
        }
    } else {
        Write-Host "No <form> tag found in HTML" -ForegroundColor Yellow
    }
    
    # Summary
    Write-Host ""
    Write-Host ""
    Write-Host "=== SUMMARY ===" -ForegroundColor Cyan
    Write-Host ("=" * 80) -ForegroundColor Gray
    Write-Host "Site is accessible: YES" -ForegroundColor Green
    Write-Host "Page loads successfully: YES" -ForegroundColor Green
    Write-Host "Status Code: $($response.StatusCode)" -ForegroundColor Green
    
    $hasUsername = $html -match 'name="username"'
    $hasPassword = $html -match 'name="password"'
    
    if ($hasUsername) {
        Write-Host "input[name='username'] exists: YES" -ForegroundColor Green
    } else {
        Write-Host "input[name='username'] exists: NO" -ForegroundColor Red
    }
    
    if ($hasPassword) {
        Write-Host "input[name='password'] exists: YES" -ForegroundColor Green
    } else {
        Write-Host "input[name='password'] exists: NO" -ForegroundColor Red
    }
    
    Write-Host ("=" * 80) -ForegroundColor Gray
    
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host $_.Exception -ForegroundColor Red
}

Write-Host ""
Write-Host "Test completed!" -ForegroundColor Green
Write-Host ""

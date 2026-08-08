$headers = @{
    "Content-Type" = "application/json"
}

$body = @{
    firstName = "Sathurjan"
    lastName = "Vijithakumarasena"
    email = "sathu20030303@gmail.com"
    password = "Sathu@2003"
    phoneNumber = "0752650480"
    dateOfBirth = "2024-01-29"
    gender = "MALE"
    bloodGroup = "B+"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method Post -Headers $headers -Body $body

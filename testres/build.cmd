@echo off
rem Wrapper for restricted PowerShell execution policy (same as scripts\check-rules.cmd).
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0build.ps1" %*

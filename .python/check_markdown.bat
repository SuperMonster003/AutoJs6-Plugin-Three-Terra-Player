@echo off
setlocal
py "%~dp0generate_markdown.py" --check %*
exit /b %errorlevel%

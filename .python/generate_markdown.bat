@echo off
setlocal
py "%~dp0generate_markdown.py" %*
exit /b %errorlevel%

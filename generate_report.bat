@echo off
echo ========================================================
echo Running Tests...
echo ========================================================
call mvn test

echo.
echo ========================================================
echo Packaging Reports for GitHub Pages...
echo ========================================================
:: Create docs folder if it doesn't exist
if not exist docs mkdir docs

:: Copy Surefire reports to docs folder
xcopy /E /I /Y target\surefire-reports\* docs\

:: Inject the Utterances feedback widget script into the HTML files
echo Injecting Feedback Widget...

powershell -Command "(Get-Content docs\emailable-report.html) -replace '</body>', '<script src=\"https://utteranc.es/client.js\" repo=\"sekhlucifer/Testing\" issue-term=\"pathname\" theme=\"github-light\" crossorigin=\"anonymous\" async></script></body>' | Set-Content docs\emailable-report.html"

powershell -Command "(Get-Content docs\index.html) -replace '</body>', '<script src=\"https://utteranc.es/client.js\" repo=\"sekhlucifer/Testing\" issue-term=\"pathname\" theme=\"github-light\" crossorigin=\"anonymous\" async></script></body>' | Set-Content docs\index.html"

echo.
echo ========================================================
echo Done! 
echo The 'docs' folder is ready. 
echo Now run: git add . ^&^& git commit -m "Update report" ^&^& git push
echo ========================================================

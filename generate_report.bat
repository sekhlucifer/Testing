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

:: Copy the beautiful Premium Report as the main index file for GitHub Pages
copy /Y target\surefire-reports\Premium-Report.html docs\index.html

echo.
echo ========================================================
echo Done! 
echo The 'docs' folder is ready. 
echo Now run: git add . ^&^& git commit -m "Update report" ^&^& git push
echo ========================================================

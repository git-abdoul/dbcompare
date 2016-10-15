@echo OFF

if "%DBCOMPARE_HOME%"=="" goto ERROR1

set DBCOMPARE_LIB=%DBCOMPARE_HOME%\lib

:MAIN
set CLASSPATH=^
%DBCOMPARE_HOME%\resources;^
%DBCOMPARE_LIB%\*

goto END

:ERROR1
  echo ERROR! Please set DBCOMPARE_HOME 
  echo.  
  echo ============================================
  echo.
goto END

:END
@echo OFF

setlocal   
   
cd ../

set DBCOMPARE_HOME=%cd%
set JAVA_HOME=%DBCOMPARE_HOME%\bin\jre

call %DBCOMPARE_HOME%\bin\classpath.bat

set VM_ARGS=-classpath %CLASSPATH% -Dhome=%DBCOMPARE_HOME% -Xms512m -Xmx1024m 

%JAVA_HOME%\bin\java %VM_ARGS% lu.sgbt.dbcompare.StartComparator SETUP %*

goto END

:ERROR1
  echo ERROR! Please set DBCOMPARE_HOME 
  echo.  
  echo ============================================
  echo.
goto END

:END
echo END.
PAUSE
endlocal 
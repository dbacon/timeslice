@echo off

rem TODO: support params - setting port, jvm - for now, hard-coded.

set port=14099

rem Crazy, but works. Somebody else write windows launcher, please.
set cmdpath=%0
set bindir=%cmdpath:~0,-11%"
set topdir=%bindir:~0,-5%"
set webdir=%topdir%\web

set javahome=c:\Program Files (x86)\JDK\1.6
rem set javahome=C:\Program Files\Java\jre6
rem do some cascading/detection here ?

start "timeslice" /I /B /D"%topdir%\.." ^
  "%javahome%\bin\javaw.exe" ^
  -cp "%topdir%/lib/*;" ^
  com.enokinomi.timeslice.web.launcher.Driver ^
    --web-root "%webdir%" ^
    --port %port% ^
    --acl "%topdir%\..\.timeslice.acl" ^
    --data "%topdir%\..\data\default-01"

rem pause


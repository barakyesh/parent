@echo off
cd %1
cd Vagrant
:menu
cls
echo.
echo Select Vagrant Options:
echo ==========================================
echo.
echo 1. Up
echo 2. Suspend
echo 3. Destroy
echo 4. Destroy ^& Up
echo.
echo ==========================================:

set /p Choice= Please choose option from above:

goto :Option-%Choice%

:Option-1
call vagrant up
goto End
:Option-2
call vagrant suspend
goto End
:Option-3
call vagrant destroy
goto End
:Option-4
call vagrant destroy
call vagrant up
goto End


:End
pause
Exit
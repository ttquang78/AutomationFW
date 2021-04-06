cmd /c "taskkill /f /im explorer.exe && start explorer"
cmd /c "taskkill /F /IM iexplore.exe"
wmic product where name="PingOne Extension 64-bit" call uninstall/nointeractive
D:
cd D:\KMS\Projects\PingIdentity\AutomationGit\test\BE_Automation
mvn clean test -PBESikuli -DfixedBuildPath=
pause
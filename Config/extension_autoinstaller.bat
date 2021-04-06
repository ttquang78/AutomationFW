REM -Create shortcut to this batch file
REM -Make shortcut Run as Administrator (Right-click on shortcut > Properties > Shortcut Tab > Advanced > check Run as Administrator)
REM -Set change notification to lowest level

bitsadmin.exe /transfer "Downloadin IE extension..." https://s3-us-west-2.amazonaws.com/ping-browser-extension/aebjagkkobajpjpacabnojeplklmijjh/ie/PingOne-Extension-x64.msi C:\Users\localadmin\Downloads\IE-P1Extension.msi
msiexec /i C:\Users\localadmin\Downloads\IE-P1Extension.msi /quiet /qn /norestart /log C:\Users\localadmin\Downloads\install.logs
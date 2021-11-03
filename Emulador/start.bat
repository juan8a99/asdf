@echo off
title Emulador Busta 3.9
:loop
"C:\Program Files (x86)\Java\jre7\bin\java.exe" -jar -Xmx1000m -Xms1000m Gavril.jar
goto loop
PAUSE
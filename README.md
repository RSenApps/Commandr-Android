Commandr for Google Now (Android)
================

Add commands to Google Now, no root required!

Website: http://Commandr.RSenApps.com
Play Store: https://play.google.com/store/apps/details?id=com.RSen.Commandr
Donate: Paypal Button on main site http://www.RSenApps.com

Contributing
================

Translate: http://www.getlocalization.com/commandr

Add a built-in command:
 - Please first look at the voting command screen in-app to see what commands people want to be added...
 - In src/com/RSen/Commandr/builtincommands there are many examples of different types of commands duplicate one of them to use as a template
 - In src/com/RSen/Commandr/core/MostWantedCommands add your own command to the commands array
 - pull request so I can add it!
 
Fix an issue with how commands are intercepted:
 - For commands without the note to self requirement see MyAccessibilityService and for legacy support see NoteToSelfActivity (both in core)
 - These commands are passed to MostWantedCommands and TaskerCommands
 
Add better integration with Tasker:
 - See TaskerCommands in core
 
Please email me at RSenApps@gmail.com if you have any questions!

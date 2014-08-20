![alt tag](http://commandr.rsenapps.com/commandrfeature.jpg)
Commandr for Google Now (Android)
================

Add commands to Google Now, no root required!

Website: http://Commandr.RSenApps.com

Play Store: https://play.google.com/store/apps/details?id=com.RSen.Commandr

XDA: http://forum.xda-developers.com/android/apps-games/app-commandr-google-t2806098/post53926039

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

Acknowledgements
================

Special thanks to Sky Kelsey and the Apptentive Team as well as Adriano Loiacono for the graphic design work!

Commandr would not be possible without the following open source libraries:
 - Card Library by gabrielmariotti http://www.github.com/gabrielmariotti/cardslib
 - ListViewAnimations by nhaarman http://www.github.com/nhaarman/ListViewAnimations
 - AndroidStaggeredGrid by etsy  http://www.github.com/etsy/AndroidStaggeredGrid
 - SystemBarTint by jgilfelt http://www.github.com/jgilfelt/SystemBarTint
 - Android Donations Lib by dschuermann http://www.github.com/dschuermann/android-donations-lib

License
================
The MIT License (MIT)

Copyright (c) 2014 RSenApps Inc.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

Contributing to the Open Data Plattform
=======================================

There are many ways in which you can contribute to the Open Data Platform:

Raise Issues
------------
If you spot a bug or wish for a new feature, use GitHub Issues to report
them. Ideally attach the issue to the corresponding component, e.g. 
gd-edit-portlet. If you are unsure just file the bug in the relevant parent
project, e.g. GovData.

Submit a pull request
---------------------
Even better: You can fix a bug or implement a feature. In that case, follow
the [fork, branch, pull request pattern](https://help.github.com/articles/using-pull-requests): 
create a fork of the corresponding, in your repo create a branch to work on.
Name your branch "bug/feature - issue-number - topic", e.g. feature-26-html-description.
Finally, create a pull request to have your edits integrated into the master branch.

General process
---------------
As explained in the main Readme, the Opendata Platform is a set of plugins and 
libraries that can easily be combined to build or be included in a JavaEE open 
data portal. So please bear in mind that the development of one component 
influences all including parent projects. Accordingly, your changes will be 
merged in the master branch quickly only they A) do not disrupt current behavior or B)
can easily be toggled, i.e. enabled and disabled in a configuration file.

Formatting conventions
----------------------
Please make sure you edit Java code with the current formatting scheme, 
otherwise merging becomes tricky. Use the default Eclipse Java formatting
style with two tweaks: 110 characters per line, indent with 2 spaces instead 
of tabs. You can find the configuration file that you can load in eclipse in 
[formatting scheme](src/eclipse/seitenbauCodeConventions.xml). [Not coding with Eclipse?](http://blogs.operationaldynamics.com/andrew/software/java-gnome/eclipse-code-format-from-command-line)
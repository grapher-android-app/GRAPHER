# GRAPHER

Welcome to GRAPHER, the graph drawing Android app!

# Description

This is an Android app for graph drawing and editing, and comes with several built-in algorithms
that you can run on your graphs. You can share them and add them to clipboard in Metapost format.

# Table of Contents

* Installation
* Origin
* Usage
* Changes from the original app
* Missing features from the original app
* Where will the app go next?
* Licence

# Installation
To contribute or alter the project you can fork the project by using Git. The project is built
using Gradle, so any modern IDE will easily automatically build it for you. We used Android Studio,
which we recommend for easy testing and multiple way to view the project structure.

# Origin
The Android app for graph drawing and editing known as Grapher was designed and developed by
Algoritmegruppen at UiB in the period of 2012-2015. After some time without maintenance the app
became outdated and lost functionality due to changes in the ever evolving world of Android.

That's why the app was picked up again as a INF219 project to restore the lost
functionality and make it compatible with new, modern Android devices. This is the home to the
new and improved version, made clear by the now capitalized name.

# Usage

The graph manipulation is done using two modes: Node and Edge. Switching between them is done by
simply flipping the switch on the navigation bar located at the bottom of the screen. You know which
mode you are currently in by looking at the text next to the switch which will either read "Node" or
"Edge".

* Node Mode
    * Used to place and move nodes around the workspace.
    * Tapping an empty space will create a new node there,
    and by dragging it you can move it around.
    * Double tapping a node will delete it.
    * Tapping an already existing node will "mark" it. This is used to signal which nodes
    to perform specific algorithms on in the graph (ex. Max Flow). They will be "unmarked" if
    you switch to edge mode.

* Edge Mode
    * Used to place edges between existing nodes.
    * Tapping two nodes will add an edge between them or remove one if there already exists one.

* Copying and Sharing the Graph
    * The app supports the ability to copy the graph information to clipboard, as well as sharing it
    with the in-built Android sharing mechanisms. Currently the only supported format is Metapost.
    
* Algorithms
    * You will find a wide variety of algorithms that you can run on your graph in the menu located
    on the navigation bar.
    * When an algorithm has been successfully run a toast will appear to tell you if there was a
    result or not.
    * Be warned, to effectively use any of the algorithms it is expected that you, the user, to
    already knows what they are meant to do. If you do not, then the result will not make much
    sense to you. An example would be that if you want to use the Max Flow algorithm, then you
    should know that you need to select two nodes beforehand, and what "flow" in graphs mean.
    
* Normalizing
    * If you ever find the graph you have made to be confusing or unnecessarily spread out or
    squished together, then you can use the normalize feature built into the app.
    * Pressing the "Normalize" button on the navigation bar or simply by shaking your phone will
    result in the graph's nodes spacing themselves out evenly. This will result in the graph looking
    like the most simple version of itself.

# Changes from the original app

* The app has been rewritten from Java into Kotlin
* Implementation of the external graph library JGraphT was changed to work with the newer version
* GitHub Actions are used for automatic building and deployment to Google Play using workflows
* There is now a navigation bar that contains all actions you can do that does not directly
relate to drawing or moving the graph manually.
    
# Missing features from the old app

The original app had some more features that we sadly have had the chance to adapt yet due to the
time constraints on the project. The functionality that is lacking but can easily be implemented
by having the time to translate it:

* The remaining algorithms.
* Tikz sharing format support.
* Ability to save and load graphs when closing and opening the app.

As specified earlier, these features can easily be reimplemented by taking the time to translate
them from the original code.

# Where will the app go next?

The app still has quite a few shortcomings even when looking away from the missing features. Some
ideas for future functionality that would be great quality of life features would be:

* In-built tutorial for how to use the app
* Explanations for all the algorithms the app provides including how to use them in the app.
* Ability to save the graph as an image.
* Color scheme changes based on light/dark mode.

There are also other features that would benefit the app but would also increase its complexity
drastically:

* Support for weighted and directed graphs
* Support for more than one graph at the time

Overall, the app is currently a great tool for representation and manipulation of graphs but can
be improved even more in the future.

# Credits
The original application and its authors are to credit for the main bulk of features that we simply
had to translate, and for outlining how the app would function in the first place.
Here is the link to the original project:
https://github.com/pgdr/Grapher

# Licence
The project is released as GPLv3
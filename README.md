# Getting an Application Running on Apple Silicon M1 

This document was last updated on December 23rd, 2020.

This is a trivial application that I wanted to see work under my Apple Silicon M1 MacBook Pro with 16GBs of RAM.

* I was able to use the [OpenJDK port from Microsoft](https://github.com/microsoft/openjdk-aarch64/releases/tag/16-ea+10-macos) or the [Azul Systems']() build to make this work. I had better luck with [AzulSystems' OpenJDK 15](https://www.azul.com/downloads/zulu-community/?package=jdk). 

* I was able to use [the IntelliJ IDEA beta that supports M1](https://youtrack.jetbrains.com/issue/JBR-2526); there's a link on that page that you can download 
# Virtual board

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white) ![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)  ![Thymeleaf](https://img.shields.io/badge/Thymeleaf-%23005C0F.svg?style=for-the-badge&logo=Thymeleaf&logoColor=white) ![HTML5](https://img.shields.io/badge/html5-%23E34F26.svg?style=for-the-badge&logo=html5&logoColor=white) ![CSS3](https://img.shields.io/badge/css3-%231572B6.svg?style=for-the-badge&logo=css3&logoColor=white) 

![](https://tokei.rs/b1/github/marcinsarnecki/Virtual-Board-web-app)

## Introduction 

A web application for  a magnet board. This is my first big project (simple html/css frontend, java spring backend, postgresql database). I made it during summer practice for a call center company. Names on screenshots are randomly generated. 

## Table of Content

[TOC]



## Motivation

The idea of this project was to create a tool for employees to record the products they managed to sell in order to motivate other workers.  

## How it works?

There are regular users, admins and projects. One user can be a member of many projects, one project can contain many users. Regular users (after logging in) on the main page see projects in which they are members. For every project, they see members and magnets with descriptions. They can add magnets and remove last magnet added.
<p align="center">
  <img src="https://i.imgur.com/kTAz6En.png" alt="regular user's main page">
  <i>regular user's main page</i>
</p>

<p align="center">
  <img src="https://i.imgur.com/ReMX5jO.png" alt="regular user can join to anew project by adding first magnet on this page">
  <i>regular user can join to anew project by adding first magnet on this page</i>
</p>

Admins can choose which users they observe on the main page (their choices are saved in database). They can edit magnets' descriptions. 

<p align="center">
  <img src="https://i.imgur.com/tHXt1HS.png" alt="list of regular users to observe from admin's perspective">
  <i>list of regular users to observe from admin's perspective</i>
</p>

<p align="center">
  <img src="https://i.imgur.com/5oK0DC4.png" alt="magnets' descriptions edit page">
  <i>magnets' descriptions edit page</i>
</p>

Of course admins can also delete users/projects, create new projects, or promote regular users to admins (that's how you can create new admins, first admin is already added in application's code with username root and password 2aE70gvt, if you want to change it, go to 59th line of code in SpringProjectApplication class)

<p align="center">
  <img src="https://i.imgur.com/XCR6jUh.png" alt="panel for adding/deleting projects, deleting/promoting users">
  <i>panel for adding/deleting projects, deleting/promoting users</i>
</p>

<p align="center">
  <img src="https://i.imgur.com/bpFaw2R.png" alt="Admin on the man page see users whom he decided to observe">
  <i>Admin on the man page see users whom he decided to observe</i>
</p>

All boards are automatically reseted every workday morning. During reset, application generates boards' images and saves them as png files. Names of the image files are like 'project name + current date and time'.

## Installation

Before you run this application, you need to install PostgreSQL database (go to https://www.postgresql.org/download/). Once you have it installed, create database for our application. For example, you can do this in postgresql console with:
```sh
$ CREATE DATABASE boards;
```
Then you need to specify database url and your postgresql username with password in application.yml file. For example, it can looks like this:
```yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/boards
    username: postgres
    password: 12345
```
Then build jar file with Maven
```sh
$ mvn package
```
Then run jar file
```sh
$ java -jar SpringProject-0.0.1-SNAPSHOT.jar
```
Now open browser and connect with url localhost:8080

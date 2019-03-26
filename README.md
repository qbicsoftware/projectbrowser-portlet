# ProjectBrowser Portlet

[![Build Status](https://travis-ci.com/qbicsoftware/projectbrowser-portlet.svg?branch=development)](https://travis-ci.com/qbicsoftware/projectbrowser-portlet)[![Code Coverage]( https://codecov.io/gh/qbicsoftware/projectbrowser-portlet/branch/development/graph/badge.svg)](https://codecov.io/gh/qbicsoftware/projectbrowser-portlet)

ProjectBrowser Portlet, version 1.9.1 - Browse and manage biomedical projects

## Author
Created by 
* Christopher Mohr (christopher.mohr@qbic.uni-tuebingen.de) 
* David Wojnar

## Description

The portlet ProjectBrowser offers functionality to view, manage and edit projects that are stored in an [openBIS](https://wiki-bsse.ethz.ch/display/bis/Home) instance. Users may download project-specific data or run analysis pipelines on the available datasets. Currently the workflow system gUSE is supported by ProjectBrowser. An interface to [Nextflow](https://www.nextflow.io/) is currently **under development**. 

For further details please refer to:

> Mohr, C., Friedrich, A., Wojnar, D., Kenar, E., Polatkan, A. C., Codrea, M. C., ... & Nahnsen, S. (2018). qPortal: A platform for data-driven biomedical research. PloS one, 13(1), e0191603.


## How to Install

If you want to use the ProjectBrowser portlet in your local Liferay instance do the following:
* clone the git repository
* run **mvn clean package** in the directory


The generated *\*.war* file will be located in: 
> /path/to/git/projectbrowser-portlet/target/

Copy the file **projectbrowser-portlet-{version}.war** in the *deploy* folder of your Liferay instance.

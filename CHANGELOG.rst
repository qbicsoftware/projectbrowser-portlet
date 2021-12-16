==========
Changelog
==========

1.12.5 (2021-12-16)
-------------------

**Dependencies**

* ``org.apache.logging.log4j:log4j-core:2.15.0`` -> ``2.16.0`` (addresses CVE-2021-45046)

* ``org.apache.logging.log4j:log4j-api:2.15.0`` -> ``2.16.0`` (addresses CVE-2021-45046)

1.12.4 (2021-12-14)
-------------------

**Dependencies**

* Add old repositories for dependency resolvement

* Bump `commons-io:commons-io:2.4 -> 2.11.0`

* Bump `com.github.lookfirst:sardine:5.8 -> 5.10`

* Bump `com.github.vlsi.mxgraph:jgraphx:3.9.8.1 -> 4.2.2`

* Bump `org.docx4j:docx4j:3.3.2 -> 6.1.2`

* Bump `org.apache.ant:ant:1.10.1 -> 1.10.11`

* Bump `org.apache.httpcomponents:httpmime:4.3.1 -> 4.5.13`

1.12.3 (2021-12-13)
-------------------

**Dependencies**


* ``org.apache.logging.log4j:log4j-core:2.14.0`` -> ``2.15.0`` (addresses CVE-2021-44228)

* ``org.apache.logging.log4j:log4j-api:2.14.0`` -> ``2.15.0``

1.12.2 (2021-10-29)
-------------------

**Dependencies**

* com.vaadin 7.7.8 -> 7.7.28 (addresses CVE-2021-37714)

1.12.1 (2021-06-16)
-------------------

**Fixed**

* Don't auto-update experimental design of a selected project if changes are recent, giving openBIS time to index samples

1.12.0 (2021-06-07)
-------------------

**Added**

* Support for new person database structure

1.11.4 (2021-03-22)
-------------------

**Fixed**

* Due to workload reasons, multi-downloads of raw data have been disable. Please use [qPostman](https://github.com/qbicsoftware/postman-cli)
* Navigating to a different project now correctly clears the download cache

1.11.2 (2020-11-25)
-------------------

**Fixed**

* Graphics for DNA, RNA etc. are now consistently displayed in the interactive sample graph.
* Add changelog

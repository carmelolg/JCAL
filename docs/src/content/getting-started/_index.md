+++
title = "Getting started"
description = ""
weight = 2
+++

## Static installation

#### Download the jar file

[Here](https://github-registry-files.githubusercontent.com/602769865/2584ac00-c1d5-11ed-8a8a-c6be6b85a305?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAIWNJYAX4CSVEH53A%2F20230313%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20230313T183826Z&X-Amz-Expires=300&X-Amz-Signature=7142042570b31db161abdffa45c01a4913fa2d46580fc3e1cb22c1ffcef5566f&X-Amz-SignedHeaders=host&actor_id=0&key_id=0&repo_id=602769865&response-content-disposition=filename%3Djcal-1.0.0.alpha.jar&response-content-type=application%2Foctet-stream) the jar file.

#### Steps

- Import the jar as static import on your Java project
- Include on your build path the jar.
- Use it and/or try the [basic settings](../basic-settings/).


## Maven projects

### Github Maven Repository

The [following guide](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-to-github-packages) helps you to install maven packages directly from Github.

Here the snippet to put on your `pom.xml`

{{< code lang="xml" file="getting-started/dependency.xml">}}{{< /code >}}

### Official Maven repository

<span class="text-danger">*Not available yet*</span>
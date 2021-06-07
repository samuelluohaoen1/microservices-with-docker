# Summary

**classpath:** `se.magnus.microservices.api`

This module demonstrates the idea of **API management** of the Microservice Architecture.
Unlike most other modules where one module corresponds to a component in our microservice landscape, 
this module is a dependency module shared by all the components in our microservice landscape.
If you inspect the `build.gradle` files of all the modules in the `microservices` module you'll find
that they all have this module as a dependency.


# To study or review:

* Packages under `se.magnus.api.core` all display basically the same idea. 
* The `product` package is commented. Go there if you want to learn about API management.
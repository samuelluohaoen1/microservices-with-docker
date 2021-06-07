# Summary

**classpath:** `se.magnus.microservices.product-composite-service`

This module is implements a component that aggregates the result of other components in our microservice
landscape by making RESTful requests to the APIs of each of those components.

Formally, this module implements the "Edge Server" design pattern.

The implementation of the composite services is divided into two parts: 
1. An integration component that handles the outgoing HTTP requests to 
the core services.
2. The composite service implementation itself. 

The main reason for this division of responsibility is that it 
simplifies automated unit and integration testing; that is, 
we can test the service implementation in isolation by replacing 
the integration component with a mock.

# To study or review:

* There are only two classes which are all under `se.magnus.microservices.composite.product.services`.
  They are both commented.
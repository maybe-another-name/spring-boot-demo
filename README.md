# Overview

Demonstrates two web end-points, with one calling the other

# Key setup

Best resources I have seen (there are many poor ones): https://lightbend.github.io/ssl-config/CertificateGeneration.html#generating-x-509-certificates

Short & sweet snippet is found in `demo-b/src/main/resources/create_signed.sh`

This will create a bunch of files. The key ones are `localhost.jks`, `localhost-trust.jks`, and `client.jks`.

# Environment variables for security

Point this to where-ever you created your certs

> export CERTS_PATH=...

# Other environment variables
Usually, spring makes everything uppercase, and replaces special characters with underscores.  However, this isn't consistent.

Here is a problematic example demonstrating the inconsistency:

>management.endpoints.web.base-path=/
>management.endpoints.web.path-mapping.prometheus=metrics

are replaced by:

>export MANAGEMENT_ENDPOINTS_WEB_BASE_PATH=/
>export MANAGEMENT_ENDPOINTS_WEB_PATHMAPPING_PROMETHEUS=metrics

Notice the dash is in one instance replaced by an underscore, and in the other, is just removed.
(Based on spring actuator starter 2.4.10)

# Running it

mvn spring-boot:run --projects demo-a

mvn spring-boot:run --projects demo-b

You should see that demo-b was able to fetch the resource offered up by demo-a.

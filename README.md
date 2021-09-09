# Overview

Demonstrates two web end-points, with one calling the other

# Key setup

Best resources I have seen (there are many poor ones): https://lightbend.github.io/ssl-config/CertificateGeneration.html#generating-x-509-certificates

Short & sweet snippet is found in `demo-b/src/main/resources/create_signed.sh`

This will create a bunch of files. The key ones are `localhost.jks`, `localhost-trust.jks`, and `client.jks`.

# Environment variables for security

Point this to where-ever you created your certs

> export CERTS_PATH=...

# Running it

mvn spring-boot:run --projects demo-a

mvn spring-boot:run --projects demo-b

You should see that demo-b was able to fetch the resource offered up by demo-a.

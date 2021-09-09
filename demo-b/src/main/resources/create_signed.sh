export PW=potatoes

################################
################CA

# Create a self signed key pair root CA certificate.
keytool -genkeypair -v \
  -alias localhost-ca \
  -dname "CN=localhost-ca, OU=Example Org, O=Example Company, L=San Potatoe, ST=Texiforas, C=CA" \
  -keystore localhost-ca.jks \
  -keypass:env PW \
  -storepass:env PW \
  -keyalg RSA \
  -keysize 4096 \
  -ext KeyUsage:critical="keyCertSign" \
  -ext BasicConstraints:critical="ca:true" \
  -validity 9999

# Export the localhost-ca public certificate as localhost-ca.crt so that it can be used in trust stores.
keytool -export -v \
  -alias localhost-ca \
  -file localhost-ca.crt \
  -keypass:env PW \
  -storepass:env PW \
  -keystore localhost-ca.jks \
  -rfc
  
################################  
################Server Cert to be signed by CA

# Create a server certificate, tied to localhost
keytool -genkeypair -v \
  -alias localhost \
  -dname "CN=localhost, OU=Example Org, O=Example Company, L=San Potatoe, ST=Texiforas, C=CA" \
  -keystore localhost.jks \
  -keypass:env PW \
  -storepass:env PW \
  -keyalg RSA \
  -keysize 2048 \
  -validity 385

# Create a certificate signing request for localhost
keytool -certreq -v \
  -alias localhost \
  -keypass:env PW \
  -storepass:env PW \
  -keystore localhost.jks \
  -file localhost.csr

# Tell localhost-ca to sign the localhost certificate. Note the extension is on the request, not the
# original certificate.
# Technically, keyUsage should be digitalSignature for DHE or ECDHE, keyEncipherment for RSA.
keytool -gencert -v \
  -alias localhost-ca \
  -keypass:env PW \
  -storepass:env PW \
  -keystore localhost-ca.jks \
  -infile localhost.csr \
  -outfile localhost.crt \
  -ext KeyUsage:critical="digitalSignature,keyEncipherment" \
  -ext EKU="serverAuth" \
  -ext SAN="DNS:localhost" \
  -rfc

# Tell localhost.jks it can trust localhost-ca as a signer.
keytool -import -v \
  -alias localhost-ca \
  -file localhost-ca.crt \
  -keystore localhost.jks \
  -storetype JKS \
  -storepass:env PW << EOF
yes
EOF

# Import the signed certificate back into localhost.jks 
keytool -import -v \
  -alias localhost \
  -file localhost.crt \
  -keystore localhost.jks \
  -storetype JKS \
  -storepass:env PW

# List out the contents of localhost.jks just to confirm it.  
# If you are using Play as a TLS termination point, this is the key store you should present as the server.
keytool -list -v \
  -keystore localhost.jks \
  -storepass:env PW  
  

################################  
################Truststore
  
  
# Create a JKS keystore that trusts the example CA, with the default password.
keytool -import -v \
  -alias localhost \
  -file localhost-ca.crt \
  -keypass:env PW \
  -storepass changeit \
  -keystore localhost-trust.jks << EOF
yes
EOF

# List out the details of the store password.
keytool -list -v \
  -keystore localhost-trust.jks \
  -storepass changeit  
  



################################  
################ Client

# Create a client certificate
keytool -genkeypair -v \
  -alias client \
  -dname "CN=client, OU=Example Org, O=Example Company, L=San Potatoe, ST=Texiforas, C=CA" \
  -keystore client.jks \
  -keypass:env PW \
  -storepass:env PW \
  -keyalg RSA \
  -keysize 2048 \
  -validity 385

# Create a certificate signing request for client
keytool -certreq -v \
  -alias client \
  -keypass:env PW \
  -storepass:env PW \
  -keystore client.jks \
  -file client.csr

# Tell client-ca to sign the client certificate. Note the extension is on the request, not the
# original certificate.
# Technically, keyUsage should be digitalSignature for DHE or ECDHE, keyEncipherment for RSA.
keytool -gencert -v \
  -alias localhost-ca \
  -keypass:env PW \
  -storepass:env PW \
  -keystore localhost-ca.jks \
  -infile client.csr \
  -outfile client.crt \
  -ext KeyUsage:critical="digitalSignature,keyEncipherment" \
  -ext EKU="serverAuth" \
  -ext EKU="clientAuth" \
  -ext SAN="DNS:client" \
  -rfc

# Tell client.jks it can trust localhost-ca as a signer.
keytool -import -v \
  -alias localhost-ca \
  -file localhost-ca.crt \
  -keystore client.jks \
  -storetype JKS \
  -storepass:env PW << EOF
yes
EOF

# Import the signed certificate back into client.jks 
keytool -import -v \
  -alias client \
  -file client.crt \
  -keystore client.jks \
  -storetype JKS \
  -storepass:env PW

# List out the contents of localhost.jks just to confirm it.  
# If you are using Play as a TLS termination point, this is the key store you should present as the server.
keytool -list -v \
  -keystore client.jks \
  -storepass:env PW  

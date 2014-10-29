1. First setup an account GUID if not already done using the GNS CLI (see tutorial at https://www.gns.name/wiki/index.php?title=GNS_CLI_Getting_Started)

>java -jar gns-cli-1.7-2014-04-01.jar
GNS CLI - Connected to 127.0.0.1:8081>account_create cecchet@cs.umass.edu
Created an account with GUID A2D4CBDC6D94A267604E0500DD769A17BAA80F4E. An email has been sent to cecchet@cs.umass.edu with instructions on how to verify the new account.
cecchet@cs.umass.edu is not verified.
GNS CLI - 127.0.0.1:8081|cecchet@cs.umass.edu>account_verify cecchet@cs.umass.edu D154CB9D7395645A683F8FE844E9A3C61FCBA5F0
Account verified.

2. You can also set your default GNS and default accound GUID if you want to use them as defaults for mSockets.

GNS CLI - 127.0.0.1:8081|cecchet@cs.umass.edu>set_default_gns 127.0.0.1 8081
Checking GNS connectivity.
Connected sucesssfully to GNS at 127.0.0.1:8081
Default GNS set to 127.0.0.1:8081

GNS CLI - 127.0.0.1:8081|cecchet@cs.umass.edu>set_default_guid cecchet@cs.umass.edu
Looking up alias cecchet@cs.umass.edu GUID and certificates...
Default GUID set to cecchet@cs.umass.edu
GNS CLI - 127.0.0.1:8081|cecchet@cs.umass.edu>

3. 
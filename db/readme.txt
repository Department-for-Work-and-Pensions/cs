Set location of postgres jar
===============
PGJARLOCATION=$HOME
echo PGJARLOCATION set to $PGJARLOCATION


Setup Liquibase
===============
1) Installation
use brew:  brew install liquibase
or download jar and add it to your path 

2) Validate your installation by running:
  liquibase --url="jdbc:postgresql://localhost:5432/carersservice_db" --classpath=$PGJARLOCATION/postgresql.jar validate

3) If running validate fails try running with debug:
  liquibase --url="jdbc:postgresql://localhost:5432/carersservice_db" --classpath=$PGJARLOCATION/postgresql.jar --logLevel=DEBUG validate


Creating / Updating the SCHEMA
==============================
This will create or update schema and the tables for those schemas.
( By running all .sql and .xml files in this folder listed in dbchangelog.xml )
  liquibase --url="jdbc:postgresql://localhost:5432/carersservice_db" --classpath=$PGJARLOCATION/postgresql.jar --logLevel=DEBUG update


Rolling Back an Update
======================
Say to rollback 007_1 if this was the last change applied.
  liquibase --url="jdbc:postgresql://localhost:5432/carersservice_db" --classpath=$PGJARLOCATION/postgresql.jar --logLevel=DEBUG rollback cs_007_1


Dropping everything - CG SUGGEST THIS WONT WORK
======================
From the command line
  liquibase --url="jdbc:postgresql://localhost:5432/carersservice_db" --classpath=$PGJARLOCATION/postgresql.jar --logLevel=INFO dropAll


Drop and ReCreate the Database
==============================
( May need to stop cs and restart postgres to clear down database connections )
dropdb carersservice_db
createdb carersservice_db
Then apply the liquibase updates as above.
( Will lose any claims in db of course ! )


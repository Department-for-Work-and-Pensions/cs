--liquibase formatted sql

--changeset jmigueis:cs_001_1
DROP SCHEMA IF EXISTS carers CASCADE;
CREATE SCHEMA carers;
--rollback DROP SCHEMA IF EXISTS carers CASCADE;

--changeset jmigueis:cs_001_2
create table carers.claim(transId varchar(7) not null primary key, claimXml varchar(50000) not null);
--rollback DROP TABLE IF EXISTS carers.claim CASCADE;

--changeset jmigueis:cs_001_3
create table carers.claimsummary(transId varchar(7) not null, key varchar(20) not null, value varchar(200) not null, constraint claimsummaryPK primary key (transId, key));
--rollback DROP TABLE IF EXISTS carers.claimsummary CASCADE;

--changeset jmigueis:cs_001_4
create table carers.claimaudit(transId varchar(7) not null, creationDate timestamp not null default current_timestamp, status varchar(50) not null, constraint claimauditPK primary key (transId, creationDate));
--rollback DROP TABLE IF EXISTS carers.claimaudit CASCADE;

--changeset jmigueis:cs_001_5
DROP USER IF EXISTS carersserviceuser;
CREATE USER carersserviceuser PASSWORD 'cs123';
GRANT USAGE on SCHEMA carers to carersserviceuser;
GRANT SELECT,INSERT,UPDATE,DELETE ON carers.claim to carersserviceuser;
GRANT SELECT,INSERT,UPDATE,DELETE ON carers.claimsummary to carersserviceuser; 
GRANT SELECT,INSERT ON carers.claimaudit to carersserviceuser; 
--rollback DROP OWNED by carersserviceuser; DROP USER IF EXISTS carersserviceuser;